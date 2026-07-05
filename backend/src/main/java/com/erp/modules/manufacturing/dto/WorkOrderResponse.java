package com.erp.modules.manufacturing.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class WorkOrderResponse {
  private UUID id;
  private UUID manufacturingOrderId;
  private Integer sequence;
  private UUID operationId;
  private UUID workCenterId;
  private LocalDateTime plannedStart;
  private LocalDateTime plannedEnd;
  private LocalDateTime actualStart;
  private LocalDateTime actualEnd;
  private String status;
  private Boolean isActive;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public UUID getId() { return id; }
  public void setId(UUID id) { this.id = id; }
  public UUID getManufacturingOrderId() { return manufacturingOrderId; }
  public void setManufacturingOrderId(UUID manufacturingOrderId) { this.manufacturingOrderId = manufacturingOrderId; }
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
  public LocalDateTime getActualStart() { return actualStart; }
  public void setActualStart(LocalDateTime actualStart) { this.actualStart = actualStart; }
  public LocalDateTime getActualEnd() { return actualEnd; }
  public void setActualEnd(LocalDateTime actualEnd) { this.actualEnd = actualEnd; }
  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }
  public Boolean getIsActive() { return isActive; }
  public void setIsActive(Boolean isActive) { this.isActive = isActive; }
  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
  public LocalDateTime getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
