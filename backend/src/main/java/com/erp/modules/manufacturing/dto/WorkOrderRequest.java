package com.erp.modules.manufacturing.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class WorkOrderRequest {
  private Integer sequence;
  private UUID operationId;
  private UUID workCenterId;
  private LocalDateTime plannedStart;
  private LocalDateTime plannedEnd;

  public Integer getSequence() { return sequence; }
  public void setSequence(Integer sequence) { this.sequence = sequence; }
  public UUID getOperationId() { return operationId; }
  public void setOperationId(UUID operationId) { this.operationId = operationId; }
  public UUID getWorkCenterId() { return workCenterId; }
  public void setWorkCenterId(UUID workCenterId) { this.workCenterId = workCenterId; }
  public LocalDateTime getPlannedStart() { return plannedStart; }
  public void setPlannedStart(LocalDateTime plannedStart) { this.plannedStart = plannedStart; }
  public LocalDateTime getPlannedEnd() { return plannedEnd; }
  public void setPlannedEnd(LocalDateTime plannedEnd) { this.plannedEnd = plannedEnd; }
}
