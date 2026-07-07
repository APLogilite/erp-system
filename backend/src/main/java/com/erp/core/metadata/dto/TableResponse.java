package com.erp.core.metadata.dto;

import java.util.List;
import java.util.UUID;

public class TableResponse {
  private UUID id;
  private String code;
  private String label;
  private String pluralLabel;
  private String description;
  private String tableName;
  private String tableType;
  private Boolean isActive;
  private List<TableColumnDto> columns;

  public TableResponse() {}

  public UUID getId() { return id; }
  public void setId(UUID id) { this.id = id; }
  public String getCode() { return code; }
  public void setCode(String code) { this.code = code; }
  public String getLabel() { return label; }
  public void setLabel(String label) { this.label = label; }
  public String getPluralLabel() { return pluralLabel; }
  public void setPluralLabel(String pluralLabel) { this.pluralLabel = pluralLabel; }
  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }
  public String getTableName() { return tableName; }
  public void setTableName(String tableName) { this.tableName = tableName; }
  public String getTableType() { return tableType; }
  public void setTableType(String tableType) { this.tableType = tableType; }
  public Boolean getIsActive() { return isActive; }
  public void setIsActive(Boolean isActive) { this.isActive = isActive; }
  public List<TableColumnDto> getColumns() { return columns; }
  public void setColumns(List<TableColumnDto> columns) { this.columns = columns; }
}
