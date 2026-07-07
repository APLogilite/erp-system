package com.erp.core.metadata.dto;

public class FormCloneRequest {

  private String name;
  private String label;

  public FormCloneRequest() {}

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }
}
