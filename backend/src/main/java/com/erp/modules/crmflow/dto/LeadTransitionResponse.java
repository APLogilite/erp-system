package com.erp.modules.crmflow.dto;

public class LeadTransitionResponse {

  private boolean success;
  private String status;
  private String message;

  public LeadTransitionResponse() {
  }

  public LeadTransitionResponse(boolean success, String status, String message) {
    this.success = success;
    this.status = status;
    this.message = message;
  }

  public boolean isSuccess() {
    return success;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
