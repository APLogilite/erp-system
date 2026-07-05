package com.erp.modules.assets.controller;

import com.erp.common.api.ApiResponse;
import com.erp.config.ApiVersionConfig;
import com.erp.modules.assets.dto.AssetRequest;
import com.erp.modules.assets.dto.AssetResponse;
import com.erp.modules.assets.entity.Asset;
import com.erp.modules.assets.service.AssetService;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiVersionConfig.API_V1 + "/assets")
public class AssetController {

  private final AssetService assetService;

  public AssetController(AssetService assetService) {
    this.assetService = assetService;
  }

  @PostMapping
  public ResponseEntity<ApiResponse<AssetResponse>> create(@RequestBody AssetRequest request) {
    Asset entity = new Asset();
    entity.setAssetCode(request.getAssetCode());
    entity.setAssetName(request.getAssetName());
    entity.setAssetType(request.getAssetType());
    entity.setPurchaseDate(request.getPurchaseDate());
    entity.setPurchaseCost(request.getPurchaseCost());
    entity.setAssignedTo(request.getAssignedTo());
    entity.setLocation(request.getLocation());
    Asset saved = assetService.create(entity);
    return ResponseEntity.ok(ApiResponse.success(toResponse(saved), "Asset created"));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<AssetResponse>>> getAll() {
    List<AssetResponse> list = assetService.findAll().stream()
        .map(this::toResponse).collect(Collectors.toList());
    return ResponseEntity.ok(ApiResponse.success(list, "Assets retrieved"));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<AssetResponse>> getById(@PathVariable UUID id) {
    Asset entity = assetService.findByIdOrThrow(id);
    return ResponseEntity.ok(ApiResponse.success(toResponse(entity), "Asset retrieved"));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<AssetResponse>> update(@PathVariable UUID id, @RequestBody AssetRequest request) {
    Asset existing = assetService.findByIdOrThrow(id);
    existing.setAssetCode(request.getAssetCode());
    existing.setAssetName(request.getAssetName());
    existing.setAssetType(request.getAssetType());
    existing.setPurchaseDate(request.getPurchaseDate());
    existing.setPurchaseCost(request.getPurchaseCost());
    existing.setAssignedTo(request.getAssignedTo());
    existing.setLocation(request.getLocation());
    Asset updated = assetService.update(existing);
    return ResponseEntity.ok(ApiResponse.success(toResponse(updated), "Asset updated"));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
    assetService.delete(id);
    return ResponseEntity.ok(ApiResponse.successMessage("Asset deleted"));
  }

  @PostMapping("/{id}/activate")
  public ResponseEntity<ApiResponse<Void>> activate(@PathVariable UUID id) {
    assetService.activate(id);
    return ResponseEntity.ok(ApiResponse.successMessage("Asset activated"));
  }

  @PostMapping("/{id}/assign")
  public ResponseEntity<ApiResponse<Void>> assign(@PathVariable UUID id, @RequestBody UUID employeeId) {
    assetService.assign(id, employeeId);
    return ResponseEntity.ok(ApiResponse.successMessage("Asset assigned"));
  }

  @PostMapping("/{id}/maintain")
  public ResponseEntity<ApiResponse<Void>> maintain(@PathVariable UUID id) {
    assetService.maintain(id);
    return ResponseEntity.ok(ApiResponse.successMessage("Asset sent to maintenance"));
  }

  @PostMapping("/{id}/dispose")
  public ResponseEntity<ApiResponse<Void>> dispose(@PathVariable UUID id) {
    assetService.dispose(id);
    return ResponseEntity.ok(ApiResponse.successMessage("Asset disposed"));
  }

  private AssetResponse toResponse(Asset entity) {
    AssetResponse r = new AssetResponse();
    r.setId(entity.getId());
    r.setAssetCode(entity.getAssetCode());
    r.setAssetName(entity.getAssetName());
    r.setAssetType(entity.getAssetType());
    r.setPurchaseDate(entity.getPurchaseDate());
    r.setPurchaseCost(entity.getPurchaseCost());
    r.setCurrentValue(entity.getCurrentValue());
    r.setAssignedTo(entity.getAssignedTo());
    r.setLocation(entity.getLocation());
    r.setStatus(entity.getStatus());
    r.setIsActive(entity.getIsActive());
    r.setCreatedAt(entity.getCreatedAt());
    r.setUpdatedAt(entity.getUpdatedAt());
    return r;
  }
}
