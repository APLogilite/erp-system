package com.erp.core.metadata.dto;

import java.util.UUID;

public class FormFieldValidationDto {

  private UUID id;
  private UUID fieldId;
  private String type;
  private String value;
  private String message;
  private Integer position;

  public FormFieldValidationDto() {}

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public UUID getFieldId() {
    return fieldId;
  }

  public void setFieldId(UUID fieldId) {
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
