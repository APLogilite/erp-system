package com.erp.core.metadata.dto;

public class FormFieldCreateRequest {

  private String columnCode;
  private String labelOverride;
  private Boolean visible;
  private Boolean readOnly;
  private Boolean required;
  private Integer position;
  private String defaultValue;
  private String placeholder;

  public FormFieldCreateRequest() {}

  public String getColumnCode() {
    return columnCode;
  }

  public void setColumnCode(String columnCode) {
    this.columnCode = columnCode;
  }

  public String getLabelOverride() {
    return labelOverride;
  }

  public void setLabelOverride(String labelOverride) {
    this.labelOverride = labelOverride;
  }

  public Boolean getVisible() {
    return visible;
  }

  public void setVisible(Boolean visible) {
    this.visible = visible;
  }

  public Boolean getReadOnly() {
    return readOnly;
  }

  public void setReadOnly(Boolean readOnly) {
    this.readOnly = readOnly;
  }

  public Boolean getRequired() {
    return required;
  }

  public void setRequired(Boolean required) {
    this.required = required;
  }

  public Integer getPosition() {
    return position;
  }

  public void setPosition(Integer position) {
    this.position = position;
  }

  public String getDefaultValue() {
    return defaultValue;
  }

  public void setDefaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
  }

  public String getPlaceholder() {
    return placeholder;
  }

  public void setPlaceholder(String placeholder) {
    this.placeholder = placeholder;
  }
}
