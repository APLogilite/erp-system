package com.erp.core.metadata.dto;

public class FormFieldRuleCreateRequest {

  private String conditionField;
  private String conditionOperator;
  private String conditionValue;
  private String action;
  private Integer logicGroup;
  private Integer position;

  public FormFieldRuleCreateRequest() {}

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

  public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
  }

  public Integer getLogicGroup() {
    return logicGroup;
  }

  public void setLogicGroup(Integer logicGroup) {
    this.logicGroup = logicGroup;
  }

  public Integer getPosition() {
    return position;
  }

  public void setPosition(Integer position) {
    this.position = position;
  }
}
