package com.erp.core.workflow.dto;

import java.util.Map;

public class WorkflowTransitionRequestDto {

  private String transitionCode;
  private String currentState;
  private Map<String, Object> context;

  public String getTransitionCode() {
    return transitionCode;
  }

  public void setTransitionCode(String transitionCode) {
    this.transitionCode = transitionCode;
  }

  public String getCurrentState() {
    return currentState;
  }

  public void setCurrentState(String currentState) {
    this.currentState = currentState;
  }

  public Map<String, Object> getContext() {
    return context;
  }

  public void setContext(Map<String, Object> context) {
    this.context = context;
  }
}
