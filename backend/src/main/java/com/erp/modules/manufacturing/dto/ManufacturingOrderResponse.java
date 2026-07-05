package com.erp.modules.manufacturing.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class ManufacturingOrderResponse {
  private UUID id;
  private String documentNo;
  private UUID productId;
  private UUID bomId;
  private UUID routingId;
  private UUID warehouseId;
  private Double plannedQuantity;
  private Double completedQuantity;
  private LocalDate plannedStart;
  private LocalDate plannedEnd;
  private String status;
  private String priority;
  private Boolean isActive;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public UUID getId() { return id; }
  public void setId(UUID id) { this.id = id; }
  public String getDocumentNo() { return documentNo; }
  public void setDocumentNo(String documentNo) { this.documentNo = documentNo; }
  public UUID getProductId() { return productId; }
  public void setProductId(UUID productId) { this.productId = productId; }
  public UUID getBomId() { return bomId; }
  public void setBomId(UUID bomId) { this.bomId = bomId; }
  public UUID getRoutingId() { return routingId; }
  public void setRoutingId(UUID routingId) { this.routingId = routingId; }
  public UUID getWarehouseId() { return warehouseId; }
  public void setWarehouseId(UUID warehouseId) { this.warehouseId = warehouseId; }
  public Double getPlannedQuantity() { return plannedQuantity; }
  public void setPlannedQuantity(Double plannedQuantity) { this.plannedQuantity = plannedQuantity; }
  public Double getCompletedQuantity() { return completedQuantity; }
  public void setCompletedQuantity(Double completedQuantity) { this.completedQuantity = completedQuantity; }
  public LocalDate getPlannedStart() { return plannedStart; }
  public void setPlannedStart(LocalDate plannedStart) { this.plannedStart = plannedStart; }
  public LocalDate getPlannedEnd() { return plannedEnd; }
  public void setPlannedEnd(LocalDate plannedEnd) { this.plannedEnd = plannedEnd; }
  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }
  public String getPriority() { return priority; }
  public void setPriority(String priority) { this.priority = priority; }
  public Boolean getIsActive() { return isActive; }
  public void setIsActive(Boolean isActive) { this.isActive = isActive; }
  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
  public LocalDateTime getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
