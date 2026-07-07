package com.erp.core.metadata.entity;

import com.erp.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "sys_form_role_filters")
public class FormRoleFilterEntity extends BaseEntity {

  @Column(name = "form_id", nullable = false)
  private java.util.UUID formId;

  @Column(name = "role_id", nullable = false)
  private java.util.UUID roleId;

  @Column(name = "condition_field", nullable = false, length = 100)
  private String conditionField;

  @Column(name = "condition_operator", nullable = false, length = 50)
  private String conditionOperator;

  @Column(name = "condition_value", length = 255)
  private String conditionValue;

  @Column(name = "position")
  private Integer position;

  public java.util.UUID getFormId() {
    return formId;
  }

  public void setFormId(java.util.UUID formId) {
    this.formId = formId;
  }

  public java.util.UUID getRoleId() {
    return roleId;
  }

  public void setRoleId(java.util.UUID roleId) {
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
