package com.erp.modules.common.dto;

import java.math.BigDecimal;

public class QuotationTotalsResponse {
  private BigDecimal subtotal;
  private BigDecimal discountAmount;
  private BigDecimal taxAmount;
  private BigDecimal grandTotal;

  public BigDecimal getSubtotal() { return subtotal; }
  public void setSubtotal(BigDecimal s) { this.subtotal = s; }
  public BigDecimal getDiscountAmount() { return discountAmount; }
  public void setDiscountAmount(BigDecimal d) { this.discountAmount = d; }
  public BigDecimal getTaxAmount() { return taxAmount; }
  public void setTaxAmount(BigDecimal t) { this.taxAmount = t; }
  public BigDecimal getGrandTotal() { return grandTotal; }
  public void setGrandTotal(BigDecimal g) { this.grandTotal = g; }
}
