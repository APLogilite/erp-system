package com.erp.modules.salesflow.dto;

import java.util.List;

public class TransitionResponse {
  private boolean success;
  private String newStatus;
  private String message;
  private List<String> allowedTransitions;

  public TransitionResponse() {}

  public TransitionResponse(boolean success, String newStatus, String message) {
    this.success = success;
    this.newStatus = newStatus;
    this.message = message;
  }

  public boolean isSuccess() { return success; }
  public void setSuccess(boolean success) { this.success = success; }

  public String getNewStatus() { return newStatus; }
  public void setNewStatus(String newStatus) { this.newStatus = newStatus; }

  public String getMessage() { return message; }
  public void setMessage(String message) { this.message = message; }

  public List<String> getAllowedTransitions() { return allowedTransitions; }
  public void setAllowedTransitions(List<String> allowedTransitions) { this.allowedTransitions = allowedTransitions; }
}
