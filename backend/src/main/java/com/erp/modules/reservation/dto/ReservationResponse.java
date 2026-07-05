package com.erp.modules.reservation.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class ReservationResponse {

  private UUID id;
  private UUID productId;
  private UUID warehouseId;
  private UUID locationId;
  private Double quantity;
  private Double reservedQuantity;
  private String sourceDocument;
  private String sourceLine;
  private String status;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private Boolean isActive;

  public UUID getId() { return id; }
  public void setId(UUID id) { this.id = id; }
  public UUID getProductId() { return productId; }
  public void setProductId(UUID productId) { this.productId = productId; }
  public UUID getWarehouseId() { return warehouseId; }
  public void setWarehouseId(UUID warehouseId) { this.warehouseId = warehouseId; }
  public UUID getLocationId() { return locationId; }
  public void setLocationId(UUID locationId) { this.locationId = locationId; }
  public Double getQuantity() { return quantity; }
  public void setQuantity(Double quantity) { this.quantity = quantity; }
  public Double getReservedQuantity() { return reservedQuantity; }
  public void setReservedQuantity(Double reservedQuantity) { this.reservedQuantity = reservedQuantity; }
  public String getSourceDocument() { return sourceDocument; }
  public void setSourceDocument(String sourceDocument) { this.sourceDocument = sourceDocument; }
  public String getSourceLine() { return sourceLine; }
  public void setSourceLine(String sourceLine) { this.sourceLine = sourceLine; }
  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }
  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
  public LocalDateTime getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
  public Boolean getIsActive() { return isActive; }
  public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
