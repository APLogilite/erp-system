package com.erp.modules.crmflow.dto;

public class OpportunityTransitionResponse {

  private boolean success;
  private String stage;
  private String message;

  public OpportunityTransitionResponse() {
  }

  public OpportunityTransitionResponse(boolean success, String stage, String message) {
    this.success = success;
    this.stage = stage;
    this.message = message;
  }

  public boolean isSuccess() {
    return success;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }

  public String getStage() {
    return stage;
  }

  public void setStage(String stage) {
    this.stage = stage;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
