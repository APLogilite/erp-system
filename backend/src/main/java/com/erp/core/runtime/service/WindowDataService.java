package com.erp.core.runtime.service;

import com.erp.core.runtime.dto.window.FieldDefinitionResponse;
import com.erp.core.runtime.dto.window.TabDefinitionResponse;
import com.erp.core.runtime.dto.window.WindowDefinitionResponse;
import com.erp.modules.metadata.entity.SysTab;
import com.erp.modules.metadata.service.SysTabService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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

  private final SysTabService sysTabService;

  public WindowDataService(
      WindowDefinitionAssemblyService windowAssemblyService,
      DynamicCrudService dynamicCrudService,
      SysTabService sysTabService) {
    this.windowAssemblyService = windowAssemblyService;
    this.dynamicCrudService = dynamicCrudService;
    this.sysTabService = sysTabService;
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

  /**
   * Finds a tab by its UUID in the window definition.
   */
  private TabDefinitionResponse findTabById(WindowDefinitionResponse def, UUID tabId) {
    if (def.getTabs() == null || tabId == null) return null;
    return def.getTabs().stream()
        .filter(t -> tabId.equals(t.getId()))
        .findFirst()
        .orElse(null);
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

    // Load child tab records (each child processed independently — failures don't block others)
    List<TabDefinitionResponse> childTabs = findChildTabs(def);
    Map<String, Object> childRecords = new LinkedHashMap<>();

    for (TabDefinitionResponse childTab : childTabs) {
      String childTableName = getTableName(childTab);
      if (childTableName == null) {
        log.warn("Child tab '{}' has no table, skipping", childTab.getName());
        continue;
      }

      // Build conditions including parent_column FK filter + where_clause
      Map<String, String> conditions = buildTabConditions(childTab, recordId);

      // Remove the parent_column FK from conditions (it's passed separately to getChildRecords)
      String relationColumn = childTab.getParentColumn();
      conditions.remove(relationColumn);

      if (relationColumn != null && !relationColumn.isBlank()) {
        try {
          List<Map<String, Object>> children =
              dynamicCrudService.getChildRecords(childTableName, relationColumn, recordId, tenantId, conditions);
          childRecords.put(childTab.getName(), children);
          log.debug("Loaded {} child records for tab '{}' from table '{}'", children.size(), childTab.getName(), childTableName);
        } catch (Exception e) {
          log.error("Failed to load child records for tab '{}' from table '{}': {}", childTab.getName(), childTableName, e.getMessage());
          // Continue with other child tabs — one failure shouldn't block all children
          childRecords.put(childTab.getName(), java.util.Collections.emptyList());
        }
      } else {
        log.warn("Child tab '{}' has no parentColumn, skipping", childTab.getName());
      }
    }

    Map<String, Object> result = new LinkedHashMap<>();
    result.put("record", record);
    result.put("childRecords", childRecords);
    return result;
  }

  /**
   * Lookup records from a table for dropdown/autocomplete use.
   * Returns all active records up to 500 with id + resolved display label.
   */
  @Transactional(readOnly = true)
  public List<Map<String, Object>> lookupRecords(
      String tableName,
      UUID tenantId) {

    Map<String, Object> result = dynamicCrudService.listRecords(
        tableName, null, null, tenantId, 0, 500, null, null, null);

    @SuppressWarnings("unchecked")
    List<Map<String, Object>> items = (List<Map<String, Object>>) result.get("items");
    if (items == null) return List.of();

    // Build display labels from known name-like columns
    Set<String> labelCols = Set.of("name", "code", "order_number", "invoice_number",
        "payment_number", "shipment_number", "receipt_number", "title", "label");

    for (Map<String, Object> item : items) {
      String display = "";
      for (String col : labelCols) {
        if (item.containsKey(col) && item.get(col) != null) {
          display = item.get(col).toString();
          break;
        }
      }
      if (display.isBlank() && item.containsKey("id")) {
        display = item.get("id").toString();
      }
      item.put("_display", display);
    }

    return items;
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

    // Validate required fields are present before inserting
    if (mainTab.getFields() != null) {
      List<String> missingFields = new ArrayList<>();
      for (FieldDefinitionResponse field : mainTab.getFields()) {
        String colCode = field.getColumn() != null ? field.getColumn().getCode() : null;
        if (colCode == null) continue;

        // Skip system columns that are auto-set by DynamicCrudService
        if (DynamicCrudService.SYSTEM_COLUMNS.contains(colCode)) continue;

        // Skip fields that are auto-set by where_clause above
        if (tabConditions.containsKey(colCode)) continue;

        boolean isRequired = Boolean.TRUE.equals(field.getIsMandatory())
            || Boolean.TRUE.equals(field.getColumn().getRequired());

        if (isRequired) {
          Object val = data.get(colCode);
          if (val == null || val.toString().isBlank()) {
            String label = field.getLabelOverride() != null
                ? field.getLabelOverride()
                : (field.getColumn() != null ? field.getColumn().getLabel() : colCode);
            missingFields.add(label);
          }
        }
      }
      if (!missingFields.isEmpty()) {
        throw new IllegalArgumentException(
            "Required fields are missing: " + String.join(", ", missingFields));
      }
    }

    return dynamicCrudService.createRecord(tableName, data, tenantId, userId);
  }

  /**
   * Update a record — either in the main tab's table (default) or in a specific
   * child tab's table (when {@code tabId} is provided, e.g. for drill-down edits).
   */
  @Transactional
  public Map<String, Object> updateRecord(
      String windowName,
      UUID recordId,
      Map<String, Object> data,
      UUID tenantId,
      UUID userId,
      UUID tabId) {

    WindowDefinitionResponse def = windowAssemblyService.assembleDefinition(windowName);
    if (def == null) {
      throw new IllegalArgumentException("Window not found: " + windowName);
    }

    // Determine which tab's table to update (main tab by default, child tab if tabId provided)
    TabDefinitionResponse targetTab;
    if (tabId != null) {
      targetTab = findTabById(def, tabId);
      if (targetTab == null) {
        // Fallback: direct DB lookup
        Optional<SysTab> sysTabOpt = sysTabService.findById(tabId);
        if (sysTabOpt.isPresent() && sysTabOpt.get().getWindowId().equals(def.getWindow().getId())) {
          targetTab = windowAssemblyService.assembleTab(sysTabOpt.get());
        } else {
          throw new IllegalArgumentException("Tab not found in window: " + tabId);
        }
      }
    } else {
      targetTab = findMainTab(def);
      if (targetTab == null) {
        throw new IllegalArgumentException("Window has no main tab: " + windowName);
      }
    }

    String tableName = getTableName(targetTab);
    if (tableName == null) {
      throw new IllegalArgumentException("Tab has no associated table: " + targetTab.getName());
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

  /**
   * Fetches a record from a specific tab's table (not just the main tab).
   * Used for drill-down navigation through the tab hierarchy.
   *
   * @param windowName  the window name (for definition context)
   * @param tabId       the tab UUID whose table contains the record
   * @param recordId    the record UUID to fetch
   * @param tenantId    the current tenant ID
   * @param childTabIds UUIDs of child tabs whose records should also be fetched
   * @return map with "record" and "childRecords" keys, or null if record not found
   */
  @Transactional(readOnly = true)
  public Map<String, Object> getTabRecordWithChildren(
      String windowName,
      UUID tabId,
      UUID recordId,
      UUID tenantId,
      List<UUID> childTabIds) {

    WindowDefinitionResponse def = windowAssemblyService.assembleDefinition(windowName);
    if (def == null) {
      throw new IllegalArgumentException("Window not found: " + windowName);
    }

    TabDefinitionResponse tab = findTabById(def, tabId);
    if (tab == null) {
      // Log diagnostic info
      log.warn("Tab ID {} not found in window '{}'. Window has {} tabs: [{}]",
          tabId, windowName,
          def.getTabs() != null ? def.getTabs().size() : 0,
          def.getTabs() != null
              ? def.getTabs().stream().map(t -> t.getId() + ":" + t.getName()).collect(java.util.stream.Collectors.joining(", "))
              : "null");

      // Fallback: try to find the tab directly from the database and assemble on the fly
      Optional<SysTab> sysTabOpt = sysTabService.findById(tabId);
      if (sysTabOpt.isPresent()) {
        SysTab sysTab = sysTabOpt.get();
        // Re-check: does this tab belong to this window?
        if (sysTab.getWindowId().equals(def.getWindow().getId())) {
          tab = windowAssemblyService.assembleTab(sysTab);
          log.info("Fallback: found tab '{}' via direct DB lookup, assembled on the fly", sysTab.getName());
        } else {
          log.error("Tab {} belongs to window {} but current window is {}", tabId, sysTab.getWindowId(), def.getWindow().getId());
          throw new IllegalArgumentException("Tab " + tabId + " does not belong to window: " + windowName);
        }
      } else {
        log.error("Tab UUID {} not found in sys_tab table at all. Window '{}' has {} tabs in def: {}",
            tabId, windowName,
            def.getTabs() != null ? def.getTabs().size() : 0,
            def.getTabs() != null
                ? def.getTabs().stream().map(t -> t.getName() + "(" + t.getId() + ")").collect(java.util.stream.Collectors.joining(", "))
                : "null");
        throw new IllegalArgumentException("Tab not found in window '" + windowName + "': " + tabId);
      }
    }

    String tableName = getTableName(tab);
    if (tableName == null) {
      throw new IllegalArgumentException("Tab has no associated table: " + (tab != null ? tab.getName() : tabId.toString()));
    }

    // Fetch the record
    Map<String, Object> record = dynamicCrudService.getRecord(tableName, recordId, tenantId, null);
    if (record == null) {
      return null;
    }

    // Fetch child records for each child tab (failures don't block others)
    Map<String, Object> childRecords = new LinkedHashMap<>();
    if (childTabIds != null) {
      for (UUID childTabId : childTabIds) {
        TabDefinitionResponse childTab = findTabById(def, childTabId);
        if (childTab == null) continue;

        String childTableName = getTableName(childTab);
        if (childTableName == null) continue;

        String relationColumn = childTab.getParentColumn();
        if (relationColumn == null || relationColumn.isBlank()) continue;

        // Build conditions including where_clause
        Map<String, String> conditions = buildTabConditions(childTab, recordId);
        // Remove the parent_column FK (passed separately)
        conditions.remove(relationColumn);
        try {
          List<Map<String, Object>> children =
              dynamicCrudService.getChildRecords(childTableName, relationColumn, recordId, tenantId, conditions);
          childRecords.put(childTab.getName(), children);
          log.debug("Loaded {} child records for tab '{}' from table '{}'", children.size(), childTab.getName(), childTableName);
        } catch (Exception e) {
          log.error("Failed to load child records for tab '{}' from table '{}': {}", childTab.getName(), childTableName, e.getMessage());
          childRecords.put(childTab.getName(), java.util.Collections.emptyList());
        }
      }
    }

    Map<String, Object> result = new LinkedHashMap<>();
    result.put("record", record);
    result.put("childRecords", childRecords);
    return result;
  }
}
