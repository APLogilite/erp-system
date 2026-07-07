package com.erp.core.metadata.dto;

public class FormFieldValidationCreateRequest {

  private String type;
  private String value;
  private String message;
  private Integer position;

  public FormFieldValidationCreateRequest() {}

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
