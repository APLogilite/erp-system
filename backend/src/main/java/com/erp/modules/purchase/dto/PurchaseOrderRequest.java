package com.erp.modules.purchase.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class PurchaseOrderRequest {

  private LocalDate documentDate;
  private UUID vendorId;
  private UUID warehouseId;
  private String description;
  private String currency;
  private LocalDate expectedDate;
  private List<PurchaseOrderLineRequest> lines;

  public LocalDate getDocumentDate() { return documentDate; }
  public void setDocumentDate(LocalDate documentDate) { this.documentDate = documentDate; }
  public UUID getVendorId() { return vendorId; }
  public void setVendorId(UUID vendorId) { this.vendorId = vendorId; }
  public UUID getWarehouseId() { return warehouseId; }
  public void setWarehouseId(UUID warehouseId) { this.warehouseId = warehouseId; }
  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }
  public String getCurrency() { return currency; }
  public void setCurrency(String currency) { this.currency = currency; }
  public LocalDate getExpectedDate() { return expectedDate; }
  public void setExpectedDate(LocalDate expectedDate) { this.expectedDate = expectedDate; }
  public List<PurchaseOrderLineRequest> getLines() { return lines; }
  public void setLines(List<PurchaseOrderLineRequest> lines) { this.lines = lines; }
}
