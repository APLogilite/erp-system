package com.erp.core.metadata.dto;

import java.util.UUID;

public class FormLayoutSectionDto {

  private UUID id;
  private UUID formId;
  private String code;
  private String label;
  private Boolean collapsible;
  private Integer columns;
  private Integer position;

  public FormLayoutSectionDto() {}

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public UUID getFormId() {
    return formId;
  }

  public void setFormId(UUID formId) {
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
