package com.erp.modules.manufacturing.controller;

import com.erp.common.api.ApiResponse;
import com.erp.config.ApiVersionConfig;
import com.erp.modules.manufacturing.dto.ManufacturingOrderRequest;
import com.erp.modules.manufacturing.dto.ManufacturingOrderResponse;
import com.erp.modules.manufacturing.dto.WorkOrderResponse;
import com.erp.modules.manufacturing.entity.ManufacturingOrder;
import com.erp.modules.manufacturing.entity.WorkOrder;
import com.erp.modules.manufacturing.service.ManufacturingOrderService;
import com.erp.modules.manufacturing.service.WorkOrderService;
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
@RequestMapping(ApiVersionConfig.API_V1 + "/manufacturing-orders")
public class ManufacturingOrderController {

  private final ManufacturingOrderService moService;
  private final WorkOrderService workOrderService;

  public ManufacturingOrderController(ManufacturingOrderService moService,
                                      WorkOrderService workOrderService) {
    this.moService = moService;
    this.workOrderService = workOrderService;
  }

  @PostMapping
  public ResponseEntity<ApiResponse<UUID>> create(@RequestBody ManufacturingOrderRequest request) {
    ManufacturingOrder mo = new ManufacturingOrder();
    mo.setDocumentNo(request.getDocumentNo());
    mo.setProductId(request.getProductId());
    mo.setBomId(request.getBomId());
    mo.setRoutingId(request.getRoutingId());
    mo.setWarehouseId(request.getWarehouseId());
    mo.setPlannedQuantity(request.getPlannedQuantity());
    mo.setPlannedStart(request.getPlannedStart());
    mo.setPlannedEnd(request.getPlannedEnd());
    mo.setPriority(request.getPriority());
    ManufacturingOrder saved = moService.create(mo);
    return ResponseEntity.ok(ApiResponse.success(saved.getId(), "Manufacturing order created"));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<ManufacturingOrderResponse>>> getAll() {
    List<ManufacturingOrderResponse> list = moService.findAll().stream()
        .map(this::toResponse).collect(Collectors.toList());
    return ResponseEntity.ok(ApiResponse.success(list, "Manufacturing orders retrieved"));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<ManufacturingOrderResponse>> getById(@PathVariable UUID id) {
    ManufacturingOrder mo = moService.findByIdOrThrow(id);
    return ResponseEntity.ok(ApiResponse.success(toResponse(mo), "Manufacturing order retrieved"));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<ManufacturingOrderResponse>> update(
      @PathVariable UUID id, @RequestBody ManufacturingOrderRequest request) {
    ManufacturingOrder existing = moService.findByIdOrThrow(id);
    existing.setProductId(request.getProductId());
    existing.setBomId(request.getBomId());
    existing.setRoutingId(request.getRoutingId());
    existing.setWarehouseId(request.getWarehouseId());
    existing.setPlannedQuantity(request.getPlannedQuantity());
    existing.setPlannedStart(request.getPlannedStart());
    existing.setPlannedEnd(request.getPlannedEnd());
    existing.setPriority(request.getPriority());
    ManufacturingOrder updated = moService.update(existing);
    return ResponseEntity.ok(ApiResponse.success(toResponse(updated), "Manufacturing order updated"));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
    ManufacturingOrder mo = moService.findByIdOrThrow(id);
    if (!"DRAFT".equals(mo.getStatus()) && !"PLANNED".equals(mo.getStatus())) {
      throw new IllegalArgumentException("Only DRAFT or PLANNED orders can be deleted");
    }
    moService.delete(id);
    return ResponseEntity.ok(ApiResponse.successMessage("Manufacturing order deleted"));
  }

  @GetMapping("/{id}/work-orders")
  public ResponseEntity<ApiResponse<List<WorkOrderResponse>>> getWorkOrders(@PathVariable UUID id) {
    List<WorkOrder> workOrders = workOrderService.getByManufacturingOrder(id);
    List<WorkOrderResponse> list = workOrders.stream().map(this::toWorkOrderResponse).collect(Collectors.toList());
    return ResponseEntity.ok(ApiResponse.success(list, "Work orders retrieved"));
  }

  @PostMapping("/{id}/plan")
  public ResponseEntity<ApiResponse<Void>> plan(@PathVariable UUID id) {
    moService.plan(id);
    return ResponseEntity.ok(ApiResponse.successMessage("Order planned"));
  }

  @PostMapping("/{id}/release")
  public ResponseEntity<ApiResponse<Void>> release(@PathVariable UUID id) {
    moService.release(id);
    return ResponseEntity.ok(ApiResponse.successMessage("Order released"));
  }

  @PostMapping("/{id}/start")
  public ResponseEntity<ApiResponse<Void>> startProduction(@PathVariable UUID id) {
    moService.startProduction(id);
    return ResponseEntity.ok(ApiResponse.successMessage("Production started"));
  }

  @PostMapping("/{id}/complete")
  public ResponseEntity<ApiResponse<Void>> complete(@PathVariable UUID id) {
    moService.complete(id);
    return ResponseEntity.ok(ApiResponse.successMessage("Production completed"));
  }

  @PostMapping("/{id}/close")
  public ResponseEntity<ApiResponse<Void>> close(@PathVariable UUID id) {
    moService.close(id);
    return ResponseEntity.ok(ApiResponse.successMessage("Order closed"));
  }

  @PostMapping("/{id}/void")
  public ResponseEntity<ApiResponse<Void>> voidOrder(@PathVariable UUID id) {
    moService.voidOrder(id);
    return ResponseEntity.ok(ApiResponse.successMessage("Order voided"));
  }

  private ManufacturingOrderResponse toResponse(ManufacturingOrder mo) {
    ManufacturingOrderResponse r = new ManufacturingOrderResponse();
    r.setId(mo.getId());
    r.setDocumentNo(mo.getDocumentNo());
    r.setProductId(mo.getProductId());
    r.setBomId(mo.getBomId());
    r.setRoutingId(mo.getRoutingId());
    r.setWarehouseId(mo.getWarehouseId());
    r.setPlannedQuantity(mo.getPlannedQuantity());
    r.setCompletedQuantity(mo.getCompletedQuantity());
    r.setPlannedStart(mo.getPlannedStart());
    r.setPlannedEnd(mo.getPlannedEnd());
    r.setStatus(mo.getStatus());
    r.setPriority(mo.getPriority());
    r.setIsActive(mo.getIsActive());
    r.setCreatedAt(mo.getCreatedAt());
    r.setUpdatedAt(mo.getUpdatedAt());
    return r;
  }

  private WorkOrderResponse toWorkOrderResponse(WorkOrder wo) {
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
