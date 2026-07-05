package com.erp.modules.sales.entity;

import com.erp.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "sales_orders")
public class SalesOrder extends BaseEntity {

  @Column(name = "document_no", unique = true, nullable = false)
  private String documentNo;

  @Column(name = "document_date", nullable = false)
  private LocalDate documentDate;

  @Column(name = "customer_id", nullable = false)
  private UUID customerId;

  @Column(name = "warehouse_id")
  private UUID warehouseId;

  @Column(nullable = false)
  private String status = "DRAFT";

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(name = "total_amount")
  private Double totalAmount = 0.0;

  @Column
  private String currency = "USD";

  public String getDocumentNo() { return documentNo; }
  public void setDocumentNo(String documentNo) { this.documentNo = documentNo; }
  public LocalDate getDocumentDate() { return documentDate; }
  public void setDocumentDate(LocalDate documentDate) { this.documentDate = documentDate; }
  public UUID getCustomerId() { return customerId; }
  public void setCustomerId(UUID customerId) { this.customerId = customerId; }
  public UUID getWarehouseId() { return warehouseId; }
  public void setWarehouseId(UUID warehouseId) { this.warehouseId = warehouseId; }
  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }
  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }
  public Double getTotalAmount() { return totalAmount; }
  public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }
  public String getCurrency() { return currency; }
  public void setCurrency(String currency) { this.currency = currency; }
}
