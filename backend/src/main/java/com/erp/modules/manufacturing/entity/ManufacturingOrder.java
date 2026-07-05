package com.erp.modules.manufacturing.entity;

import com.erp.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "manufacturing_orders")
public class ManufacturingOrder extends BaseEntity {

  @Column(name = "document_no", nullable = false, unique = true)
  private String documentNo;

  @Column(name = "product_id", nullable = false)
  private UUID productId;

  @Column(name = "bom_id")
  private UUID bomId;

  @Column(name = "routing_id")
  private UUID routingId;

  @Column(name = "warehouse_id", nullable = false)
  private UUID warehouseId;

  @Column(name = "planned_quantity", nullable = false)
  private Double plannedQuantity;

  @Column(name = "completed_quantity")
  private Double completedQuantity = 0.0;

  @Column(name = "planned_start")
  private LocalDate plannedStart;

  @Column(name = "planned_end")
  private LocalDate plannedEnd;

  @Column(nullable = false)
  private String status = "DRAFT";

  @Column
  private String priority = "MEDIUM";

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
}
