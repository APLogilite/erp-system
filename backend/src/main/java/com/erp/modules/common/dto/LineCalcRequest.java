package com.erp.modules.common.dto;

import java.math.BigDecimal;

public class LineCalcRequest {
  private String productId;
  private String partnerId;
  private BigDecimal quantity;
  private BigDecimal taxRate;

  public String getProductId() { return productId; }
  public void setProductId(String p) { this.productId = p; }
  public String getPartnerId() { return partnerId; }
  public void setPartnerId(String p) { this.partnerId = p; }
  public BigDecimal getQuantity() { return quantity; }
  public void setQuantity(BigDecimal q) { this.quantity = q; }
  public BigDecimal getTaxRate() { return taxRate; }
  public void setTaxRate(BigDecimal t) { this.taxRate = t; }
}
