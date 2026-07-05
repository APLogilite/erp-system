package com.erp.modules.manufacturing.dto;

import java.util.UUID;

public class RoutingOperationRequest {
  private Integer sequence;
  private UUID workCenterId;
  private String operationName;
  private Double setupTime;
  private Double runTime;
  private Double queueTime;

  public Integer getSequence() { return sequence; }
  public void setSequence(Integer sequence) { this.sequence = sequence; }
  public UUID getWorkCenterId() { return workCenterId; }
  public void setWorkCenterId(UUID workCenterId) { this.workCenterId = workCenterId; }
  public String getOperationName() { return operationName; }
  public void setOperationName(String operationName) { this.operationName = operationName; }
  public Double getSetupTime() { return setupTime; }
  public void setSetupTime(Double setupTime) { this.setupTime = setupTime; }
  public Double getRunTime() { return runTime; }
  public void setRunTime(Double runTime) { this.runTime = runTime; }
  public Double getQueueTime() { return queueTime; }
  public void setQueueTime(Double queueTime) { this.queueTime = queueTime; }
}
