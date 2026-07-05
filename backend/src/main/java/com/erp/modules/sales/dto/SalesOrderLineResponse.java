package com.erp.modules.sales.dto;

import java.util.UUID;

public class SalesOrderLineResponse {

  private UUID id;
  private UUID orderId;
  private Integer lineNo;
  private UUID productId;
  private String description;
  private Double quantity;
  private String uom;
  private Double unitPrice;
  private Double discount;
  private Double lineAmount;

  public UUID getId() { return id; }
  public void setId(UUID id) { this.id = id; }
  public UUID getOrderId() { return orderId; }
  public void setOrderId(UUID orderId) { this.orderId = orderId; }
  public Integer getLineNo() { return lineNo; }
  public void setLineNo(Integer lineNo) { this.lineNo = lineNo; }
  public UUID getProductId() { return productId; }
  public void setProductId(UUID productId) { this.productId = productId; }
  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }
  public Double getQuantity() { return quantity; }
  public void setQuantity(Double quantity) { this.quantity = quantity; }
  public String getUom() { return uom; }
  public void setUom(String uom) { this.uom = uom; }
  public Double getUnitPrice() { return unitPrice; }
  public void setUnitPrice(Double unitPrice) { this.unitPrice = unitPrice; }
  public Double getDiscount() { return discount; }
  public void setDiscount(Double discount) { this.discount = discount; }
  public Double getLineAmount() { return lineAmount; }
  public void setLineAmount(Double lineAmount) { this.lineAmount = lineAmount; }
}
