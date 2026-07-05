package com.erp.modules.warehouse.controller;

import com.erp.common.api.ApiResponse;
import com.erp.config.ApiVersionConfig;
import com.erp.modules.warehouse.dto.WarehouseRequestDTO;
import com.erp.modules.warehouse.dto.WarehouseResponseDTO;
import com.erp.modules.warehouse.entity.Warehouse;
import com.erp.modules.warehouse.service.WarehouseService;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiVersionConfig.API_V1 + "/warehouses")
public class WarehouseController {

    private final WarehouseService warehouseService;

    public WarehouseController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<WarehouseResponseDTO>> create(@RequestBody WarehouseRequestDTO dto) {
        Warehouse entity = mapToEntity(dto);
        Warehouse saved = warehouseService.create(entity);
        return ResponseEntity.ok(ApiResponse.success(mapToResponse(saved), "Warehouse created."));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<WarehouseResponseDTO>>> getAll() {
        List<WarehouseResponseDTO> list = warehouseService.findAll().stream()
                .map(this::mapToResponse).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(list, "Warehouses retrieved."));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<WarehouseResponseDTO>> getById(@PathVariable UUID id) {
        Warehouse entity = warehouseService.findByIdOrThrow(id);
        return ResponseEntity.ok(ApiResponse.success(mapToResponse(entity), "Warehouse retrieved."));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<WarehouseResponseDTO>> update(@PathVariable UUID id, @RequestBody WarehouseRequestDTO dto) {
        Warehouse existing = warehouseService.findByIdOrThrow(id);
        existing.setCode(dto.getCode());
        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());
        Warehouse updated = warehouseService.update(existing);
        return ResponseEntity.ok(ApiResponse.success(mapToResponse(updated), "Warehouse updated."));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        warehouseService.delete(id);
        return ResponseEntity.ok(ApiResponse.successMessage("Warehouse deleted."));
    }

    private Warehouse mapToEntity(WarehouseRequestDTO dto) {
        Warehouse entity = new Warehouse();
        entity.setCode(dto.getCode());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        return entity;
    }

    private WarehouseResponseDTO mapToResponse(Warehouse entity) {
        WarehouseResponseDTO dto = new WarehouseResponseDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setIsActive(entity.getIsActive());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
