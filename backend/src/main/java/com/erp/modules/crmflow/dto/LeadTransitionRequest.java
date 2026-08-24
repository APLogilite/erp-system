package com.erp.modules.crmflow.dto;

public class LeadTransitionRequest {

  private String targetStatus;

  public LeadTransitionRequest() {
  }

  public LeadTransitionRequest(String targetStatus) {
    this.targetStatus = targetStatus;
  }

  public String getTargetStatus() {
    return targetStatus;
  }

  public void setTargetStatus(String targetStatus) {
    this.targetStatus = targetStatus;
  }
}
