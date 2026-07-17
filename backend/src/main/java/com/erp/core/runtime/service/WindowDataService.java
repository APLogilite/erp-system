package com.erp.core.runtime.service;

import com.erp.core.runtime.dto.window.FieldDefinitionResponse;
import com.erp.core.runtime.dto.window.TabDefinitionResponse;
import com.erp.core.runtime.dto.window.WindowDefinitionResponse;
import com.erp.core.layout.entity.SysTab;
import com.erp.core.layout.service.SysTabService;
import java.math.BigDecimal;
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
   * Resolves FK display names in records and adds a {@code _display} key with the
   * record's own display column value. Uses {@code sys_column.is_display_column}
   * metadata to find the right column for each table.
   */
  private void resolveDisplayNames(List<Map<String, Object>> records, TabDefinitionResponse tab) {
    if (records == null || records.isEmpty()) return;

    // First, try to populate _display on each record from its own display column
    if (tab.getTable() != null && tab.getTable().getName() != null) {
      String displayCol = findDisplayColumnForTable(tab.getTable().getName());
      if (displayCol != null) {
        for (Map<String, Object> rec : records) {
          Object val = rec.get(displayCol);
          if (val != null && !rec.containsKey("_display")) {
            rec.put("_display", val.toString());
          }
        }
      }
    }

    if (tab.getFields() == null) return;

    // Collect FK values grouped by relation table
    java.util.Map<String, java.util.List<UUID>> fkByTable = new java.util.LinkedHashMap<>();
    java.util.Map<String, String> fkToField = new java.util.LinkedHashMap<>();
    for (FieldDefinitionResponse field : tab.getFields()) {
      if (field.getColumn() == null || !"many2one".equals(field.getColumn().getType())) continue;
      String relTable = field.getColumn().getRelationTable();
      String colCode = field.getColumn().getCode();
      if (relTable == null || colCode == null) continue;
      fkToField.put(colCode, colCode + "_display");
      for (Map<String, Object> rec : records) {
        Object val = rec.get(colCode);
        if (val instanceof UUID uuid) {
          fkByTable.computeIfAbsent(relTable, k -> new java.util.ArrayList<>()).add(uuid);
        } else if (val instanceof String s) {
          try { fkByTable.computeIfAbsent(relTable, k -> new java.util.ArrayList<>()).add(UUID.fromString(s));
          } catch (IllegalArgumentException ignored) {}
        }
      }
    }

    if (fkByTable.isEmpty()) return;

    // Find display column for each relation table from sys_column metadata
    java.util.Map<String, String> displayColForTable = new java.util.LinkedHashMap<>();
    for (String tableName : fkByTable.keySet()) {
      displayColForTable.put(tableName, findDisplayColumnForTable(tableName));
    }

    // Resolve FK values to display names
    for (java.util.Map.Entry<String, java.util.List<UUID>> entry : fkByTable.entrySet()) {
      String relTable = entry.getKey();
      String displayCol = displayColForTable.get(relTable);
      if (displayCol == null) continue;

      java.util.List<UUID> ids = entry.getValue().stream().distinct().toList();
      if (ids.isEmpty()) continue;

      String idList = ids.stream().map(u -> "'" + u + "'").collect(java.util.stream.Collectors.joining(","));
      String sql = "SELECT id, \"" + displayCol + "\" AS _display FROM \"" + relTable + "\" WHERE id IN (" + idList + ")";
      try {
        List<Map<String, Object>> refRecords = dynamicCrudService.queryForList(sql);
        java.util.Map<String, String> displayMap = new java.util.LinkedHashMap<>();
        for (Map<String, Object> ref : refRecords) {
          Object refId = ref.get("id");
          Object refDisplay = ref.get("_display");
          if (refId != null && refDisplay != null) {
            displayMap.put(refId.toString(), refDisplay.toString());
          }
        }
        for (java.util.Map.Entry<String, String> fe : fkToField.entrySet()) {
          String fkCol = fe.getKey();
          String displayKey = fe.getValue();
          for (Map<String, Object> rec : records) {
            Object val = rec.get(fkCol);
            if (val != null) {
              String display = displayMap.get(val.toString());
              if (display != null) rec.put(displayKey, display);
            }
          }
        }
      } catch (Exception e) {
        log.warn("Failed to resolve display names from table '{}': {}", relTable, e.getMessage());
      }
    }
  }

  /**
   * Gets filter_where_clause for a lookup — checks field level first, then falls back to column level.
   * Field-level filter overrides column-level default.
   */
  private String getFieldFilterClause(String tableName, UUID tabId, String fieldCode) {
    // 1. Try field-level filter (sys_window_field) — per-instance override
    try {
      String sql = "SELECT swf.filter_where_clause FROM sys_window_field swf "
          + "JOIN sys_column sc ON swf.column_id = sc.id "
          + "WHERE swf.tab_id = '" + tabId + "'::UUID AND sc.code = '" + fieldCode + "' "
          + "AND swf.filter_where_clause IS NOT NULL LIMIT 1";
      List<Map<String, Object>> rows = dynamicCrudService.queryForList(sql);
      if (!rows.isEmpty()) {
        Object val = rows.get(0).get("filter_where_clause");
        if (val != null) return val.toString();
      }
    } catch (Exception e) {
      log.warn("Failed to get field filter for {}.{}: {}", tableName, fieldCode, e.getMessage());
    }

    // 2. Fallback to column-level filter (sys_column) — default for all fields
    try {
      String sql = "SELECT filter_where_clause FROM sys_column WHERE "
          + "table_id = (SELECT id FROM sys_table WHERE name = '" + tableName + "') "
          + "AND code = '" + fieldCode + "' AND filter_where_clause IS NOT NULL LIMIT 1";
      // table_id is UUID compared with subquery result — PostgreSQL handles this correctly
      List<Map<String, Object>> rows = dynamicCrudService.queryForList(sql);
      if (!rows.isEmpty()) {
        Object val = rows.get(0).get("filter_where_clause");
        if (val != null) return val.toString();
      }
    } catch (Exception e) {
      log.warn("Failed to get column filter for {}.{}: {}", tableName, fieldCode, e.getMessage());
    }
    return null;
  }

  /**
   * Resolves @tab.field@ placeholders using the full drill context.
   * drillContext format: "TabName:recordId|TabName:recordId" for all parent levels.
   * The window context (windowId) is used to find the tab by name, then the
   * corresponding record ID from the drill context is used to query the field value.
   */
  private String resolveFilterPlaceholders(String filterClause, UUID windowId, String drillContext) {
    if (filterClause == null || windowId == null || drillContext == null) return null;

    // Parse drill context into a map: tabName → recordId
    java.util.Map<String, String> drillMap = new java.util.LinkedHashMap<>();
    for (String level : drillContext.split("\\|")) {
      String[] parts = level.split(":", 2);
      if (parts.length == 2) drillMap.put(parts[0], parts[1]);
    }

    // Match @tabname.fieldname@ patterns
    java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("@([^.]+)\\.([^@]+)@").matcher(filterClause);
    StringBuffer sb = new StringBuffer();
    while (matcher.find()) {
      String tabName = matcher.group(1);
      String fieldCode = matcher.group(2);
      try {
        // Find the tab by NAME within the current window
        String findTabSql = "SELECT st.table_id FROM sys_tab st "
            + "JOIN sys_window sw ON st.window_id = sw.id "
            + "WHERE sw.id = '" + windowId + "'::UUID AND st.name = '" + tabName + "' LIMIT 1";
        List<Map<String, Object>> tabRows = dynamicCrudService.queryForList(findTabSql);
        if (tabRows.isEmpty()) {
          log.warn("Tab '{}' not found in window {}", tabName, windowId);
          matcher.appendReplacement(sb, "''");
          continue;
        }
        Object tabTableId = tabRows.get(0).get("table_id");
        if (tabTableId == null) { matcher.appendReplacement(sb, "''"); continue; }

        // Get the physical table name for this tab
        String tableName = getPhysicalTableName(UUID.fromString(tabTableId.toString()));
        if (tableName == null) { matcher.appendReplacement(sb, "''"); continue; }

        // Get the parent record ID for this tab from the drill context
        String recordId = drillMap.get(tabName);
        if (recordId == null) {
          log.warn("No drill context for tab '{}'", tabName);
          matcher.appendReplacement(sb, "''");
          continue;
        }

        // Query the parent record to get the field value
        String sql = "SELECT \"" + fieldCode + "\" FROM \"" + tableName + "\" WHERE id = '" + recordId + "'::UUID";
        List<Map<String, Object>> rows = dynamicCrudService.queryForList(sql);
        if (!rows.isEmpty()) {
          Object val = rows.get(0).get(fieldCode);
          String replacement = val != null ? "'" + val.toString() + "'" : "''";
          matcher.appendReplacement(sb, java.util.regex.Matcher.quoteReplacement(replacement));
        } else {
          matcher.appendReplacement(sb, "''");
        }
      } catch (Exception e) {
        log.warn("Failed to resolve placeholder @{}.{}@: {}", tabName, fieldCode, e.getMessage());
        matcher.appendReplacement(sb, "''");
      }
    }
    matcher.appendTail(sb);
    return sb.toString();
  }

  /**
   * Gets the physical table name for a given table UUID from sys_table.
   */
  private String getPhysicalTableName(UUID tableId) {
    try {
      String sql = "SELECT table_name FROM sys_table WHERE id = '" + tableId + "'::UUID";
      List<Map<String, Object>> rows = dynamicCrudService.queryForList(sql);
      if (!rows.isEmpty()) {
        Object val = rows.get(0).get("table_name");
        if (val != null) return val.toString();
      }
    } catch (Exception e) {
      log.warn("Failed to get physical table name for id '{}': {}", tableId, e.getMessage());
    }
    return null;
  }

  /**
   * Queries sys_column for the display column of a given table.
   */
  private String findDisplayColumnForTable(String tableName) {
    try {
      String sql = "SELECT code FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = '"
          + tableName + "') AND is_display_column = true LIMIT 1";
      List<Map<String, Object>> cols = dynamicCrudService.queryForList(sql);
      if (!cols.isEmpty() && cols.get(0).get("code") != null) {
        return cols.get(0).get("code").toString();
      }
    } catch (Exception e) {
      log.warn("Failed to find display column for table '{}': {}", tableName, e.getMessage());
    }
    return null;
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

    Map<String, Object> result = dynamicCrudService.listRecords(
        tableName,
        whereClauseField,
        whereClauseValue,
        tenantId,
        page,
        size,
        sortField,
        sortDir,
        null);

    // Resolve FK display names for many2one fields
    @SuppressWarnings("unchecked")
    List<Map<String, Object>> items = (List<Map<String, Object>>) result.get("items");
    if (items != null) {
      resolveDisplayNames(items, mainTab);
    }
    return result;
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

    // Resolve display names for main record's FK fields
    resolveDisplayNames(java.util.Collections.singletonList(record), mainTab);

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
          // Resolve display names for child records' FK fields
          resolveDisplayNames(children, childTab);
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
      UUID tenantId,
      UUID tabId,
      UUID windowId,
      String fieldCode,
      String drillContext) {

    // Resolve filter_where_clause — supports both static and @tab.field@ placeholders
    String whereField = null;
    String whereValue = null;
    if (tabId != null && fieldCode != null) {
      String filterClause = getFieldFilterClause(tableName, tabId, fieldCode);
      if (filterClause != null) {
        String resolved;
        // Check if filter has @tab.field@ placeholders (needs window + drill context)
        if (filterClause.contains("@") && windowId != null && drillContext != null) {
          resolved = resolveFilterPlaceholders(filterClause, windowId, drillContext);
        } else {
          resolved = filterClause; // Static filter, no placeholder resolution needed
        }
        if (resolved != null) {
          int eqIdx = resolved.indexOf('=');
          if (eqIdx > 0) {
            whereField = resolved.substring(0, eqIdx).trim();
            whereValue = resolved.substring(eqIdx + 1).trim().replaceAll("'", "");
          }
        }
      }
    }

    Map<String, Object> result = dynamicCrudService.listRecords(
        tableName, whereField, whereValue, tenantId, 0, 500, null, null, null);

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
      UUID userId,
      UUID tabId,
      UUID parentRecordId) {

    WindowDefinitionResponse def = windowAssemblyService.assembleDefinition(windowName);
    if (def == null) {
      throw new IllegalArgumentException("Window not found: " + windowName);
    }

    // Determine which tab's table to create in (main tab by default, child tab if tabId provided)
    TabDefinitionResponse targetTab;
    if (tabId != null) {
      targetTab = findTabById(def, tabId);
      if (targetTab == null) {
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

    // Auto-set where clause field value (e.g. order_type = 'sales' for Sales Orders)
    Map<String, String> tabConditions = buildTabConditions(targetTab, null);
    for (Map.Entry<String, String> entry : tabConditions.entrySet()) {
      if (!data.containsKey(entry.getKey())) {
        data.put(entry.getKey(), entry.getValue());
      }
    }

    // Auto-set parent FK when creating a child record from drill-down context
    if (tabId != null && parentRecordId != null) {
      String parentColumn = targetTab.getParentColumn();
      if (parentColumn != null && !parentColumn.isBlank() && !data.containsKey(parentColumn)) {
        data.put(parentColumn, parentRecordId);
      }
    }

    // Coerce string values to proper types based on column metadata
    coerceFieldTypes(data, targetTab);

    // Validate required fields are present before inserting
    if (targetTab.getFields() != null) {
      List<String> missingFields = new ArrayList<>();
      for (FieldDefinitionResponse field : targetTab.getFields()) {
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

    // Coerce string values to proper types based on column metadata
    coerceFieldTypes(data, targetTab);

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
    // Resolve display names for the tab record itself
    resolveDisplayNames(java.util.Collections.singletonList(record), tab);

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
          // Resolve display names for grandchildren too
          resolveDisplayNames(children, childTab);
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

  /**
   * Coerces string values in the data map to the correct Java types based on
   * the column type metadata from the tab's field definitions.
   * This allows the frontend to send raw string values from form inputs without
   * client-side parseInt/parseFloat coercion.
   */
  private void coerceFieldTypes(Map<String, Object> data, TabDefinitionResponse tab) {
    if (tab.getFields() == null) return;
    for (FieldDefinitionResponse field : tab.getFields()) {
      if (field.getColumn() == null) continue;
      String code = field.getColumn().getCode();
      if (!data.containsKey(code)) continue;
      Object val = data.get(code);
      if (val == null || val instanceof Number || val instanceof Boolean) continue;
      String type = field.getColumn().getType();
      if (type == null) continue;
      try {
        if (val instanceof String str) {
          if (str.isBlank()) {
            data.put(code, null);
            continue;
          }
          switch (type) {
            case "integer" -> data.put(code, Integer.parseInt(str.trim()));
            case "decimal", "numeric" -> data.put(code, new BigDecimal(str.trim()));
            case "boolean" -> data.put(code, "true".equalsIgnoreCase(str.trim()) || "1".equals(str.trim()) || "yes".equalsIgnoreCase(str.trim()));
            case "date" -> data.put(code, java.time.LocalDate.parse(str.trim()));
            case "datetime" -> data.put(code, java.time.LocalDateTime.parse(str.trim().replace(" ", "T")));
          }
        }
      } catch (Exception e) {
        log.warn("Failed to coerce field '{}' value '{}' to type '{}': {}", code, val, type, e.getMessage());
      }
    }
  }
}
