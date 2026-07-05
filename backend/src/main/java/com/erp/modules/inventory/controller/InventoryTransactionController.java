package com.erp.modules.inventory.controller;

import com.erp.common.api.ApiResponse;
import com.erp.modules.inventory.dto.InventoryTransactionCreateRequestDto;
import com.erp.modules.inventory.entity.InventoryTransaction;
import com.erp.modules.inventory.entity.InventoryTransactionLine;
import com.erp.modules.inventory.service.InventoryTransactionService;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * InventoryTransactionController handles inventory transaction CRUD operations.
 * Supports nested create for header + lines in single request.
 */
@RestController
@RequestMapping("/api/v1/inventory-transactions")
public class InventoryTransactionController {

  private final InventoryTransactionService transactionService;

  public InventoryTransactionController(InventoryTransactionService transactionService) {
    this.transactionService = transactionService;
  }

  /**
   * Create inventory transaction with nested lines (M2 requirement).
   */
  @PostMapping
  public ResponseEntity<ApiResponse<UUID>> createTransaction(
      @RequestBody InventoryTransactionCreateRequestDto requestDto) {
    try {
      InventoryTransaction transaction = new InventoryTransaction();
      transaction.setWarehouseId(requestDto.getWarehouseId());
      transaction.setTransactionType(requestDto.getTransactionType());
      transaction.setTransactionDate(requestDto.getTransactionDate());
      transaction.setStatus(
          requestDto.getStatus() != null ? requestDto.getStatus() : "DRAFT");
      transaction.setDescription(requestDto.getDescription());

      // beforeCreate hook sets documentNumber
      InventoryTransaction savedTransaction = transactionService.create(transaction);

      // Create lines if provided
      if (requestDto.getLines() != null && !requestDto.getLines().isEmpty()) {
        List<InventoryTransactionLine> lines = new ArrayList<>();
        for (InventoryTransactionCreateRequestDto.InventoryTransactionLineCreateDto lineDto :
            requestDto.getLines()) {
          InventoryTransactionLine line = new InventoryTransactionLine();
          line.setProductId(lineDto.getProductId());
          line.setLocationId(lineDto.getLocationId());
          line.setQuantity(lineDto.getQuantity());
          line.setUom(lineDto.getUom());
          lines.add(line);
        }
        transactionService.createTransactionLines(savedTransaction.getId(), lines);
      }

      return ResponseEntity.ok(
          ApiResponse.success(savedTransaction.getId(), "Inventory transaction created successfully"));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(
              ApiResponse.error(
                  "TRANSACTION_CREATE_ERROR",
                  "Failed to create inventory transaction: " + e.getMessage()));
    }
  }

  /**
   * Get inventory transaction by ID.
   */
  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<InventoryTransaction>> getTransaction(@PathVariable UUID id) {
    try {
      InventoryTransaction transaction =
          transactionService
              .findById(id)
              .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));
      return ResponseEntity.ok(
          ApiResponse.success(transaction, "Transaction retrieved successfully"));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(
              ApiResponse.error(
                  "TRANSACTION_NOTFOUND",
                  "Failed to retrieve transaction: " + e.getMessage()));
    }
  }

  /**
   * Complete transaction (change status from DRAFT to COMPLETED).
   */
  @PostMapping("/{id}/complete")
  public ResponseEntity<ApiResponse<Void>> completeTransaction(@PathVariable UUID id) {
    try {
      transactionService.completeTransaction(id);
      return ResponseEntity.ok(ApiResponse.successMessage("Transaction completed successfully"));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(
              ApiResponse.error(
                  "TRANSACTION_COMPLETE_ERROR",
                  "Failed to complete transaction: " + e.getMessage()));
    }
  }

  /**
   * Post transaction (apply stock movements and change status to POSTED).
   */
  @PostMapping("/{id}/post")
  public ResponseEntity<ApiResponse<Void>> postTransaction(@PathVariable UUID id) {
    try {
      transactionService.postTransaction(id);
      return ResponseEntity.ok(ApiResponse.successMessage("Transaction posted and stock movements applied"));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(
              ApiResponse.error(
                  "TRANSACTION_POST_ERROR",
                  "Failed to post transaction: " + e.getMessage()));
    }
  }

  /**
   * Delete inventory transaction.
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> deleteTransaction(@PathVariable UUID id) {
    try {
      transactionService.delete(id);
      return ResponseEntity.ok(ApiResponse.successMessage("Transaction deleted successfully"));
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(
              ApiResponse.error(
                  "TRANSACTION_DELETE_ERROR",
                  "Failed to delete transaction: " + e.getMessage()));
    }
  }
}
