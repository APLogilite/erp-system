package com.erp.modules.service.controller;

import com.erp.common.api.ApiResponse;
import com.erp.config.ApiVersionConfig;
import com.erp.modules.service.dto.ServiceRequestRequest;
import com.erp.modules.service.dto.ServiceRequestResponse;
import com.erp.modules.service.entity.ServiceRequest;
import com.erp.modules.service.service.ServiceRequestService;
import java.util.List;
import java.util.Map;
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
@RequestMapping(ApiVersionConfig.API_V1 + "/service-requests")
public class ServiceRequestController {

  private final ServiceRequestService serviceRequestService;

  public ServiceRequestController(ServiceRequestService serviceRequestService) {
    this.serviceRequestService = serviceRequestService;
  }

  @PostMapping
  public ResponseEntity<ApiResponse<UUID>> create(@RequestBody ServiceRequestRequest request) {
    ServiceRequest entity = new ServiceRequest();
    entity.setCustomerId(request.getCustomerId());
    entity.setAssetId(request.getAssetId());
    entity.setPriority(request.getPriority());
    entity.setCategory(request.getCategory());
    entity.setDescription(request.getDescription());
    ServiceRequest saved = serviceRequestService.create(entity);
    return ResponseEntity.ok(ApiResponse.success(saved.getId(), "Service request created"));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<ServiceRequestResponse>>> getAll() {
    List<ServiceRequestResponse> list = serviceRequestService.findAll().stream()
        .map(this::toResponse).collect(Collectors.toList());
    return ResponseEntity.ok(ApiResponse.success(list, "Service requests retrieved"));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<ServiceRequestResponse>> getById(@PathVariable UUID id) {
    ServiceRequest entity = serviceRequestService.findByIdOrThrow(id);
    return ResponseEntity.ok(ApiResponse.success(toResponse(entity), "Service request retrieved"));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<ServiceRequestResponse>> update(@PathVariable UUID id, @RequestBody ServiceRequestRequest request) {
    ServiceRequest existing = serviceRequestService.findByIdOrThrow(id);
    existing.setCustomerId(request.getCustomerId());
    existing.setAssetId(request.getAssetId());
    existing.setPriority(request.getPriority());
    existing.setCategory(request.getCategory());
    existing.setDescription(request.getDescription());
    ServiceRequest updated = serviceRequestService.update(existing);
    return ResponseEntity.ok(ApiResponse.success(toResponse(updated), "Service request updated"));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
    serviceRequestService.delete(id);
    return ResponseEntity.ok(ApiResponse.successMessage("Service request deleted"));
  }

  @PostMapping("/{id}/assign")
  public ResponseEntity<ApiResponse<Void>> assign(@PathVariable UUID id, @RequestBody Map<String, UUID> body) {
    serviceRequestService.assign(id, body.get("engineerId"));
    return ResponseEntity.ok(ApiResponse.successMessage("Engineer assigned"));
  }

  @PostMapping("/{id}/start")
  public ResponseEntity<ApiResponse<Void>> start(@PathVariable UUID id) {
    serviceRequestService.start(id);
    return ResponseEntity.ok(ApiResponse.successMessage("Service started"));
  }

  @PostMapping("/{id}/resolve")
  public ResponseEntity<ApiResponse<Void>> resolve(@PathVariable UUID id, @RequestBody Map<String, String> body) {
    serviceRequestService.resolve(id, body.get("resolution"));
    return ResponseEntity.ok(ApiResponse.successMessage("Service resolved"));
  }

  @PostMapping("/{id}/close")
  public ResponseEntity<ApiResponse<Void>> close(@PathVariable UUID id) {
    serviceRequestService.close(id);
    return ResponseEntity.ok(ApiResponse.successMessage("Service closed"));
  }

  private ServiceRequestResponse toResponse(ServiceRequest entity) {
    ServiceRequestResponse r = new ServiceRequestResponse();
    r.setId(entity.getId());
    r.setTicketNumber(entity.getTicketNumber());
    r.setCustomerId(entity.getCustomerId());
    r.setAssetId(entity.getAssetId());
    r.setPriority(entity.getPriority());
    r.setCategory(entity.getCategory());
    r.setAssignedEngineerId(entity.getAssignedEngineerId());
    r.setStatus(entity.getStatus());
    r.setDescription(entity.getDescription());
    r.setResolution(entity.getResolution());
    r.setIsActive(entity.getIsActive());
    r.setCreatedAt(entity.getCreatedAt());
    r.setUpdatedAt(entity.getUpdatedAt());
    return r;
  }
}
