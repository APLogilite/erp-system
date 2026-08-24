package com.erp.modules.salesflow.dto;

public class TransitionRequest {
  private String targetStatus;
  private boolean override;
  private String overrideReason;

  public String getTargetStatus() { return targetStatus; }
  public void setTargetStatus(String targetStatus) { this.targetStatus = targetStatus; }

  public boolean isOverride() { return override; }
  public void setOverride(boolean override) { this.override = override; }

  public String getOverrideReason() { return overrideReason; }
  public void setOverrideReason(String overrideReason) { this.overrideReason = overrideReason; }
}
