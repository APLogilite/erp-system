package com.erp.core.metadata.dto;

import java.util.Map;

public class WorkflowTransitionDto {

  private String code;
  private String label;
  private String fromState;
  private String toState;
  private Map<String, Object> guards;
  private Map<String, Object> actions;

  public WorkflowTransitionDto() {}

  public WorkflowTransitionDto(String code, String label, String fromState, String toState) {
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

  public Map<String, Object> getGuards() {
    return guards;
  }

  public void setGuards(Map<String, Object> guards) {
    this.guards = guards;
  }

  public Map<String, Object> getActions() {
    return actions;
  }

  public void setActions(Map<String, Object> actions) {
    this.actions = actions;
  }
}
