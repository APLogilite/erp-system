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
import java.util.List;
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

  public WindowDefinitionAssemblyService(
      SysWindowService windowService,
      SysTabService tabService,
      SysWindowFieldService fieldService,
      SysColumnService columnService,
      SysTableService tableService) {
    this.windowService = windowService;
    this.tabService = tabService;
    this.fieldService = fieldService;
    this.columnService = columnService;
    this.tableService = tableService;
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

    // Resolve column info (needed before we can pre-resolve the label)
    Optional<SysColumn> columnOpt = columnService.findById(field.getColumnId());
    columnOpt.ifPresent(column -> {
      ColumnInfo columnInfo = new ColumnInfo();
      columnInfo.setCode(column.getCode());
      columnInfo.setLabel(column.getLabel());
      columnInfo.setType(column.getType());
      columnInfo.setRequired(column.getRequired());
      columnInfo.setMaxLength(column.getMaxLength());
      columnInfo.setPrecision(column.getPrecision());
      columnInfo.setScale(column.getScale());
      columnInfo.setRelationTable(column.getRelationTable());
      columnInfo.setEnumOptions(column.getEnumOptions());
      fieldResponse.setColumn(columnInfo);
      // Pre-resolve the display label: labelOverride ?? column.label
      // Frontend uses this directly — no labelOverride vs column.label logic needed
      if (fieldResponse.getLabel() == null) {
        fieldResponse.setLabel(column.getLabel());
      }
    });
    if (fieldResponse.getLabel() == null && field.getLabelOverride() != null) {
      fieldResponse.setLabel(field.getLabelOverride());
    }

    return fieldResponse;
  }
}
