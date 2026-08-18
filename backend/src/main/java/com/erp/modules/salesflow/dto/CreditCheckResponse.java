package com.erp.modules.salesflow.dto;

import java.math.BigDecimal;

public class CreditCheckResponse {
  private boolean approved;
  private String reason;
  private BigDecimal creditLimit;
  private BigDecimal outstanding;
  private BigDecimal available;
  private BigDecimal orderAmount;

  public boolean isApproved() { return approved; }
  public void setApproved(boolean approved) { this.approved = approved; }

  public String getReason() { return reason; }
  public void setReason(String reason) { this.reason = reason; }

  public BigDecimal getCreditLimit() { return creditLimit; }
  public void setCreditLimit(BigDecimal creditLimit) { this.creditLimit = creditLimit; }

  public BigDecimal getOutstanding() { return outstanding; }
  public void setOutstanding(BigDecimal outstanding) { this.outstanding = outstanding; }

  public BigDecimal getAvailable() { return available; }
  public void setAvailable(BigDecimal available) { this.available = available; }

  public BigDecimal getOrderAmount() { return orderAmount; }
  public void setOrderAmount(BigDecimal orderAmount) { this.orderAmount = orderAmount; }
}
