package com.erp.core.metadata.entity;

import com.erp.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "sys_form_field_validations")
public class FormFieldValidationEntity extends BaseEntity {

  @Column(name = "field_id", nullable = false)
  private java.util.UUID fieldId;

  @Column(name = "type", nullable = false, length = 50)
  private String type;

  @Column(name = "value", length = 255)
  private String value;

  @Column(name = "message", length = 500)
  private String message;

  @Column(name = "position")
  private Integer position;

  public java.util.UUID getFieldId() {
    return fieldId;
  }

  public void setFieldId(java.util.UUID fieldId) {
    this.fieldId = fieldId;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public Integer getPosition() {
    return position;
  }

  public void setPosition(Integer position) {
    this.position = position;
  }
}
