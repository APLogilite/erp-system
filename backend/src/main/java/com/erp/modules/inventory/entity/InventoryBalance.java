package com.erp.modules.inventory.entity;

import com.erp.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "inventory_balances")
public class InventoryBalance extends BaseEntity {

  @Column(name = "product_id", nullable = false)
  private UUID productId;

  @Column(name = "warehouse_id", nullable = false)
  private UUID warehouseId;

  @Column(name = "location_id")
  private UUID locationId;

  @Column(name = "on_hand", nullable = false)
  private Double onHand = 0.0;

  @Column(nullable = false)
  private Double reserved = 0.0;

  @Column(nullable = false)
  private Double available = 0.0;

  public UUID getProductId() { return productId; }
  public void setProductId(UUID productId) { this.productId = productId; }
  public UUID getWarehouseId() { return warehouseId; }
  public void setWarehouseId(UUID warehouseId) { this.warehouseId = warehouseId; }
  public UUID getLocationId() { return locationId; }
  public void setLocationId(UUID locationId) { this.locationId = locationId; }
  public Double getOnHand() { return onHand; }
  public void setOnHand(Double onHand) { this.onHand = onHand; }
  public Double getReserved() { return reserved; }
  public void setReserved(Double reserved) { this.reserved = reserved; }
  public Double getAvailable() { return available; }
  public void setAvailable(Double available) { this.available = available; }
}
