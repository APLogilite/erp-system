package com.erp.core.metadata.entity;

import com.erp.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "sys_form_field_rules")
public class FormFieldRuleEntity extends BaseEntity {

  @Column(name = "field_id", nullable = false)
  private java.util.UUID fieldId;

  @Column(name = "condition_field", nullable = false, length = 100)
  private String conditionField;

  @Column(name = "condition_operator", nullable = false, length = 50)
  private String conditionOperator;

  @Column(name = "condition_value", length = 255)
  private String conditionValue;

  @Column(name = "action", nullable = false, length = 50)
  private String action;

  @Column(name = "logic_group")
  private Integer logicGroup = 0;

  @Column(name = "position")
  private Integer position;

  public java.util.UUID getFieldId() {
    return fieldId;
  }

  public void setFieldId(java.util.UUID fieldId) {
    this.fieldId = fieldId;
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
