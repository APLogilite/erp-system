package com.erp.modules.inventory.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Nested DTO for creating Inventory Transaction with lines in a single request.
 */
public class InventoryTransactionCreateRequestDto {

  private String documentNumber;
  private UUID warehouseId;
  private String transactionType;
  private LocalDateTime transactionDate;
  private String status;
  private String description;
  private List<InventoryTransactionLineCreateDto> lines;

  public static class InventoryTransactionLineCreateDto {
    private UUID productId;
    private UUID locationId;
    private Double quantity;
    private String uom;
    private Integer lineNumber;

    // Getters and setters
    public UUID getProductId() {
      return productId;
    }

    public void setProductId(UUID productId) {
      this.productId = productId;
    }

    public UUID getLocationId() {
      return locationId;
    }

    public void setLocationId(UUID locationId) {
      this.locationId = locationId;
    }

    public Double getQuantity() {
      return quantity;
    }

    public void setQuantity(Double quantity) {
      this.quantity = quantity;
    }

    public String getUom() {
      return uom;
    }

    public void setUom(String uom) {
      this.uom = uom;
    }

    public Integer getLineNumber() {
      return lineNumber;
    }

    public void setLineNumber(Integer lineNumber) {
      this.lineNumber = lineNumber;
    }
  }

  // Getters and setters
  public String getDocumentNumber() {
    return documentNumber;
  }

  public void setDocumentNumber(String documentNumber) {
    this.documentNumber = documentNumber;
  }

  public UUID getWarehouseId() {
    return warehouseId;
  }

  public void setWarehouseId(UUID warehouseId) {
    this.warehouseId = warehouseId;
  }

  public String getTransactionType() {
    return transactionType;
  }

  public void setTransactionType(String transactionType) {
    this.transactionType = transactionType;
  }

  public LocalDateTime getTransactionDate() {
    return transactionDate;
  }

  public void setTransactionDate(LocalDateTime transactionDate) {
    this.transactionDate = transactionDate;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public List<InventoryTransactionLineCreateDto> getLines() {
    return lines;
  }

  public void setLines(List<InventoryTransactionLineCreateDto> lines) {
    this.lines = lines;
  }
}
