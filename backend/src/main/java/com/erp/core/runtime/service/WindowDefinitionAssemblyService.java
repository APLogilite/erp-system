package com.erp.core.runtime.service;

import com.erp.core.runtime.dto.window.FieldDefinitionResponse;
import com.erp.core.runtime.dto.window.FieldDefinitionResponse.ColumnInfo;
import com.erp.core.runtime.dto.window.TabDefinitionResponse;
import com.erp.core.runtime.dto.window.TabDefinitionResponse.TableInfo;
import com.erp.core.runtime.dto.window.WindowDefinitionResponse;
import com.erp.core.runtime.dto.window.WindowDefinitionResponse.WindowInfo;
import com.erp.core.layout.entity.SysColumn;
import com.erp.core.layout.entity.SysMenu;
import com.erp.core.layout.entity.SysTab;
import com.erp.core.layout.entity.SysTable;
import com.erp.core.layout.entity.SysWindow;
import com.erp.core.layout.entity.SysWindowField;
import com.erp.core.layout.service.SysColumnService;
import com.erp.core.layout.service.SysTableService;
import com.erp.core.layout.service.SysWindowFieldService;
import com.erp.core.layout.service.SysWindowService;
import com.erp.core.layout.service.SysTabService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Assembles the full window definition bundle from the metadata entities.
 * This is the replacement for the old FormDefinitionAssemblyService.
 */
@Service
public class WindowDefinitionAssemblyService {

  private static final Logger log = LoggerFactory.getLogger(WindowDefinitionAssemblyService.class);

  private final SysWindowService windowService;
  private final SysTabService tabService;
  private final SysWindowFieldService fieldService;
  private final SysColumnService columnService;
  private final SysTableService tableService;
  private final DynamicCrudService dynamicCrudService;
  private final com.erp.core.layout.repository.SysColumnRepository sysColumnRepository;

  public WindowDefinitionAssemblyService(
      SysWindowService windowService,
      SysTabService tabService,
      SysWindowFieldService fieldService,
      SysColumnService columnService,
      SysTableService tableService,
      DynamicCrudService dynamicCrudService,
      com.erp.core.layout.repository.SysColumnRepository sysColumnRepository) {
    this.windowService = windowService;
    this.tabService = tabService;
    this.fieldService = fieldService;
    this.columnService = columnService;
    this.tableService = tableService;
    this.dynamicCrudService = dynamicCrudService;
    this.sysColumnRepository = sysColumnRepository;
  }

  /**
   * Assembles the full window definition for the given window name.
   * Returns null if the window is not found.
   */
  @Transactional(readOnly = true)
  public WindowDefinitionResponse assembleDefinition(String windowName) {
    Optional<SysWindow> windowOpt = windowService.findByName(windowName);
    if (windowOpt.isEmpty()) {
      return null;
    }

    SysWindow window = windowOpt.get();
    WindowInfo windowInfo = new WindowInfo(
        window.getId(),
        window.getName(),
        window.getTableId(),
        window.getDescription());

    // Load all tabs for this window, ordered by seq_no
    List<SysTab> tabs = tabService.findByWindowIdOrderBySeqNoAsc(window.getId());
    List<TabDefinitionResponse> tabResponses = new ArrayList<>();

    for (SysTab tab : tabs) {
      tabResponses.add(assembleTab(tab));
    }

    // Compute childTabIds using reference-based resolution via sys_column.relation_table.
    // For each tab with parentLinkColumnId set, load the sys_column to get relation_table,
    // then find the parent tab whose table.name matches that relation_table.
    for (TabDefinitionResponse tr : tabResponses) {
      List<UUID> childIds = new ArrayList<>();
      for (TabDefinitionResponse candidate : tabResponses) {
        if (candidate.getParentLinkColumnId() == null) continue;
        // Load the sys_column by UUID to get its relation_table
        java.util.Optional<SysColumn> colOpt = sysColumnRepository.findById(candidate.getParentLinkColumnId());
        if (colOpt.isEmpty()) continue;
        SysColumn col = colOpt.get();
        String relationTable = col.getRelationTable();
        if (relationTable == null || relationTable.isBlank()) continue;
        // Check if this candidate's relation_table matches the current tab's table name
        String currentTableName = tr.getTable() != null ? tr.getTable().getName() : null;
        if (relationTable.equals(currentTableName)) {
          childIds.add(candidate.getId());
        }
      }
      tr.setChildTabIds(childIds);
    }

    return new WindowDefinitionResponse(windowInfo, tabResponses);
  }

  /**
   * Assembles a single tab response including table info and fields.
   */
  public TabDefinitionResponse assembleTab(SysTab tab) {
    TabDefinitionResponse tabResponse = new TabDefinitionResponse();
    tabResponse.setId(tab.getId());
    tabResponse.setName(tab.getName());
    tabResponse.setSeqNo(tab.getSeqNo());
    tabResponse.setIsSingleRow(tab.getIsSingleRow());
    tabResponse.setWhereClause(tab.getWhereClause());
    tabResponse.setParentLinkColumnId(tab.getParentLinkColumnId());

    // Resolve table info
    Optional<SysTable> tableOpt = tableService.findById(tab.getTableId());
    tableOpt.ifPresent(sysTable -> {
      TableInfo tableInfo = new TableInfo(
          sysTable.getId(),
          sysTable.getTableName(),
          sysTable.getLabel());
      tabResponse.setTable(tableInfo);
    });

    // Load fields for this tab, ordered by seq_no, excluding non-displayed fields
    List<SysWindowField> fields = fieldService.findByTabIdOrderBySeqNoAsc(tab.getId());
    List<FieldDefinitionResponse> fieldResponses = new ArrayList<>();

    // Exclude fields where isDisplayed is explicitly false (backend pre-filters)
    for (SysWindowField field : fields) {
      if (Boolean.FALSE.equals(field.getIsDisplayed())) {
        continue;
      }
      fieldResponses.add(assembleField(field));
    }

    tabResponse.setFields(fieldResponses);
    return tabResponse;
  }

  /**
   * Assembles a single field response including column metadata.
   */
  private FieldDefinitionResponse assembleField(SysWindowField field) {
    FieldDefinitionResponse fieldResponse = new FieldDefinitionResponse();
    fieldResponse.setId(field.getId());
    fieldResponse.setSeqNo(field.getSeqNo());
    fieldResponse.setIsSameLine(field.getIsSameLine());
    fieldResponse.setNumLines(field.getNumLines());
    fieldResponse.setColumnWidth(field.getColumnWidth());
    fieldResponse.setIsDisplayed(field.getIsDisplayed());
    fieldResponse.setIsReadonly(field.getIsReadonly());
    fieldResponse.setIsMandatory(field.getIsMandatory());
    fieldResponse.setDisplayLogic(field.getDisplayLogic());
    fieldResponse.setReadonlyLogic(field.getReadonlyLogic());
    fieldResponse.setDefaultValue(field.getDefaultValue());
    fieldResponse.setLabelOverride(field.getLabelOverride());
    fieldResponse.setFilterWhereClause(field.getFilterWhereClause());

    // Pre-resolve the display label: labelOverride ?? column.label
    // labelOverride takes priority; if null, fall back to column's default label
    if (field.getLabelOverride() != null && !field.getLabelOverride().isBlank()) {
      fieldResponse.setLabel(field.getLabelOverride());
    }

    // Resolve column info (needed for column.label fallback)
    Optional<SysColumn> columnOpt = columnService.findById(field.getColumnId());
    columnOpt.ifPresent(column -> {
      ColumnInfo columnInfo = new ColumnInfo();
      columnInfo.setCode(column.getCode());
      columnInfo.setLabel(column.getLabel());
      columnInfo.setType(column.getType());
      columnInfo.setHtmlType(mapToHtmlType(column.getType()));
      columnInfo.setRequired(column.getRequired());
      columnInfo.setMaxLength(column.getMaxLength());
      columnInfo.setPrecision(column.getPrecision());
      columnInfo.setScale(column.getScale());
      columnInfo.setRelationTable(column.getRelationTable());
      columnInfo.setEnumOptions(column.getEnumOptions());
      columnInfo.setFilterWhereClause(column.getFilterWhereClause());
      // Populate lookupOptions for fields with a relationTable
      // Skip eager loading if there's a filter_where_clause (field-level or column-level)
      // — the frontend will fall back to the dynamic fetchLookupRecords API for filtered lookups
      String fieldFilter = field.getFilterWhereClause();
      String columnFilter = column.getFilterWhereClause();
      boolean hasFilter = (fieldFilter != null && !fieldFilter.isBlank())
          || (columnFilter != null && !columnFilter.isBlank());
      if (column.getRelationTable() != null && !column.getRelationTable().isBlank() && !hasFilter) {
        columnInfo.setLookupOptions(fetchLookupOptions(column.getRelationTable()));
      }
      fieldResponse.setColumn(columnInfo);
      // Fallback to column's default label only if no labelOverride was set
      if (fieldResponse.getLabel() == null) {
        fieldResponse.setLabel(column.getLabel());
      }
    });

    return fieldResponse;
  }

  /**
   * Maps a backend column type to an HTML input type for frontend rendering.
   */
  private String mapToHtmlType(String columnType) {
    if (columnType == null) return "text";
    return switch (columnType) {
      case "integer", "numeric" -> "number";
      case "decimal" -> "number";
      case "date" -> "date";
      case "datetime" -> "datetime-local";
      case "boolean" -> "checkbox";
      case "enum" -> "select";
      case "many2one" -> "select";
      case "text" -> "textarea";
      default -> "text";
    };
  }

  /**
   * Fetches lookup options (id, label pairs) for a given relation table.
   * Queries the display column from sys_column metadata and returns up to 100 results.
   */
  private List<Map<String, Object>> fetchLookupOptions(String relationTable) {
    try {
      // Find the display column for this relation table
      String displayCol = findDisplayColumnForTable(relationTable);
      if (displayCol == null) {
        log.debug("No display column found for lookup table '{}'", relationTable);
        return List.of();
      }

      String sql = "SELECT id, \"" + displayCol + "\" AS label FROM \"" + relationTable
          + "\" ORDER BY \"" + displayCol + "\" LIMIT 100";
      return dynamicCrudService.queryForList(sql);
    } catch (Exception e) {
      log.warn("Failed to load lookup options for table '{}': {}", relationTable, e.getMessage());
      return List.of();
    }
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
}
