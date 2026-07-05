package com.erp.modules.manufacturing.controller;

import com.erp.common.api.ApiResponse;
import com.erp.config.ApiVersionConfig;
import com.erp.modules.manufacturing.dto.WorkOrderRequest;
import com.erp.modules.manufacturing.dto.WorkOrderResponse;
import com.erp.modules.manufacturing.entity.WorkOrder;
import com.erp.modules.manufacturing.service.WorkOrderService;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiVersionConfig.API_V1 + "/work-orders")
public class WorkOrderController {

  private final WorkOrderService workOrderService;

  public WorkOrderController(WorkOrderService workOrderService) {
    this.workOrderService = workOrderService;
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<WorkOrderResponse>>> getAll() {
    List<WorkOrderResponse> list = workOrderService.findAll().stream()
        .map(this::toResponse).collect(Collectors.toList());
    return ResponseEntity.ok(ApiResponse.success(list, "Work orders retrieved"));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<WorkOrderResponse>> getById(@PathVariable UUID id) {
    WorkOrder wo = workOrderService.findByIdOrThrow(id);
    return ResponseEntity.ok(ApiResponse.success(toResponse(wo), "Work order retrieved"));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<WorkOrderResponse>> update(@PathVariable UUID id, @RequestBody WorkOrderRequest request) {
    WorkOrder existing = workOrderService.findByIdOrThrow(id);
    existing.setSequence(request.getSequence());
    existing.setOperationId(request.getOperationId());
    existing.setWorkCenterId(request.getWorkCenterId());
    existing.setPlannedStart(request.getPlannedStart());
    existing.setPlannedEnd(request.getPlannedEnd());
    WorkOrder updated = workOrderService.update(existing);
    return ResponseEntity.ok(ApiResponse.success(toResponse(updated), "Work order updated"));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
    workOrderService.delete(id);
    return ResponseEntity.ok(ApiResponse.successMessage("Work order deleted"));
  }

  @GetMapping("/by-mo/{moId}")
  public ResponseEntity<ApiResponse<List<WorkOrderResponse>>> getByManufacturingOrder(@PathVariable UUID moId) {
    List<WorkOrderResponse> list = workOrderService.getByManufacturingOrder(moId).stream()
        .map(this::toResponse).collect(Collectors.toList());
    return ResponseEntity.ok(ApiResponse.success(list, "Work orders by MO retrieved"));
  }

  @GetMapping("/by-work-center/{workCenterId}")
  public ResponseEntity<ApiResponse<List<WorkOrderResponse>>> getByWorkCenter(@PathVariable UUID workCenterId) {
    List<WorkOrderResponse> list = workOrderService.getByWorkCenter(workCenterId).stream()
        .map(this::toResponse).collect(Collectors.toList());
    return ResponseEntity.ok(ApiResponse.success(list, "Work orders by work center retrieved"));
  }

  @PostMapping("/{id}/start")
  public ResponseEntity<ApiResponse<Void>> start(@PathVariable UUID id) {
    workOrderService.start(id);
    return ResponseEntity.ok(ApiResponse.successMessage("Work order started"));
  }

  @PostMapping("/{id}/complete")
  public ResponseEntity<ApiResponse<Void>> complete(@PathVariable UUID id) {
    workOrderService.complete(id);
    return ResponseEntity.ok(ApiResponse.successMessage("Work order completed"));
  }

  @PostMapping("/{id}/close")
  public ResponseEntity<ApiResponse<Void>> close(@PathVariable UUID id) {
    workOrderService.close(id);
    return ResponseEntity.ok(ApiResponse.successMessage("Work order closed"));
  }

  private WorkOrderResponse toResponse(WorkOrder wo) {
    WorkOrderResponse r = new WorkOrderResponse();
    r.setId(wo.getId());
    r.setManufacturingOrderId(wo.getManufacturingOrderId());
    r.setSequence(wo.getSequence());
    r.setOperationId(wo.getOperationId());
    r.setWorkCenterId(wo.getWorkCenterId());
    r.setPlannedStart(wo.getPlannedStart());
    r.setPlannedEnd(wo.getPlannedEnd());
    r.setActualStart(wo.getActualStart());
    r.setActualEnd(wo.getActualEnd());
    r.setStatus(wo.getStatus());
    r.setIsActive(wo.getIsActive());
    r.setCreatedAt(wo.getCreatedAt());
    r.setUpdatedAt(wo.getUpdatedAt());
    return r;
  }
}
