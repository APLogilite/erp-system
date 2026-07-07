package com.erp.core.metadata.dto;

import java.util.Map;

public class TableColumnCreateRequest {

  private String code;
  private String label;
  private String type;
  private Boolean required;
  private String defaultValue;
  private Integer maxLength;
  private Integer precision;
  private Integer scale;
  private String relationTable;
  private Map<String, Object> enumOptions;
  private Integer position;

  public TableColumnCreateRequest() {}

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Boolean getRequired() {
    return required;
  }

  public void setRequired(Boolean required) {
    this.required = required;
  }

  public String getDefaultValue() {
    return defaultValue;
  }

  public void setDefaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
  }

  public Integer getMaxLength() {
    return maxLength;
  }

  public void setMaxLength(Integer maxLength) {
    this.maxLength = maxLength;
  }

  public Integer getPrecision() {
    return precision;
  }

  public void setPrecision(Integer precision) {
    this.precision = precision;
  }

  public Integer getScale() {
    return scale;
  }

  public void setScale(Integer scale) {
    this.scale = scale;
  }

  public String getRelationTable() {
    return relationTable;
  }

  public void setRelationTable(String relationTable) {
    this.relationTable = relationTable;
  }

  public Map<String, Object> getEnumOptions() {
    return enumOptions;
  }

  public void setEnumOptions(Map<String, Object> enumOptions) {
    this.enumOptions = enumOptions;
  }

  public Integer getPosition() {
    return position;
  }

  public void setPosition(Integer position) {
    this.position = position;
  }
}
