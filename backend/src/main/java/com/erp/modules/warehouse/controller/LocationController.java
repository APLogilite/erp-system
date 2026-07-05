package com.erp.modules.warehouse.controller;

import com.erp.common.api.ApiResponse;
import com.erp.config.ApiVersionConfig;
import com.erp.modules.warehouse.dto.LocationDTO;
import com.erp.modules.warehouse.entity.Location;
import com.erp.modules.warehouse.repository.LocationRepository;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiVersionConfig.API_V1 + "/locations")
public class LocationController {

    private final LocationRepository locationRepository;

    public LocationController(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<LocationDTO>> create(@RequestBody LocationDTO dto) {
        Location entity = mapToEntity(dto);
        Location saved = locationRepository.save(entity);
        return ResponseEntity.ok(ApiResponse.success(mapToDTO(saved), "Location created."));
    }

    @GetMapping("/by-warehouse/{warehouseId}")
    public ResponseEntity<ApiResponse<List<LocationDTO>>> getByWarehouse(@PathVariable UUID warehouseId) {
        List<LocationDTO> list = locationRepository.findByWarehouseId(warehouseId).stream()
                .map(this::mapToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(list, "Locations retrieved."));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<LocationDTO>> update(@PathVariable UUID id, @RequestBody LocationDTO dto) {
        Location existing = locationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Location not found"));
        existing.setCode(dto.getCode());
        existing.setName(dto.getName());
        existing.setParentId(dto.getParentId());
        existing.setLocationType(dto.getLocationType());
        Location saved = locationRepository.save(existing);
        return ResponseEntity.ok(ApiResponse.success(mapToDTO(saved), "Location updated."));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        locationRepository.deleteById(id);
        return ResponseEntity.ok(ApiResponse.successMessage("Location deleted."));
    }

    private Location mapToEntity(LocationDTO dto) {
        Location entity = new Location();
        entity.setCode(dto.getCode());
        entity.setName(dto.getName());
        entity.setParentId(dto.getParentId());
        entity.setLocationType(dto.getLocationType());
        entity.setWarehouseId(dto.getWarehouseId());
        return entity;
    }

    private LocationDTO mapToDTO(Location entity) {
        LocationDTO dto = new LocationDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setName(entity.getName());
        dto.setParentId(entity.getParentId());
        dto.setLocationType(entity.getLocationType());
        dto.setWarehouseId(entity.getWarehouseId());
        dto.setIsActive(entity.getIsActive());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
