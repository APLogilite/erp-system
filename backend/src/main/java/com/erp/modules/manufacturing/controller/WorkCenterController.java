package com.erp.modules.manufacturing.controller;

import com.erp.common.api.ApiResponse;
import com.erp.config.ApiVersionConfig;
import com.erp.modules.manufacturing.dto.WorkCenterRequest;
import com.erp.modules.manufacturing.dto.WorkCenterResponse;
import com.erp.modules.manufacturing.entity.WorkCenter;
import com.erp.modules.manufacturing.service.WorkCenterService;
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
@RequestMapping(ApiVersionConfig.API_V1 + "/work-centers")
public class WorkCenterController {

  private final WorkCenterService workCenterService;

  public WorkCenterController(WorkCenterService workCenterService) {
    this.workCenterService = workCenterService;
  }

  @PostMapping
  public ResponseEntity<ApiResponse<WorkCenterResponse>> create(@RequestBody WorkCenterRequest request) {
    WorkCenter entity = new WorkCenter();
    entity.setCode(request.getCode());
    entity.setName(request.getName());
    entity.setCapacity(request.getCapacity());
    entity.setCostPerHour(request.getCostPerHour());
    entity.setEfficiency(request.getEfficiency());
    entity.setCalendar(request.getCalendar());
    WorkCenter saved = workCenterService.create(entity);
    return ResponseEntity.ok(ApiResponse.success(toResponse(saved), "Work center created"));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<WorkCenterResponse>>> getAll() {
    List<WorkCenterResponse> list = workCenterService.findAll().stream()
        .map(this::toResponse).collect(Collectors.toList());
    return ResponseEntity.ok(ApiResponse.success(list, "Work centers retrieved"));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<WorkCenterResponse>> getById(@PathVariable UUID id) {
    WorkCenter entity = workCenterService.findByIdOrThrow(id);
    return ResponseEntity.ok(ApiResponse.success(toResponse(entity), "Work center retrieved"));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<WorkCenterResponse>> update(@PathVariable UUID id, @RequestBody WorkCenterRequest request) {
    WorkCenter existing = workCenterService.findByIdOrThrow(id);
    existing.setCode(request.getCode());
    existing.setName(request.getName());
    existing.setCapacity(request.getCapacity());
    existing.setCostPerHour(request.getCostPerHour());
    existing.setEfficiency(request.getEfficiency());
    existing.setCalendar(request.getCalendar());
    WorkCenter updated = workCenterService.update(existing);
    return ResponseEntity.ok(ApiResponse.success(toResponse(updated), "Work center updated"));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
    workCenterService.delete(id);
    return ResponseEntity.ok(ApiResponse.successMessage("Work center deleted"));
  }

  private WorkCenterResponse toResponse(WorkCenter entity) {
    WorkCenterResponse r = new WorkCenterResponse();
    r.setId(entity.getId());
    r.setCode(entity.getCode());
    r.setName(entity.getName());
    r.setCapacity(entity.getCapacity());
    r.setCostPerHour(entity.getCostPerHour());
    r.setEfficiency(entity.getEfficiency());
    r.setCalendar(entity.getCalendar());
    r.setIsActive(entity.getIsActive());
    r.setCreatedAt(entity.getCreatedAt());
    r.setUpdatedAt(entity.getUpdatedAt());
    return r;
  }
}
