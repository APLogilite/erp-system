package com.erp.modules.common.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class PriceResolveResponse {
  private BigDecimal unitPrice;
  private UUID priceListId;
  private String source;
  private BigDecimal discountPercent;
  private BigDecimal discountAmount;
  private UUID discountRuleId;

  // ... getters/setters ...

  public BigDecimal getUnitPrice() { return unitPrice; }
  public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
  public UUID getPriceListId() { return priceListId; }
  public void setPriceListId(UUID priceListId) { this.priceListId = priceListId; }
  public String getSource() { return source; }
  public void setSource(String source) { this.source = source; }
  public BigDecimal getDiscountPercent() { return discountPercent; }
  public void setDiscountPercent(BigDecimal p) { this.discountPercent = p; }
  public BigDecimal getDiscountAmount() { return discountAmount; }
  public void setDiscountAmount(BigDecimal a) { this.discountAmount = a; }
  public UUID getDiscountRuleId() { return discountRuleId; }
  public void setDiscountRuleId(UUID id) { this.discountRuleId = id; }
}
