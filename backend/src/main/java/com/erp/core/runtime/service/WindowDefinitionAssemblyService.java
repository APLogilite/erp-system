package com.erp.core.runtime.service;

import com.erp.core.runtime.dto.window.FieldDefinitionResponse;
import com.erp.core.runtime.dto.window.FieldDefinitionResponse.ColumnInfo;
import com.erp.core.runtime.dto.window.TabDefinitionResponse;
import com.erp.core.runtime.dto.window.TabDefinitionResponse.TableInfo;
import com.erp.core.runtime.dto.window.WindowDefinitionResponse;
import com.erp.core.runtime.dto.window.WindowDefinitionResponse.WindowInfo;
import com.erp.modules.metadata.entity.SysColumn;
import com.erp.modules.metadata.entity.SysMenu;
import com.erp.modules.metadata.entity.SysTab;
import com.erp.modules.metadata.entity.SysTable;
import com.erp.modules.metadata.entity.SysWindow;
import com.erp.modules.metadata.entity.SysWindowField;
import com.erp.modules.metadata.service.SysColumnService;
import com.erp.modules.metadata.service.SysTableService;
import com.erp.modules.metadata.service.SysWindowFieldService;
import com.erp.modules.metadata.service.SysWindowService;
import com.erp.modules.metadata.service.SysTabService;
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

  public WindowDefinitionAssemblyService(
      SysWindowService windowService,
      SysTabService tabService,
      SysWindowFieldService fieldService,
      SysColumnService columnService,
      SysTableService tableService,
      DynamicCrudService dynamicCrudService) {
    this.windowService = windowService;
    this.tabService = tabService;
    this.fieldService = fieldService;
    this.columnService = columnService;
    this.tableService = tableService;
    this.dynamicCrudService = dynamicCrudService;
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

    // Compute childTabIds for each tab by matching parentColumn naming conventions
    // A tab is a child of another tab when its parentColumn references the parent's table
    // e.g., parentColumn="window_id" → stub="window" → matches table "sys_window"
    for (TabDefinitionResponse tr : tabResponses) {
      List<UUID> childIds = new ArrayList<>();
      String tableName = tr.getTable() != null ? tr.getTable().getName() : null;
      if (tableName != null) {
        for (TabDefinitionResponse other : tabResponses) {
          if (other.getParentColumn() != null && other.getParentColumn().endsWith("_id")) {
            String colStub = other.getParentColumn().substring(0, other.getParentColumn().length() - 3);
            if (tableName.endsWith("_" + colStub)) {
              childIds.add(other.getId());
            }
          }
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
    tabResponse.setParentColumn(tab.getParentColumn());

    // Resolve table info
    Optional<SysTable> tableOpt = tableService.findById(tab.getTableId());
    tableOpt.ifPresent(sysTable -> {
      TableInfo tableInfo = new TableInfo(
          sysTable.getId(),
          sysTable.getTableName(),
          sysTable.getLabel());
      tabResponse.setTable(tableInfo);
    });

    // Load fields for this tab, ordered by seq_no
    List<SysWindowField> fields = fieldService.findByTabIdOrderBySeqNoAsc(tab.getId());
    List<FieldDefinitionResponse> fieldResponses = new ArrayList<>();

    for (SysWindowField field : fields) {
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
      if (column.getRelationTable() != null && !column.getRelationTable().isBlank()) {
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
          + "\" WHERE is_active = true ORDER BY \"" + displayCol + "\" LIMIT 100";
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
