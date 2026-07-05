package com.erp.modules.manufacturing.controller;

import com.erp.common.api.ApiResponse;
import com.erp.config.ApiVersionConfig;
import com.erp.modules.manufacturing.dto.RoutingOperationResponse;
import com.erp.modules.manufacturing.dto.RoutingRequest;
import com.erp.modules.manufacturing.dto.RoutingResponse;
import com.erp.modules.manufacturing.entity.Routing;
import com.erp.modules.manufacturing.entity.RoutingOperation;
import com.erp.modules.manufacturing.service.RoutingService;
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
@RequestMapping(ApiVersionConfig.API_V1 + "/routings")
public class RoutingController {

  private final RoutingService routingService;

  public RoutingController(RoutingService routingService) {
    this.routingService = routingService;
  }

  @PostMapping
  public ResponseEntity<ApiResponse<UUID>> create(@RequestBody RoutingRequest request) {
    UUID id = routingService.createWithOperations(request);
    return ResponseEntity.ok(ApiResponse.success(id, "Routing created"));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<RoutingResponse>>> getAll() {
    List<RoutingResponse> list = routingService.findAll().stream()
        .map(this::toResponse).collect(Collectors.toList());
    return ResponseEntity.ok(ApiResponse.success(list, "Routings retrieved"));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<RoutingResponse>> getById(@PathVariable UUID id) {
    Routing routing = routingService.findByIdOrThrow(id);
    return ResponseEntity.ok(ApiResponse.success(toResponse(routing), "Routing retrieved"));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<RoutingResponse>> update(@PathVariable UUID id, @RequestBody RoutingRequest request) {
    Routing existing = routingService.findByIdOrThrow(id);
    existing.setCode(request.getCode());
    existing.setName(request.getName());
    existing.setDescription(request.getDescription());
    Routing updated = routingService.update(existing);
    return ResponseEntity.ok(ApiResponse.success(toResponse(updated), "Routing updated"));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
    routingService.delete(id);
    return ResponseEntity.ok(ApiResponse.successMessage("Routing deleted"));
  }

  @GetMapping("/{id}/operations")
  public ResponseEntity<ApiResponse<List<RoutingOperationResponse>>> getOperations(@PathVariable UUID id) {
    List<RoutingOperation> ops = routingService.getOperations(id);
    List<RoutingOperationResponse> list = ops.stream().map(this::toOperationResponse).collect(Collectors.toList());
    return ResponseEntity.ok(ApiResponse.success(list, "Operations retrieved"));
  }

  private RoutingResponse toResponse(Routing routing) {
    RoutingResponse r = new RoutingResponse();
    r.setId(routing.getId());
    r.setCode(routing.getCode());
    r.setName(routing.getName());
    r.setDescription(routing.getDescription());
    r.setIsActive(routing.getIsActive());
    r.setCreatedAt(routing.getCreatedAt());
    r.setUpdatedAt(routing.getUpdatedAt());
    try {
      List<RoutingOperationResponse> ops = routingService.getOperations(routing.getId()).stream()
          .map(this::toOperationResponse).collect(Collectors.toList());
      r.setOperations(ops);
    } catch (Exception e) {
      r.setOperations(List.of());
    }
    return r;
  }

  private RoutingOperationResponse toOperationResponse(RoutingOperation op) {
    RoutingOperationResponse r = new RoutingOperationResponse();
    r.setId(op.getId());
    r.setRoutingId(op.getRoutingId());
    r.setSequence(op.getSequence());
    r.setWorkCenterId(op.getWorkCenterId());
    r.setOperationName(op.getOperationName());
    r.setSetupTime(op.getSetupTime());
    r.setRunTime(op.getRunTime());
    r.setQueueTime(op.getQueueTime());
    return r;
  }
}
