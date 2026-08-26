package com.erp.modules.purchase.entity;

import com.erp.common.base.BaseEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "pur_price_list_line")
public class PurchasePriceListLine extends BaseEntity {

  @Column(name = "price_list_id", nullable = false)
  private UUID priceListId;

  @Column(name = "product_id", nullable = false)
  private UUID productId;

  @Column(name = "uom_id")
  private UUID uomId;

  @Column(name = "min_qty", nullable = false)
  private BigDecimal minQty = BigDecimal.ZERO;

  @Column(name = "unit_price", nullable = false)
  private BigDecimal unitPrice = BigDecimal.ZERO;

  @Column(name = "is_active", nullable = false)
  private Boolean isActive = true;

  public UUID getPriceListId() { return priceListId; }
  public void setPriceListId(UUID priceListId) { this.priceListId = priceListId; }

  public UUID getProductId() { return productId; }
  public void setProductId(UUID productId) { this.productId = productId; }

  public UUID getUomId() { return uomId; }
  public void setUomId(UUID uomId) { this.uomId = uomId; }

  public BigDecimal getMinQty() { return minQty; }
  public void setMinQty(BigDecimal minQty) { this.minQty = minQty; }

  public BigDecimal getUnitPrice() { return unitPrice; }
  public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

  public Boolean getIsActive() { return isActive; }
  public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
