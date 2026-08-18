package com.erp.modules.common.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class PriceResolveRequest {
  private UUID productId;
  private UUID partnerId;
  private BigDecimal quantity;

  public UUID getProductId() { return productId; }
  public void setProductId(UUID productId) { this.productId = productId; }
  public UUID getPartnerId() { return partnerId; }
  public void setPartnerId(UUID partnerId) { this.partnerId = partnerId; }
  public BigDecimal getQuantity() { return quantity; }
  public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
}
