package com.erp.modules.manufacturing.dto;

import java.time.LocalDate;
import java.util.UUID;

public class ManufacturingOrderRequest {
  private String documentNo;
  private UUID productId;
  private UUID bomId;
  private UUID routingId;
  private UUID warehouseId;
  private Double plannedQuantity;
  private LocalDate plannedStart;
  private LocalDate plannedEnd;
  private String priority;

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
  public LocalDate getPlannedStart() { return plannedStart; }
  public void setPlannedStart(LocalDate plannedStart) { this.plannedStart = plannedStart; }
  public LocalDate getPlannedEnd() { return plannedEnd; }
  public void setPlannedEnd(LocalDate plannedEnd) { this.plannedEnd = plannedEnd; }
  public String getPriority() { return priority; }
  public void setPriority(String priority) { this.priority = priority; }
}
