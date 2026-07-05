package com.erp.modules.inventory.controller;

import com.erp.common.api.ApiResponse;
import com.erp.config.ApiVersionConfig;
import com.erp.modules.inventory.dto.StockMovementDto;
import com.erp.modules.inventory.entity.StockMovement;
import com.erp.modules.inventory.service.InventoryService;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiVersionConfig.API_V1 + "/inventory")
public class InventoryController {

  private final InventoryService inventoryService;

  public InventoryController(InventoryService inventoryService) {
    this.inventoryService = inventoryService;
  }

  @GetMapping("/stock/{productId}/{warehouseId}")
  public ResponseEntity<ApiResponse<Double>> getStock(@PathVariable UUID productId, @PathVariable UUID warehouseId) {
    Double stock = inventoryService.getCurrentStock(productId, warehouseId);
    return ResponseEntity.ok(ApiResponse.success(stock, "Stock level retrieved."));
  }

  @PostMapping("/movement")
  public ResponseEntity<ApiResponse<StockMovement>> createMovement(@RequestBody StockMovementDto dto) {
    StockMovement movement = new StockMovement();
    movement.setProductId(dto.getProductId());
    movement.setWarehouseId(dto.getWarehouseId());
    movement.setQuantity(dto.getQuantity());
    movement.setMovementType(dto.getMovementType());
    movement.setReferenceId(dto.getReferenceId());
    movement.setReferenceType(dto.getReferenceType());
    movement.setMovementDate(dto.getMovementDate());
    StockMovement saved = inventoryService.create(movement);
    return ResponseEntity.ok(ApiResponse.success(saved, "Stock movement recorded."));
  }
}
