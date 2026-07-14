package com.erp.core.runtime.service;

import com.erp.core.runtime.dto.window.TabDefinitionResponse;
import com.erp.core.runtime.dto.window.WindowDefinitionResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Orchestrates CRUD operations on window records using the new
 * Window/Tab/Field metadata schema. This is the replacement for the old
 * RecordCrudService which used the PRD-001 form definition structure.
 *
 * <p>Key behaviors:
 * <ul>
 *   <li>Record list uses the window's main tab (first tab where parent_column IS NULL)</li>
 *   <li>Single record includes main record + child records for each child tab</li>
 *   <li>Tab where_clause is applied to queries automatically</li>
 *   <li>Child tabs use parent_column FK to filter by parent record ID</li>
 *   <li>Tenant isolation is enforced via the underlying DynamicCrudService</li>
 * </ul>
 */
@Service
public class WindowDataService {

  private static final Logger log = LoggerFactory.getLogger(WindowDataService.class);

  private final WindowDefinitionAssemblyService windowAssemblyService;
  private final DynamicCrudService dynamicCrudService;

  public WindowDataService(
      WindowDefinitionAssemblyService windowAssemblyService,
      DynamicCrudService dynamicCrudService) {
    this.windowAssemblyService = windowAssemblyService;
    this.dynamicCrudService = dynamicCrudService;
  }

  /**
   * Returns the main (first) tab from a window definition.
   * The main tab is the one with {@code parent_column IS NULL}.
   */
  private TabDefinitionResponse findMainTab(WindowDefinitionResponse def) {
    if (def.getTabs() == null) {
      return null;
    }
    return def.getTabs().stream()
        .filter(t -> t.getParentColumn() == null || t.getParentColumn().isBlank())
        .findFirst()
        .orElse(null);
  }

  /**
   * Returns child tabs (tabs with a parent_column set).
   */
  private List<TabDefinitionResponse> findChildTabs(WindowDefinitionResponse def) {
    if (def.getTabs() == null) {
      return List.of();
    }
    return def.getTabs().stream()
        .filter(t -> t.getParentColumn() != null && !t.getParentColumn().isBlank())
        .toList();
  }

  /**
   * Gets the physical table name from a tab definition.
   */
  private String getTableName(TabDefinitionResponse tab) {
    if (tab == null || tab.getTable() == null) {
      return null;
    }
    return tab.getTable().getName();
  }

  /**
   * Builds a where-clause condition map from a tab's where_clause and parent_column.
   * The {@code @id@} variable is resolved to the parent record ID for child tabs.
   */
  private Map<String, String> buildTabConditions(TabDefinitionResponse tab, UUID parentRecordId) {
    Map<String, String> conditions = new LinkedHashMap<>();
    if (tab.getWhereClause() != null && !tab.getWhereClause().isBlank()) {
      // Parse simple where_clause expressions like "order_type = 'sales'"
      // For now, handle the common pattern: "field = value" or "field = @id@"
      String where = tab.getWhereClause().trim();
      if (where.contains("= @id@") && parentRecordId != null) {
        String field = where.substring(0, where.indexOf("=")).trim();
        conditions.put(field, parentRecordId.toString());
      } else if (where.contains("=")) {
        String[] parts = where.split("=", 2);
        String field = parts[0].trim();
        String value = parts[1].trim().replaceAll("'", "");
        conditions.put(field, value);
      }
    }
    // For child tabs, add the parent_column FK filter
    if (tab.getParentColumn() != null && !tab.getParentColumn().isBlank() && parentRecordId != null) {
      conditions.put(tab.getParentColumn(), parentRecordId.toString());
    }
    return conditions;
  }

  // ---------------------------------------------------------------
  // Public API
  // ---------------------------------------------------------------

  /**
   * List records from a window's main tab table with pagination.
   */
  @Transactional(readOnly = true)
  public Map<String, Object> listRecords(
      String windowName,
      UUID tenantId,
      int page,
      int size,
      String sortField,
      String sortDir) {

    WindowDefinitionResponse def = windowAssemblyService.assembleDefinition(windowName);
    if (def == null) {
      throw new IllegalArgumentException("Window not found: " + windowName);
    }

    TabDefinitionResponse mainTab = findMainTab(def);
    if (mainTab == null) {
      throw new IllegalArgumentException("Window has no main tab: " + windowName);
    }

    String tableName = getTableName(mainTab);
    if (tableName == null) {
      throw new IllegalArgumentException("Main tab has no associated table: " + windowName);
    }

    // Build where clause conditions from the tab's where_clause
    Map<String, String> tabConditions = buildTabConditions(mainTab, null);
    String whereClauseField = null;
    String whereClauseValue = null;
    if (!tabConditions.isEmpty()) {
      Map.Entry<String, String> entry = tabConditions.entrySet().iterator().next();
      whereClauseField = entry.getKey();
      whereClauseValue = entry.getValue();
    }

    return dynamicCrudService.listRecords(
        tableName,
        whereClauseField,
        whereClauseValue,
        tenantId,
        page,
        size,
        sortField,
        sortDir,
        null);
  }

  /**
   * Get a single record with all child tab records.
   */
  @Transactional(readOnly = true)
  public Map<String, Object> getRecordWithChildren(
      String windowName,
      UUID recordId,
      UUID tenantId) {

    WindowDefinitionResponse def = windowAssemblyService.assembleDefinition(windowName);
    if (def == null) {
      throw new IllegalArgumentException("Window not found: " + windowName);
    }

    TabDefinitionResponse mainTab = findMainTab(def);
    if (mainTab == null) {
      throw new IllegalArgumentException("Window has no main tab: " + windowName);
    }

    String tableName = getTableName(mainTab);
    if (tableName == null) {
      throw new IllegalArgumentException("Main tab has no associated table: " + windowName);
    }

    // Get the main record
    Map<String, Object> record = dynamicCrudService.getRecord(tableName, recordId, tenantId, null);
    if (record == null) {
      return null;
    }

    // Load child tab records
    List<TabDefinitionResponse> childTabs = findChildTabs(def);
    Map<String, Object> childRecords = new LinkedHashMap<>();

    for (TabDefinitionResponse childTab : childTabs) {
      String childTableName = getTableName(childTab);
      if (childTableName == null) {
        continue;
      }

      // Build conditions including parent_column FK filter + where_clause
      Map<String, String> conditions = buildTabConditions(childTab, recordId);

      // Use the parent_column as the FK relation
      String relationColumn = childTab.getParentColumn();
      if (relationColumn != null && !relationColumn.isBlank()) {
        List<Map<String, Object>> children =
            dynamicCrudService.getChildRecords(childTableName, relationColumn, recordId, tenantId);
        childRecords.put(childTab.getName(), children);
      }
    }

    Map<String, Object> result = new LinkedHashMap<>();
    result.put("record", record);
    result.put("childRecords", childRecords);
    return result;
  }

  /**
   * Create a record in the window's main tab table.
   */
  @Transactional
  public Map<String, Object> createRecord(
      String windowName,
      Map<String, Object> data,
      UUID tenantId,
      UUID userId) {

    WindowDefinitionResponse def = windowAssemblyService.assembleDefinition(windowName);
    if (def == null) {
      throw new IllegalArgumentException("Window not found: " + windowName);
    }

    TabDefinitionResponse mainTab = findMainTab(def);
    if (mainTab == null) {
      throw new IllegalArgumentException("Window has no main tab: " + windowName);
    }

    String tableName = getTableName(mainTab);
    if (tableName == null) {
      throw new IllegalArgumentException("Main tab has no associated table: " + windowName);
    }

    // Auto-set where clause field value (e.g. order_type = 'sales' for Sales Orders)
    Map<String, String> tabConditions = buildTabConditions(mainTab, null);
    for (Map.Entry<String, String> entry : tabConditions.entrySet()) {
      if (!data.containsKey(entry.getKey())) {
        data.put(entry.getKey(), entry.getValue());
      }
    }

    return dynamicCrudService.createRecord(tableName, data, tenantId, userId);
  }

  /**
   * Update a record in the window's main tab table.
   */
  @Transactional
  public Map<String, Object> updateRecord(
      String windowName,
      UUID recordId,
      Map<String, Object> data,
      UUID tenantId,
      UUID userId) {

    WindowDefinitionResponse def = windowAssemblyService.assembleDefinition(windowName);
    if (def == null) {
      throw new IllegalArgumentException("Window not found: " + windowName);
    }

    TabDefinitionResponse mainTab = findMainTab(def);
    if (mainTab == null) {
      throw new IllegalArgumentException("Window has no main tab: " + windowName);
    }

    String tableName = getTableName(mainTab);
    if (tableName == null) {
      throw new IllegalArgumentException("Main tab has no associated table: " + windowName);
    }

    return dynamicCrudService.updateRecord(tableName, recordId, data, tenantId, userId, null);
  }

  /**
   * Soft-delete a record from the window's main tab table.
   */
  @Transactional
  public void deleteRecord(String windowName, UUID recordId, UUID tenantId) {

    WindowDefinitionResponse def = windowAssemblyService.assembleDefinition(windowName);
    if (def == null) {
      throw new IllegalArgumentException("Window not found: " + windowName);
    }

    TabDefinitionResponse mainTab = findMainTab(def);
    if (mainTab == null) {
      throw new IllegalArgumentException("Window has no main tab: " + windowName);
    }

    String tableName = getTableName(mainTab);
    if (tableName == null) {
      throw new IllegalArgumentException("Main tab has no associated table: " + windowName);
    }

    dynamicCrudService.deleteRecord(tableName, recordId, tenantId);
  }
}
