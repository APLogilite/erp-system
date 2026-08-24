package com.erp.modules.crmflow.dto;

public class OpportunityTransitionRequest {

  private String targetStage;

  public OpportunityTransitionRequest() {
  }

  public OpportunityTransitionRequest(String targetStage) {
    this.targetStage = targetStage;
  }

  public String getTargetStage() {
    return targetStage;
  }

  public void setTargetStage(String targetStage) {
    this.targetStage = targetStage;
  }
}
