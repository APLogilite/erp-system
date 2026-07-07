package com.erp.core.metadata.entity;

import com.erp.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "sys_form_layout_sections")
public class FormLayoutSectionEntity extends BaseEntity {

  @Column(name = "form_id", nullable = false)
  private java.util.UUID formId;

  @Column(name = "code", nullable = false, length = 100)
  private String code;

  @Column(name = "label", nullable = false, length = 200)
  private String label;

  @Column(name = "collapsible")
  private Boolean collapsible = false;

  @Column(name = "columns")
  private Integer columns = 1;

  @Column(name = "position", nullable = false)
  private Integer position;

  public java.util.UUID getFormId() {
    return formId;
  }

  public void setFormId(java.util.UUID formId) {
    this.formId = formId;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public Boolean getCollapsible() {
    return collapsible;
  }

  public void setCollapsible(Boolean collapsible) {
    this.collapsible = collapsible;
  }

  public Integer getColumns() {
    return columns;
  }

  public void setColumns(Integer columns) {
    this.columns = columns;
  }

  public Integer getPosition() {
    return position;
  }

  public void setPosition(Integer position) {
    this.position = position;
  }
}
