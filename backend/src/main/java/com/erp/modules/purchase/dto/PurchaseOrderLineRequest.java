package com.erp.modules.purchase.dto;

import java.time.LocalDate;
import java.util.UUID;

public class PurchaseOrderLineRequest {

  private UUID productId;
  private String description;
  private Double quantity;
  private Double unitPrice;
  private Double discount;
  private LocalDate expectedDate;

  public UUID getProductId() { return productId; }
  public void setProductId(UUID productId) { this.productId = productId; }
  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }
  public Double getQuantity() { return quantity; }
  public void setQuantity(Double quantity) { this.quantity = quantity; }
  public Double getUnitPrice() { return unitPrice; }
  public void setUnitPrice(Double unitPrice) { this.unitPrice = unitPrice; }
  public Double getDiscount() { return discount; }
  public void setDiscount(Double discount) { this.discount = discount; }
  public LocalDate getExpectedDate() { return expectedDate; }
  public void setExpectedDate(LocalDate expectedDate) { this.expectedDate = expectedDate; }
}
