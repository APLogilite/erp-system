package com.erp.modules.common.dto;

import java.math.BigDecimal;

public class LineCalcResponse {
  private BigDecimal unitPrice;
  private BigDecimal discountPercent;
  private BigDecimal discountAmount;
  private BigDecimal lineTotal;
  private BigDecimal taxAmount;

  public BigDecimal getUnitPrice() { return unitPrice; }
  public void setUnitPrice(BigDecimal u) { this.unitPrice = u; }
  public BigDecimal getDiscountPercent() { return discountPercent; }
  public void setDiscountPercent(BigDecimal d) { this.discountPercent = d; }
  public BigDecimal getDiscountAmount() { return discountAmount; }
  public void setDiscountAmount(BigDecimal d) { this.discountAmount = d; }
  public BigDecimal getLineTotal() { return lineTotal; }
  public void setLineTotal(BigDecimal t) { this.lineTotal = t; }
  public BigDecimal getTaxAmount() { return taxAmount; }
  public void setTaxAmount(BigDecimal t) { this.taxAmount = t; }
}
