package com.erp.modules.reservation.entity;

import com.erp.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "reservations")
public class Reservation extends BaseEntity {

  @Column(name = "product_id", nullable = false)
  private UUID productId;

  @Column(name = "warehouse_id", nullable = false)
  private UUID warehouseId;

  @Column(name = "location_id")
  private UUID locationId;

  @Column(nullable = false)
  private Double quantity;

  @Column(name = "reserved_quantity", nullable = false)
  private Double reservedQuantity = 0.0;

  @Column(name = "source_document")
  private String sourceDocument;

  @Column(name = "source_line")
  private String sourceLine;

  @Column(nullable = false)
  private String status = "DRAFT";

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
}
