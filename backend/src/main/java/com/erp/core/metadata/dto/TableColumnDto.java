package com.erp.core.metadata.dto;

import java.util.Map;
import java.util.UUID;

public class TableColumnDto {

  private UUID id;
  private UUID tableId;
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
  private Boolean isActive;

  public TableColumnDto() {}

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public UUID getTableId() {
    return tableId;
  }

  public void setTableId(UUID tableId) {
    this.tableId = tableId;
  }

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

  public Boolean getIsActive() {
    return isActive;
  }

  public void setIsActive(Boolean isActive) {
    this.isActive = isActive;
  }
}
