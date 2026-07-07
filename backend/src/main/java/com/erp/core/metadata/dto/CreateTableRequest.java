package com.erp.core.metadata.dto;

import java.util.List;

public class CreateTableRequest {
  private String code;
  private String label;
  private String pluralLabel;
  private String description;
  private String tableName;
  private List<CreateColumnRequest> columns;

  public CreateTableRequest() {}

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
  public List<CreateColumnRequest> getColumns() { return columns; }
  public void setColumns(List<CreateColumnRequest> columns) { this.columns = columns; }
}
