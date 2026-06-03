package com.erp.core.metadata.dto;

public class WorkflowStateDto {

  private String code;
  private String label;
  private boolean initial;

  public WorkflowStateDto() {}

  public WorkflowStateDto(String code, String label, boolean initial) {
    this.code = code;
    this.label = label;
    this.initial = initial;
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

  public boolean isInitial() {
    return initial;
  }

  public void setInitial(boolean initial) {
    this.initial = initial;
  }
}
