package com.erp.modules.customer.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class Customer360Response {
  private Map<String, Object> partner;
  private BigDecimal outstanding;
  private List<Map<String, Object>> recentOrders;
  private List<Map<String, Object>> recentInvoices;
  private List<Map<String, Object>> recentPayments;
  private List<Map<String, Object>> openQuotations;

  public Map<String, Object> getPartner() { return partner; }
  public void setPartner(Map<String, Object> partner) { this.partner = partner; }

  public BigDecimal getOutstanding() { return outstanding; }
  public void setOutstanding(BigDecimal outstanding) { this.outstanding = outstanding; }

  public List<Map<String, Object>> getRecentOrders() { return recentOrders; }
  public void setRecentOrders(List<Map<String, Object>> recentOrders) { this.recentOrders = recentOrders; }

  public List<Map<String, Object>> getRecentInvoices() { return recentInvoices; }
  public void setRecentInvoices(List<Map<String, Object>> recentInvoices) { this.recentInvoices = recentInvoices; }

  public List<Map<String, Object>> getRecentPayments() { return recentPayments; }
  public void setRecentPayments(List<Map<String, Object>> recentPayments) { this.recentPayments = recentPayments; }

  public List<Map<String, Object>> getOpenQuotations() { return openQuotations; }
  public void setOpenQuotations(List<Map<String, Object>> openQuotations) { this.openQuotations = openQuotations; }
}
