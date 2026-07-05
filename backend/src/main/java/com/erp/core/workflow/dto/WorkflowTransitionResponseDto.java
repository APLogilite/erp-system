package com.erp.core.workflow.dto;

public class WorkflowTransitionResponseDto {

  private String code;
  private String label;
  private String fromState;
  private String toState;

  public WorkflowTransitionResponseDto() {}

  public WorkflowTransitionResponseDto(String code, String label, String fromState, String toState) {
    this.code = code;
    this.label = label;
    this.fromState = fromState;
    this.toState = toState;
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

  public String getFromState() {
    return fromState;
  }

  public void setFromState(String fromState) {
    this.fromState = fromState;
  }

  public String getToState() {
    return toState;
  }

  public void setToState(String toState) {
    this.toState = toState;
  }
}
