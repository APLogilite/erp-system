package com.erp.modules.common.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class PriceResolveResponse {
  private BigDecimal unitPrice;
  private UUID priceListId;
  private String source; // customer_price_list | default_price_list | product_base

  public BigDecimal getUnitPrice() { return unitPrice; }
  public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
  public UUID getPriceListId() { return priceListId; }
  public void setPriceListId(UUID priceListId) { this.priceListId = priceListId; }
  public String getSource() { return source; }
  public void setSource(String source) { this.source = source; }
}
