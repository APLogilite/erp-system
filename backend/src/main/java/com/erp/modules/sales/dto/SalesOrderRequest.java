package com.erp.modules.sales.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class SalesOrderRequest {

  private LocalDate documentDate;
  private UUID customerId;
  private UUID warehouseId;
  private String description;
  private String currency;
  private List<SalesOrderLineRequest> lines;

  public LocalDate getDocumentDate() { return documentDate; }
  public void setDocumentDate(LocalDate documentDate) { this.documentDate = documentDate; }
  public UUID getCustomerId() { return customerId; }
  public void setCustomerId(UUID customerId) { this.customerId = customerId; }
  public UUID getWarehouseId() { return warehouseId; }
  public void setWarehouseId(UUID warehouseId) { this.warehouseId = warehouseId; }
  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }
  public String getCurrency() { return currency; }
  public void setCurrency(String currency) { this.currency = currency; }
  public List<SalesOrderLineRequest> getLines() { return lines; }
  public void setLines(List<SalesOrderLineRequest> lines) { this.lines = lines; }
}
