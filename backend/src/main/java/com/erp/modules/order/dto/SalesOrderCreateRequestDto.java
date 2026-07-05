package com.erp.modules.order.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Nested DTO for saving Sales Order with lines in a single request.
 */
public class SalesOrderCreateRequestDto {

  private String orderNumber;
  private UUID customerId;
  private LocalDateTime orderDate;
  private String status;
  private Double totalAmount;
  private List<OrderLineCreateDto> lines;

  public static class OrderLineCreateDto {
    private UUID productId;
    private Double quantity;
    private Double unitPrice;
    private Double lineTotal;

    // Getters and setters
    public UUID getProductId() {
      return productId;
    }

    public void setProductId(UUID productId) {
      this.productId = productId;
    }

    public Double getQuantity() {
      return quantity;
    }

    public void setQuantity(Double quantity) {
      this.quantity = quantity;
    }

    public Double getUnitPrice() {
      return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
      this.unitPrice = unitPrice;
    }

    public Double getLineTotal() {
      return lineTotal;
    }

    public void setLineTotal(Double lineTotal) {
      this.lineTotal = lineTotal;
    }
  }

  // Getters and setters
  public String getOrderNumber() {
    return orderNumber;
  }

  public void setOrderNumber(String orderNumber) {
    this.orderNumber = orderNumber;
  }

  public UUID getCustomerId() {
    return customerId;
  }

  public void setCustomerId(UUID customerId) {
    this.customerId = customerId;
  }

  public LocalDateTime getOrderDate() {
    return orderDate;
  }

  public void setOrderDate(LocalDateTime orderDate) {
    this.orderDate = orderDate;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Double getTotalAmount() {
    return totalAmount;
  }

  public void setTotalAmount(Double totalAmount) {
    this.totalAmount = totalAmount;
  }

  public List<OrderLineCreateDto> getLines() {
    return lines;
  }

  public void setLines(List<OrderLineCreateDto> lines) {
    this.lines = lines;
  }
}
