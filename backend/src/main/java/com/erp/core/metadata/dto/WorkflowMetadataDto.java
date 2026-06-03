package com.erp.core.metadata.dto;

import java.util.List;

public class WorkflowMetadataDto {

  private String code;
  private String modelCode;
  private List<WorkflowStateDto> states;
  private List<WorkflowTransitionDto> transitions;

  public WorkflowMetadataDto() {}

  public WorkflowMetadataDto(String code, String modelCode) {
    this.code = code;
    this.modelCode = modelCode;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getModelCode() {
    return modelCode;
  }

  public void setModelCode(String modelCode) {
    this.modelCode = modelCode;
  }

  public List<WorkflowStateDto> getStates() {
    return states;
  }

  public void setStates(List<WorkflowStateDto> states) {
    this.states = states;
  }

  public List<WorkflowTransitionDto> getTransitions() {
    return transitions;
  }

  public void setTransitions(List<WorkflowTransitionDto> transitions) {
    this.transitions = transitions;
  }
}
