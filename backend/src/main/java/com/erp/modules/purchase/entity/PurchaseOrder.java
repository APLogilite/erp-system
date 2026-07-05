package com.erp.modules.purchase.entity;

import com.erp.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "purchase_orders")
public class PurchaseOrder extends BaseEntity {

  @Column(name = "document_no", unique = true, nullable = false)
  private String documentNo;

  @Column(name = "document_date", nullable = false)
  private LocalDate documentDate;

  @Column(name = "vendor_id", nullable = false)
  private UUID vendorId;

  @Column(name = "warehouse_id")
  private UUID warehouseId;

  @Column(nullable = false)
  private String status = "DRAFT";

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column
  private String currency = "USD";

  @Column(name = "total_amount")
  private Double totalAmount = 0.0;

  @Column(name = "expected_date")
  private LocalDate expectedDate;

  public String getDocumentNo() { return documentNo; }
  public void setDocumentNo(String documentNo) { this.documentNo = documentNo; }
  public LocalDate getDocumentDate() { return documentDate; }
  public void setDocumentDate(LocalDate documentDate) { this.documentDate = documentDate; }
  public UUID getVendorId() { return vendorId; }
  public void setVendorId(UUID vendorId) { this.vendorId = vendorId; }
  public UUID getWarehouseId() { return warehouseId; }
  public void setWarehouseId(UUID warehouseId) { this.warehouseId = warehouseId; }
  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }
  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }
  public String getCurrency() { return currency; }
  public void setCurrency(String currency) { this.currency = currency; }
  public Double getTotalAmount() { return totalAmount; }
  public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }
  public LocalDate getExpectedDate() { return expectedDate; }
  public void setExpectedDate(LocalDate expectedDate) { this.expectedDate = expectedDate; }
}
