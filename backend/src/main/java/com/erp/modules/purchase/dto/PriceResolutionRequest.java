package com.erp.modules.purchase.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class PriceResolutionRequest {

  private UUID vendorId;
  private UUID productId;
  private UUID uomId;
  private BigDecimal quantity;
  private LocalDate date;

  public UUID getVendorId() { return vendorId; }
  public void setVendorId(UUID vendorId) { this.vendorId = vendorId; }

  public UUID getProductId() { return productId; }
  public void setProductId(UUID productId) { this.productId = productId; }

  public UUID getUomId() { return uomId; }
  public void setUomId(UUID uomId) { this.uomId = uomId; }

  public BigDecimal getQuantity() { return quantity; }
  public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }

  public LocalDate getDate() { return date; }
  public void setDate(LocalDate date) { this.date = date; }
}
