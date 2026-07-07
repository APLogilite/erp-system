package com.erp.core.metadata.dto;

import java.util.UUID;

public class FormRoleFilterCreateRequest {

  private UUID roleId;
  private String conditionField;
  private String conditionOperator;
  private String conditionValue;
  private Integer position;

  public FormRoleFilterCreateRequest() {}

  public UUID getRoleId() {
    return roleId;
  }

  public void setRoleId(UUID roleId) {
    this.roleId = roleId;
  }

  public String getConditionField() {
    return conditionField;
  }

  public void setConditionField(String conditionField) {
    this.conditionField = conditionField;
  }

  public String getConditionOperator() {
    return conditionOperator;
  }

  public void setConditionOperator(String conditionOperator) {
    this.conditionOperator = conditionOperator;
  }

  public String getConditionValue() {
    return conditionValue;
  }

  public void setConditionValue(String conditionValue) {
    this.conditionValue = conditionValue;
  }

  public Integer getPosition() {
    return position;
  }

  public void setPosition(Integer position) {
    this.position = position;
  }
}
