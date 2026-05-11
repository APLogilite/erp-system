package com.erp.modules.inventory.controller;

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
  public ResponseEntity<Double> getStock(@PathVariable UUID productId, @PathVariable UUID warehouseId) {
    Double stock = inventoryService.getCurrentStock(productId, warehouseId);
    return ResponseEntity.ok(stock);
  }

  @PostMapping("/movement")
  public ResponseEntity<StockMovement> createMovement(@RequestBody StockMovementDto dto) {
    StockMovement movement = new StockMovement();
    // Map dto to entity
    movement.setProductId(dto.getProductId());
    movement.setWarehouseId(dto.getWarehouseId());
    movement.setQuantity(dto.getQuantity());
    movement.setMovementType(dto.getMovementType());
    movement.setReferenceId(dto.getReferenceId());
    movement.setReferenceType(dto.getReferenceType());
    movement.setMovementDate(dto.getMovementDate());
    StockMovement saved = inventoryService.create(movement);
    return ResponseEntity.ok(saved);
  }

  // Warehouse CRUD
  @GetMapping("/warehouses")
  public ResponseEntity<List<Warehouse>> getWarehouses() {
    List<Warehouse> warehouses = warehouseService.findAll();
    return ResponseEntity.ok(warehouses);
  }

  @GetMapping("/warehouses/{id}")
  public ResponseEntity<Warehouse> getWarehouse(@PathVariable UUID id) {
    Warehouse warehouse = warehouseService.findById(id).orElseThrow(() -> new RuntimeException("Warehouse not found"));
    return ResponseEntity.ok(warehouse);
  }

  @PostMapping("/warehouses")
  public ResponseEntity<Warehouse> createWarehouse(@RequestBody WarehouseDto dto) {
    Warehouse warehouse = new Warehouse();
    warehouse.setName(dto.getName());
    warehouse.setLocation(dto.getLocation());
    Warehouse saved = warehouseService.create(warehouse);
    return ResponseEntity.ok(saved);
  }

  @PutMapping("/warehouses/{id}")
  public ResponseEntity<Warehouse> updateWarehouse(@PathVariable UUID id, @RequestBody WarehouseDto dto) {
    Warehouse warehouse = warehouseService.findById(id).orElseThrow(() -> new RuntimeException("Warehouse not found"));
    warehouse.setName(dto.getName());
    warehouse.setLocation(dto.getLocation());
    Warehouse updated = warehouseService.update(warehouse);
    return ResponseEntity.ok(updated);
  }

  @DeleteMapping("/warehouses/{id}")
  public ResponseEntity<Void> deleteWarehouse(@PathVariable UUID id) {
    warehouseService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
