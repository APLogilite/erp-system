package com.erp.core.metadata.entity;

import com.erp.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "sys_form_sub_forms")
public class FormSubFormEntity extends BaseEntity {

  @Column(name = "parent_form_id", nullable = false)
  private java.util.UUID parentFormId;

  @Column(name = "relation_code", nullable = false, length = 100)
  private String relationCode;

  @Column(name = "child_form_code", nullable = false, length = 100)
  private String childFormCode;

  @Column(name = "label", nullable = false, length = 200)
  private String label;

  @Column(name = "display_as", length = 50)
  private String displayAs = "tab";

  @Column(name = "position")
  private Integer position;

  public java.util.UUID getParentFormId() {
    return parentFormId;
  }

  public void setParentFormId(java.util.UUID parentFormId) {
    this.parentFormId = parentFormId;
  }

  public String getRelationCode() {
    return relationCode;
  }

  public void setRelationCode(String relationCode) {
    this.relationCode = relationCode;
  }

  public String getChildFormCode() {
    return childFormCode;
  }

  public void setChildFormCode(String childFormCode) {
    this.childFormCode = childFormCode;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getDisplayAs() {
    return displayAs;
  }

  public void setDisplayAs(String displayAs) {
    this.displayAs = displayAs;
  }

  public Integer getPosition() {
    return position;
  }

  public void setPosition(Integer position) {
    this.position = position;
  }
}
