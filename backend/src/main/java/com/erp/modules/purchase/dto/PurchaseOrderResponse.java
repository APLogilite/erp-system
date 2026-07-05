package com.erp.modules.purchase.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class PurchaseOrderResponse {

  private UUID id;
  private String documentNo;
  private LocalDate documentDate;
  private UUID vendorId;
  private UUID warehouseId;
  private String status;
  private String description;
  private String currency;
  private Double totalAmount;
  private LocalDate expectedDate;
  private List<PurchaseOrderLineResponse> lines;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private Boolean isActive;

  public UUID getId() { return id; }
  public void setId(UUID id) { this.id = id; }
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
  public List<PurchaseOrderLineResponse> getLines() { return lines; }
  public void setLines(List<PurchaseOrderLineResponse> lines) { this.lines = lines; }
  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
  public LocalDateTime getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
  public Boolean getIsActive() { return isActive; }
  public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
