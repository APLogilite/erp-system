package com.erp.core.metadata.dto;

import java.util.UUID;

public class FormFieldDto {

  private UUID id;
  private UUID formId;
  private String columnCode;
  private String labelOverride;
  private Boolean visible;
  private Boolean readOnly;
  private Boolean required;
  private Integer position;
  private String defaultValue;
  private String placeholder;
  private Boolean isActive;

  public FormFieldDto() {}

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public UUID getFormId() {
    return formId;
  }

  public void setFormId(UUID formId) {
    this.formId = formId;
  }

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

  public Boolean getIsActive() {
    return isActive;
  }

  public void setIsActive(Boolean isActive) {
    this.isActive = isActive;
  }
}
