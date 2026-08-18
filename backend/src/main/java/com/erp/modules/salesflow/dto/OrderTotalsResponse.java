package com.erp.modules.salesflow.dto;

import java.math.BigDecimal;

public class OrderTotalsResponse {
  private BigDecimal subtotal;
  private BigDecimal discountAmount;
  private BigDecimal taxAmount;
  private BigDecimal grandTotal;

  public BigDecimal getSubtotal() { return subtotal; }
  public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

  public BigDecimal getDiscountAmount() { return discountAmount; }
  public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }

  public BigDecimal getTaxAmount() { return taxAmount; }
  public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }

  public BigDecimal getGrandTotal() { return grandTotal; }
  public void setGrandTotal(BigDecimal grandTotal) { this.grandTotal = grandTotal; }
}
