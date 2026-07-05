package com.erp.modules.inventory.entity;

import com.erp.common.base.BaseEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * InventoryTransaction entity (document header).
 * Represents a stock movement document (IN, OUT, TRANSFER, ADJUSTMENT).
 */
@Entity
@Table(name = "inventory_transactions")
public class InventoryTransaction extends BaseEntity {

  @Column(name = "document_number", unique = true, nullable = false)
  private String documentNumber;

  @Column(name = "warehouse_id", nullable = false)
  private UUID warehouseId;

  @Column(name = "transaction_type", nullable = false)
  private String transactionType; // IN, OUT, TRANSFER, ADJUSTMENT

  @Column(name = "transaction_date", nullable = false)
  private LocalDateTime transactionDate;

  @Column(name = "status", nullable = false)
  private String status = "DRAFT"; // DRAFT, COMPLETED, POSTED, CLOSED

  @Column(name = "description")
  private String description;

  @Column(name = "reference_number")
  private String referenceNumber; // PO, SO, etc.

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

  public String getReferenceNumber() {
    return referenceNumber;
  }

  public void setReferenceNumber(String referenceNumber) {
    this.referenceNumber = referenceNumber;
  }
}
