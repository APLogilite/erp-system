package com.erp.modules.reservation.dto;

import java.util.UUID;

public class ReservationRequest {

  private UUID productId;
  private UUID warehouseId;
  private UUID locationId;
  private Double quantity;
  private String sourceDocument;
  private String sourceLine;

  public UUID getProductId() { return productId; }
  public void setProductId(UUID productId) { this.productId = productId; }
  public UUID getWarehouseId() { return warehouseId; }
  public void setWarehouseId(UUID warehouseId) { this.warehouseId = warehouseId; }
  public UUID getLocationId() { return locationId; }
  public void setLocationId(UUID locationId) { this.locationId = locationId; }
  public Double getQuantity() { return quantity; }
  public void setQuantity(Double quantity) { this.quantity = quantity; }
  public String getSourceDocument() { return sourceDocument; }
  public void setSourceDocument(String sourceDocument) { this.sourceDocument = sourceDocument; }
  public String getSourceLine() { return sourceLine; }
  public void setSourceLine(String sourceLine) { this.sourceLine = sourceLine; }
}
