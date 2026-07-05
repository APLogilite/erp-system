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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/inventory-transactions")
public class InventoryTransactionController {

  private final InventoryTransactionService transactionService;

  public InventoryTransactionController(InventoryTransactionService transactionService) {
    this.transactionService = transactionService;
  }

  @PostMapping
  public ResponseEntity<ApiResponse<UUID>> createTransaction(
      @RequestBody InventoryTransactionCreateRequestDto requestDto) {
    InventoryTransaction transaction = new InventoryTransaction();
    transaction.setWarehouseId(requestDto.getWarehouseId());
    transaction.setTransactionType(requestDto.getTransactionType());
    transaction.setTransactionDate(requestDto.getTransactionDate());
    transaction.setStatus(requestDto.getStatus() != null ? requestDto.getStatus() : "DRAFT");
    transaction.setDescription(requestDto.getDescription());

    InventoryTransaction savedTransaction = transactionService.create(transaction);

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
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<InventoryTransaction>> getTransaction(@PathVariable UUID id) {
    InventoryTransaction transaction =
        transactionService.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));
    return ResponseEntity.ok(ApiResponse.success(transaction, "Transaction retrieved successfully"));
  }

  @PostMapping("/{id}/complete")
  public ResponseEntity<ApiResponse<Void>> completeTransaction(@PathVariable UUID id) {
    transactionService.completeTransaction(id);
    return ResponseEntity.ok(ApiResponse.successMessage("Transaction completed successfully"));
  }

  @PostMapping("/{id}/post")
  public ResponseEntity<ApiResponse<Void>> postTransaction(@PathVariable UUID id) {
    transactionService.postTransaction(id);
    return ResponseEntity.ok(ApiResponse.successMessage("Transaction posted and stock movements applied"));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> deleteTransaction(@PathVariable UUID id) {
    transactionService.delete(id);
    return ResponseEntity.ok(ApiResponse.successMessage("Transaction deleted successfully"));
  }
}
