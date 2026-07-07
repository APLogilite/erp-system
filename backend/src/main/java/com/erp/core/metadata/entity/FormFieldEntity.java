package com.erp.core.metadata.entity;

import com.erp.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "sys_form_fields")
public class FormFieldEntity extends BaseEntity {

  @Column(name = "form_id", nullable = false)
  private java.util.UUID formId;

  @Column(name = "column_code", nullable = false, length = 100)
  private String columnCode;

  @Column(name = "label_override", length = 200)
  private String labelOverride;

  @Column(name = "visible")
  private Boolean visible = true;

  @Column(name = "read_only")
  private Boolean readOnly = false;

  @Column(name = "required")
  private Boolean required = false;

  @Column(name = "position", nullable = false)
  private Integer position;

  @Column(name = "default_value", columnDefinition = "TEXT")
  private String defaultValue;

  @Column(name = "placeholder", length = 255)
  private String placeholder;

  public java.util.UUID getFormId() {
    return formId;
  }

  public void setFormId(java.util.UUID formId) {
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
}
