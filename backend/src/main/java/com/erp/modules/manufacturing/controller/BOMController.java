package com.erp.modules.manufacturing.controller;

import com.erp.common.api.ApiResponse;
import com.erp.config.ApiVersionConfig;
import com.erp.modules.manufacturing.dto.BOMLineRequest;
import com.erp.modules.manufacturing.dto.BOMLineResponse;
import com.erp.modules.manufacturing.dto.BOMRequest;
import com.erp.modules.manufacturing.dto.BOMResponse;
import com.erp.modules.manufacturing.entity.BOMLine;
import com.erp.modules.manufacturing.entity.BillOfMaterial;
import com.erp.modules.manufacturing.service.BOMService;
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
@RequestMapping(ApiVersionConfig.API_V1 + "/boms")
public class BOMController {

  private final BOMService bomService;

  public BOMController(BOMService bomService) {
    this.bomService = bomService;
  }

  @PostMapping
  public ResponseEntity<ApiResponse<UUID>> create(@RequestBody BOMRequest request) {
    UUID id = bomService.createWithLines(request);
    return ResponseEntity.ok(ApiResponse.success(id, "BOM created"));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<BOMResponse>>> getAll() {
    List<BOMResponse> list = bomService.findAll().stream()
        .map(this::toResponse).collect(Collectors.toList());
    return ResponseEntity.ok(ApiResponse.success(list, "BOMs retrieved"));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<BOMResponse>> getById(@PathVariable UUID id) {
    BillOfMaterial bom = bomService.findByIdOrThrow(id);
    return ResponseEntity.ok(ApiResponse.success(toResponse(bom), "BOM retrieved"));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<BOMResponse>> update(@PathVariable UUID id, @RequestBody BOMRequest request) {
    BillOfMaterial existing = bomService.findByIdOrThrow(id);
    existing.setCode(request.getCode());
    existing.setName(request.getName());
    existing.setProductId(request.getProductId());
    existing.setRevision(request.getRevision());
    existing.setVersion(request.getVersion());
    existing.setEffectiveFrom(request.getEffectiveFrom());
    existing.setEffectiveTo(request.getEffectiveTo());
    existing.setDescription(request.getDescription());
    BillOfMaterial updated = bomService.update(existing);
    return ResponseEntity.ok(ApiResponse.success(toResponse(updated), "BOM updated"));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
    bomService.delete(id);
    return ResponseEntity.ok(ApiResponse.successMessage("BOM deleted"));
  }

  @GetMapping("/{id}/lines")
  public ResponseEntity<ApiResponse<List<BOMLineResponse>>> getLines(@PathVariable UUID id) {
    List<BOMLine> lines = bomService.getLines(id);
    List<BOMLineResponse> list = lines.stream().map(this::toLineResponse).collect(Collectors.toList());
    return ResponseEntity.ok(ApiResponse.success(list, "BOM lines retrieved"));
  }

  @GetMapping("/by-product/{productId}")
  public ResponseEntity<ApiResponse<List<BOMResponse>>> getByProduct(@PathVariable UUID productId) {
    List<BOMResponse> list = bomService.getByProduct(productId).stream()
        .map(this::toResponse).collect(Collectors.toList());
    return ResponseEntity.ok(ApiResponse.success(list, "BOMs by product retrieved"));
  }

  @PostMapping("/{id}/approve")
  public ResponseEntity<ApiResponse<Void>> approve(@PathVariable UUID id) {
    bomService.approveBom(id);
    return ResponseEntity.ok(ApiResponse.successMessage("BOM approved"));
  }

  @PostMapping("/{id}/archive")
  public ResponseEntity<ApiResponse<Void>> archive(@PathVariable UUID id) {
    bomService.archiveBom(id);
    return ResponseEntity.ok(ApiResponse.successMessage("BOM archived"));
  }

  private BOMResponse toResponse(BillOfMaterial bom) {
    BOMResponse r = new BOMResponse();
    r.setId(bom.getId());
    r.setCode(bom.getCode());
    r.setName(bom.getName());
    r.setProductId(bom.getProductId());
    r.setRevision(bom.getRevision());
    r.setVersion(bom.getVersion());
    r.setStatus(bom.getStatus());
    r.setEffectiveFrom(bom.getEffectiveFrom());
    r.setEffectiveTo(bom.getEffectiveTo());
    r.setDescription(bom.getDescription());
    r.setIsActive(bom.getIsActive());
    r.setCreatedAt(bom.getCreatedAt());
    r.setUpdatedAt(bom.getUpdatedAt());
    try {
      List<BOMLineResponse> lines = bomService.getLines(bom.getId()).stream()
          .map(this::toLineResponse).collect(Collectors.toList());
      r.setLines(lines);
    } catch (Exception e) {
      r.setLines(List.of());
    }
    return r;
  }

  private BOMLineResponse toLineResponse(BOMLine line) {
    BOMLineResponse r = new BOMLineResponse();
    r.setId(line.getId());
    r.setBomId(line.getBomId());
    r.setLineNo(line.getLineNo());
    r.setComponentId(line.getComponentId());
    r.setQuantity(line.getQuantity());
    r.setUom(line.getUom());
    r.setScrapPercentage(line.getScrapPercentage());
    r.setOperationId(line.getOperationId());
    return r;
  }
}
