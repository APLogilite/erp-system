package com.erp.modules.sales.entity;

import com.erp.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "sales_order_lines")
public class SalesOrderLine extends BaseEntity {

  @Column(name = "order_id", nullable = false)
  private UUID orderId;

  @Column(name = "line_no")
  private Integer lineNo;

  @Column(name = "product_id", nullable = false)
  private UUID productId;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(nullable = false)
  private Double quantity;

  @Column
  private String uom;

  @Column(name = "unit_price")
  private Double unitPrice = 0.0;

  @Column
  private Double discount = 0.0;

  @Column(name = "line_amount")
  private Double lineAmount = 0.0;

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
