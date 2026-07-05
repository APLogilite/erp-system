package com.erp.core.workflow.dto;

import java.util.UUID;

public class WorkflowTransitionResultDto {

  private UUID recordId;
  private String modelCode;
  private String transitionCode;
  private String fromState;
  private String toState;

  public UUID getRecordId() {
    return recordId;
  }

  public void setRecordId(UUID recordId) {
    this.recordId = recordId;
  }

  public String getModelCode() {
    return modelCode;
  }

  public void setModelCode(String modelCode) {
    this.modelCode = modelCode;
  }

  public String getTransitionCode() {
    return transitionCode;
  }

  public void setTransitionCode(String transitionCode) {
    this.transitionCode = transitionCode;
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
