package com.erp.modules.inventory.entity;

import com.erp.common.base.BaseEntity;
import jakarta.persistence.*;
import java.util.UUID;

/**
 * InventoryTransactionLine entity.
 * Represents individual line items in an inventory transaction document.
 */
@Entity
@Table(name = "inventory_transaction_lines")
public class InventoryTransactionLine extends BaseEntity {

  @Column(name = "transaction_id", nullable = false)
  private UUID transactionId;

  @Column(name = "product_id", nullable = false)
  private UUID productId;

  @Column(name = "location_id")
  private UUID locationId;

  @Column(name = "quantity", nullable = false)
  private Double quantity;

  @Column(name = "uom", nullable = false)
  private String uom;

  @Column(name = "line_number", nullable = false)
  private Integer lineNumber;

  public UUID getTransactionId() {
    return transactionId;
  }

  public void setTransactionId(UUID transactionId) {
    this.transactionId = transactionId;
  }

  public UUID getProductId() {
    return productId;
  }

  public void setProductId(UUID productId) {
    this.productId = productId;
  }

  public UUID getLocationId() {
    return locationId;
  }

  public void setLocationId(UUID locationId) {
    this.locationId = locationId;
  }

  public Double getQuantity() {
    return quantity;
  }

  public void setQuantity(Double quantity) {
    this.quantity = quantity;
  }

  public String getUom() {
    return uom;
  }

  public void setUom(String uom) {
    this.uom = uom;
  }

  public Integer getLineNumber() {
    return lineNumber;
  }

  public void setLineNumber(Integer lineNumber) {
    this.lineNumber = lineNumber;
  }
}
