package com.erp.modules.inventory.service;

import com.erp.common.base.BaseService;
import com.erp.modules.inventory.entity.InventoryTransaction;
import com.erp.modules.inventory.entity.InventoryTransactionLine;
import com.erp.modules.inventory.repository.InventoryTransactionRepository;
import com.erp.modules.inventory.repository.InventoryTransactionLineRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * InventoryTransactionService manages inventory transaction documents.
 * Handles creation, completion, and posting of inventory movements.
 */
@Service
public class InventoryTransactionService extends BaseService<InventoryTransaction> {

  private final InventoryTransactionRepository transactionRepository;
  private final InventoryTransactionLineRepository lineRepository;
  private final StockMovementService stockMovementService;

  public InventoryTransactionService(
      InventoryTransactionRepository transactionRepository,
      InventoryTransactionLineRepository lineRepository,
      StockMovementService stockMovementService) {
    this.transactionRepository = transactionRepository;
    this.lineRepository = lineRepository;
    this.stockMovementService = stockMovementService;
  }

  @Override
  protected JpaRepository<InventoryTransaction, UUID> getRepository() {
    return transactionRepository;
  }

  @Override
  protected void beforeCreate(InventoryTransaction entity) {
    String documentNumber = generateDocumentNumber();
    entity.setDocumentNumber(documentNumber);

    if (entity.getStatus() == null) {
      entity.setStatus("DRAFT");
    }

    if (entity.getTransactionDate() == null) {
      entity.setTransactionDate(LocalDateTime.now());
    }
  }

  @Transactional
  public void createTransactionLines(UUID transactionId, List<InventoryTransactionLine> lines) {
    InventoryTransaction transaction =
        transactionRepository
            .findById(transactionId)
            .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));

    int lineNumber = 1;
    for (InventoryTransactionLine line : lines) {
      if (line.getQuantity() <= 0) {
        throw new IllegalArgumentException("Quantity must be greater than 0");
      }
      line.setTransactionId(transactionId);
      line.setLineNumber(lineNumber++);
      lineRepository.save(line);
    }
  }

  @Transactional
  public void completeTransaction(UUID transactionId) {
    InventoryTransaction transaction =
        transactionRepository
            .findById(transactionId)
            .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));

    if (!"DRAFT".equals(transaction.getStatus())) {
      throw new IllegalArgumentException("Only DRAFT transactions can be completed");
    }

    transaction.setStatus("COMPLETED");
    transactionRepository.save(transaction);
  }

  @Transactional
  public void postTransaction(UUID transactionId) {
    InventoryTransaction transaction =
        transactionRepository
            .findById(transactionId)
            .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));

    if (!"COMPLETED".equals(transaction.getStatus())) {
      throw new IllegalArgumentException("Only COMPLETED transactions can be posted");
    }

    List<InventoryTransactionLine> lines = lineRepository.findByTransactionId(transactionId);

    for (InventoryTransactionLine line : lines) {
      processMovement(transaction, line);
    }

    transaction.setStatus("POSTED");
    transactionRepository.save(transaction);
  }

  private void processMovement(InventoryTransaction transaction, InventoryTransactionLine line) {
    String type = transaction.getTransactionType();
    UUID warehouseId = transaction.getWarehouseId();

    switch (type) {
      case "IN":
        stockMovementService.increaseStock(line.getProductId(), warehouseId, line.getQuantity());
        break;
      case "OUT":
        stockMovementService.decreaseStock(line.getProductId(), warehouseId, line.getQuantity());
        break;
      case "ADJUSTMENT":
        stockMovementService.adjustStock(line.getProductId(), warehouseId, line.getQuantity());
        break;
      default:
        throw new IllegalArgumentException("Unknown transaction type: " + type);
    }
  }

  private String generateDocumentNumber() {
    return "INV-" + System.currentTimeMillis();
  }
}
