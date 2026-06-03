package com.erp.modules.inventory.controller;

import com.erp.common.api.ApiResponse;
import com.erp.config.ApiVersionConfig;
import com.erp.modules.inventory.dto.StockMovementDto;
import com.erp.modules.inventory.dto.WarehouseDto;
import com.erp.modules.inventory.entity.StockMovement;
import com.erp.modules.inventory.entity.Warehouse;
import com.erp.modules.inventory.service.InventoryService;
import com.erp.modules.inventory.service.WarehouseService;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiVersionConfig.API_V1 + "/inventory")
public class InventoryController {

  private final InventoryService inventoryService;
  private final WarehouseService warehouseService;

  public InventoryController(InventoryService inventoryService, WarehouseService warehouseService) {
    this.inventoryService = inventoryService;
    this.warehouseService = warehouseService;
  }

  // Stock endpoints
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

  // Warehouse CRUD
  @GetMapping("/warehouses")
  public ResponseEntity<ApiResponse<List<Warehouse>>> getWarehouses() {
    List<Warehouse> warehouses = warehouseService.findAll();
    return ResponseEntity.ok(ApiResponse.success(warehouses, "Warehouses retrieved."));
  }

  @GetMapping("/warehouses/{id}")
  public ResponseEntity<ApiResponse<Warehouse>> getWarehouse(@PathVariable UUID id) {
    Warehouse warehouse = warehouseService.findById(id).orElseThrow(() -> new RuntimeException("Warehouse not found"));
    return ResponseEntity.ok(ApiResponse.success(warehouse, "Warehouse retrieved."));
  }

  @PostMapping("/warehouses")
  public ResponseEntity<ApiResponse<Warehouse>> createWarehouse(@RequestBody WarehouseDto dto) {
    Warehouse warehouse = new Warehouse();
    warehouse.setName(dto.getName());
    warehouse.setLocation(dto.getLocation());
    Warehouse saved = warehouseService.create(warehouse);
    return ResponseEntity.ok(ApiResponse.success(saved, "Warehouse created."));
  }

  @PutMapping("/warehouses/{id}")
  public ResponseEntity<ApiResponse<Warehouse>> updateWarehouse(@PathVariable UUID id, @RequestBody WarehouseDto dto) {
    Warehouse warehouse = warehouseService.findById(id).orElseThrow(() -> new RuntimeException("Warehouse not found"));
    warehouse.setName(dto.getName());
    warehouse.setLocation(dto.getLocation());
    Warehouse updated = warehouseService.update(warehouse);
    return ResponseEntity.ok(ApiResponse.success(updated, "Warehouse updated."));
  }

  @DeleteMapping("/warehouses/{id}")
  public ResponseEntity<ApiResponse<Void>> deleteWarehouse(@PathVariable UUID id) {
    warehouseService.delete(id);
    return ResponseEntity.ok(ApiResponse.successMessage("Warehouse deleted."));
  }
}
