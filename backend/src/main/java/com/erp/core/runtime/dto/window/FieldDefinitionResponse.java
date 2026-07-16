package com.erp.core.runtime.dto.window;

import java.util.UUID;

/**
 * Represents a single field within a tab definition response,
 * including its column type metadata.
 */
public class FieldDefinitionResponse {

  private UUID id;
  private Integer seqNo;
  private Boolean isSameLine;
  private Integer numLines;
  private Integer columnWidth;
  private Boolean isDisplayed;
  private Boolean isReadonly;
  private Boolean isMandatory;
  private String displayLogic;
  private String readonlyLogic;
  private String defaultValue;
  private String labelOverride;
  private String label;  // Pre-resolved: labelOverride ?? column.label
  private String filterWhereClause;
  private ColumnInfo column;

  public FieldDefinitionResponse() {}

  public UUID getId() { return id; }
  public void setId(UUID id) { this.id = id; }
  public Integer getSeqNo() { return seqNo; }
  public void setSeqNo(Integer seqNo) { this.seqNo = seqNo; }
  public Boolean getIsSameLine() { return isSameLine; }
  public void setIsSameLine(Boolean isSameLine) { this.isSameLine = isSameLine; }
  public Integer getNumLines() { return numLines; }
  public void setNumLines(Integer numLines) { this.numLines = numLines; }
  public Integer getColumnWidth() { return columnWidth; }
  public void setColumnWidth(Integer columnWidth) { this.columnWidth = columnWidth; }
  public Boolean getIsDisplayed() { return isDisplayed; }
  public void setIsDisplayed(Boolean isDisplayed) { this.isDisplayed = isDisplayed; }
  public Boolean getIsReadonly() { return isReadonly; }
  public void setIsReadonly(Boolean isReadonly) { this.isReadonly = isReadonly; }
  public Boolean getIsMandatory() { return isMandatory; }
  public void setIsMandatory(Boolean isMandatory) { this.isMandatory = isMandatory; }
  public String getDisplayLogic() { return displayLogic; }
  public void setDisplayLogic(String displayLogic) { this.displayLogic = displayLogic; }
  public String getReadonlyLogic() { return readonlyLogic; }
  public void setReadonlyLogic(String readonlyLogic) { this.readonlyLogic = readonlyLogic; }
  public String getDefaultValue() { return defaultValue; }
  public void setDefaultValue(String defaultValue) { this.defaultValue = defaultValue; }
  public String getLabelOverride() { return labelOverride; }
  public void setLabelOverride(String labelOverride) { this.labelOverride = labelOverride; }
  public String getLabel() { return label; }
  public void setLabel(String label) { this.label = label; }
  public String getFilterWhereClause() { return filterWhereClause; }
  public void setFilterWhereClause(String filterWhereClause) { this.filterWhereClause = filterWhereClause; }
  public ColumnInfo getColumn() { return column; }
  public void setColumn(ColumnInfo column) { this.column = column; }

  /**
   * Inner DTO for column type metadata.
   */
  public static class ColumnInfo {
    private String code;
    private String label;
    private String type;
    private Boolean required;
    private Integer maxLength;
    private Integer precision;
    private Integer scale;
    private String relationTable;
    private String enumOptions;
    private String filterWhereClause;

    public ColumnInfo() {}

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Boolean getRequired() { return required; }
    public void setRequired(Boolean required) { this.required = required; }
    public Integer getMaxLength() { return maxLength; }
    public void setMaxLength(Integer maxLength) { this.maxLength = maxLength; }
    public Integer getPrecision() { return precision; }
    public void setPrecision(Integer precision) { this.precision = precision; }
    public Integer getScale() { return scale; }
    public void setScale(Integer scale) { this.scale = scale; }
    public String getRelationTable() { return relationTable; }
    public void setRelationTable(String relationTable) { this.relationTable = relationTable; }
    public String getEnumOptions() { return enumOptions; }
    public void setEnumOptions(String enumOptions) { this.enumOptions = enumOptions; }
    public String getFilterWhereClause() { return filterWhereClause; }
    public void setFilterWhereClause(String filterWhereClause) { this.filterWhereClause = filterWhereClause; }
  }
}
