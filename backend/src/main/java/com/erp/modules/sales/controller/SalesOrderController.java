package com.erp.modules.sales.controller;

import com.erp.common.api.ApiResponse;
import com.erp.config.ApiVersionConfig;
import com.erp.modules.sales.dto.SalesOrderLineRequest;
import com.erp.modules.sales.dto.SalesOrderLineResponse;
import com.erp.modules.sales.dto.SalesOrderRequest;
import com.erp.modules.sales.dto.SalesOrderResponse;
import com.erp.modules.sales.entity.SalesOrder;
import com.erp.modules.sales.entity.SalesOrderLine;
import com.erp.modules.sales.service.SalesOrderService;
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
@RequestMapping(ApiVersionConfig.API_V1 + "/sales-orders")
public class SalesOrderController {

  private final SalesOrderService salesOrderService;

  public SalesOrderController(SalesOrderService salesOrderService) {
    this.salesOrderService = salesOrderService;
  }

  @PostMapping
  public ResponseEntity<ApiResponse<UUID>> create(@RequestBody SalesOrderRequest request) {
    UUID id = salesOrderService.createWithLines(request);
    return ResponseEntity.ok(ApiResponse.success(id, "Sales order created"));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<SalesOrderResponse>>> getAll() {
    List<SalesOrderResponse> list = salesOrderService.findAll().stream()
        .map(this::toResponse)
        .collect(Collectors.toList());
    return ResponseEntity.ok(ApiResponse.success(list, "Sales orders retrieved"));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<SalesOrderResponse>> getById(@PathVariable UUID id) {
    SalesOrder order = salesOrderService.findByIdOrThrow(id);
    return ResponseEntity.ok(ApiResponse.success(toResponse(order), "Sales order retrieved"));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<SalesOrderResponse>> update(
      @PathVariable UUID id, @RequestBody SalesOrderRequest request) {
    SalesOrder existing = salesOrderService.findByIdOrThrow(id);
    existing.setDocumentDate(request.getDocumentDate());
    existing.setCustomerId(request.getCustomerId());
    existing.setWarehouseId(request.getWarehouseId());
    existing.setDescription(request.getDescription());
    existing.setCurrency(request.getCurrency());
    SalesOrder updated = salesOrderService.update(existing);
    return ResponseEntity.ok(ApiResponse.success(toResponse(updated), "Sales order updated"));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
    SalesOrder order = salesOrderService.findByIdOrThrow(id);
    if (!"DRAFT".equals(order.getStatus())) {
      throw new IllegalArgumentException("Only DRAFT orders can be deleted");
    }
    salesOrderService.delete(id);
    return ResponseEntity.ok(ApiResponse.successMessage("Sales order deleted"));
  }

  @PostMapping("/{id}/lines")
  public ResponseEntity<ApiResponse<Void>> addLines(
      @PathVariable UUID id, @RequestBody List<SalesOrderLineRequest> lines) {
    salesOrderService.addLines(id, lines);
    return ResponseEntity.ok(ApiResponse.successMessage("Lines added"));
  }

  @GetMapping("/{id}/lines")
  public ResponseEntity<ApiResponse<List<SalesOrderLineResponse>>> getLines(@PathVariable UUID id) {
    List<SalesOrderLine> lines = salesOrderService.getLines(id);
    List<SalesOrderLineResponse> list = lines.stream().map(this::toLineResponse).collect(Collectors.toList());
    return ResponseEntity.ok(ApiResponse.success(list, "Lines retrieved"));
  }

  @PostMapping("/{id}/recalculate")
  public ResponseEntity<ApiResponse<Void>> recalculate(@PathVariable UUID id) {
    salesOrderService.recalculate(id);
    return ResponseEntity.ok(ApiResponse.successMessage("Order recalculated"));
  }

  @PostMapping("/{id}/complete")
  public ResponseEntity<ApiResponse<Void>> complete(@PathVariable UUID id) {
    salesOrderService.completeOrder(id);
    return ResponseEntity.ok(ApiResponse.successMessage("Order completed"));
  }

  @PostMapping("/{id}/approve")
  public ResponseEntity<ApiResponse<Void>> approve(@PathVariable UUID id) {
    salesOrderService.approveOrder(id);
    return ResponseEntity.ok(ApiResponse.successMessage("Order approved"));
  }

  @PostMapping("/{id}/close")
  public ResponseEntity<ApiResponse<Void>> close(@PathVariable UUID id) {
    salesOrderService.closeOrder(id);
    return ResponseEntity.ok(ApiResponse.successMessage("Order closed"));
  }

  @PostMapping("/{id}/reopen")
  public ResponseEntity<ApiResponse<Void>> reopen(@PathVariable UUID id) {
    salesOrderService.reopenOrder(id);
    return ResponseEntity.ok(ApiResponse.successMessage("Order reopened"));
  }

  @PostMapping("/{id}/void")
  public ResponseEntity<ApiResponse<Void>> voidOrder(@PathVariable UUID id) {
    salesOrderService.voidOrder(id);
    return ResponseEntity.ok(ApiResponse.successMessage("Order voided"));
  }

  private SalesOrderResponse toResponse(SalesOrder order) {
    SalesOrderResponse r = new SalesOrderResponse();
    r.setId(order.getId());
    r.setDocumentNo(order.getDocumentNo());
    r.setDocumentDate(order.getDocumentDate());
    r.setCustomerId(order.getCustomerId());
    r.setWarehouseId(order.getWarehouseId());
    r.setStatus(order.getStatus());
    r.setDescription(order.getDescription());
    r.setTotalAmount(order.getTotalAmount());
    r.setCurrency(order.getCurrency());
    r.setCreatedAt(order.getCreatedAt());
    r.setUpdatedAt(order.getUpdatedAt());
    r.setIsActive(order.getIsActive());
    try {
      List<SalesOrderLineResponse> lines = salesOrderService.getLines(order.getId()).stream()
          .map(this::toLineResponse).collect(Collectors.toList());
      r.setLines(lines);
    } catch (Exception e) {
      r.setLines(List.of());
    }
    return r;
  }

  private SalesOrderLineResponse toLineResponse(SalesOrderLine line) {
    SalesOrderLineResponse r = new SalesOrderLineResponse();
    r.setId(line.getId());
    r.setOrderId(line.getOrderId());
    r.setLineNo(line.getLineNo());
    r.setProductId(line.getProductId());
    r.setDescription(line.getDescription());
    r.setQuantity(line.getQuantity());
    r.setUom(line.getUom());
    r.setUnitPrice(line.getUnitPrice());
    r.setDiscount(line.getDiscount());
    r.setLineAmount(line.getLineAmount());
    return r;
  }
}
