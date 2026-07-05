package com.erp.modules.purchase.controller;

import com.erp.common.api.ApiResponse;
import com.erp.config.ApiVersionConfig;
import com.erp.modules.purchase.dto.PurchaseOrderLineRequest;
import com.erp.modules.purchase.dto.PurchaseOrderLineResponse;
import com.erp.modules.purchase.dto.PurchaseOrderRequest;
import com.erp.modules.purchase.dto.PurchaseOrderResponse;
import com.erp.modules.purchase.entity.PurchaseOrder;
import com.erp.modules.purchase.entity.PurchaseOrderLine;
import com.erp.modules.purchase.service.PurchaseOrderService;
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
@RequestMapping(ApiVersionConfig.API_V1 + "/purchase-orders")
public class PurchaseOrderController {

  private final PurchaseOrderService purchaseOrderService;

  public PurchaseOrderController(PurchaseOrderService purchaseOrderService) {
    this.purchaseOrderService = purchaseOrderService;
  }

  @PostMapping
  public ResponseEntity<ApiResponse<UUID>> create(@RequestBody PurchaseOrderRequest request) {
    UUID id = purchaseOrderService.createWithLines(request);
    return ResponseEntity.ok(ApiResponse.success(id, "Purchase order created"));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<PurchaseOrderResponse>>> getAll() {
    List<PurchaseOrderResponse> list = purchaseOrderService.findAll().stream()
        .map(this::toResponse)
        .collect(Collectors.toList());
    return ResponseEntity.ok(ApiResponse.success(list, "Purchase orders retrieved"));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<PurchaseOrderResponse>> getById(@PathVariable UUID id) {
    PurchaseOrder order = purchaseOrderService.findByIdOrThrow(id);
    return ResponseEntity.ok(ApiResponse.success(toResponse(order), "Purchase order retrieved"));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<PurchaseOrderResponse>> update(
      @PathVariable UUID id, @RequestBody PurchaseOrderRequest request) {
    PurchaseOrder existing = purchaseOrderService.findByIdOrThrow(id);
    existing.setDocumentDate(request.getDocumentDate());
    existing.setVendorId(request.getVendorId());
    existing.setWarehouseId(request.getWarehouseId());
    existing.setDescription(request.getDescription());
    existing.setCurrency(request.getCurrency());
    existing.setExpectedDate(request.getExpectedDate());
    PurchaseOrder updated = purchaseOrderService.update(existing);
    return ResponseEntity.ok(ApiResponse.success(toResponse(updated), "Purchase order updated"));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
    PurchaseOrder order = purchaseOrderService.findByIdOrThrow(id);
    if (!"DRAFT".equals(order.getStatus())) {
      throw new IllegalArgumentException("Only DRAFT purchase orders can be deleted");
    }
    purchaseOrderService.delete(id);
    return ResponseEntity.ok(ApiResponse.successMessage("Purchase order deleted"));
  }

  @PostMapping("/{id}/lines")
  public ResponseEntity<ApiResponse<Void>> addLines(
      @PathVariable UUID id, @RequestBody List<PurchaseOrderLineRequest> lines) {
    purchaseOrderService.addLines(id, lines);
    return ResponseEntity.ok(ApiResponse.successMessage("Lines added"));
  }

  @GetMapping("/{id}/lines")
  public ResponseEntity<ApiResponse<List<PurchaseOrderLineResponse>>> getLines(@PathVariable UUID id) {
    List<PurchaseOrderLine> lines = purchaseOrderService.getLines(id);
    List<PurchaseOrderLineResponse> list = lines.stream().map(this::toLineResponse).collect(Collectors.toList());
    return ResponseEntity.ok(ApiResponse.success(list, "Lines retrieved"));
  }

  @PostMapping("/{id}/recalculate")
  public ResponseEntity<ApiResponse<Void>> recalculate(@PathVariable UUID id) {
    purchaseOrderService.recalculate(id);
    return ResponseEntity.ok(ApiResponse.successMessage("Order recalculated"));
  }

  @PostMapping("/{id}/complete")
  public ResponseEntity<ApiResponse<Void>> complete(@PathVariable UUID id) {
    purchaseOrderService.completeOrder(id);
    return ResponseEntity.ok(ApiResponse.successMessage("Order completed"));
  }

  @PostMapping("/{id}/approve")
  public ResponseEntity<ApiResponse<Void>> approve(@PathVariable UUID id) {
    purchaseOrderService.approveOrder(id);
    return ResponseEntity.ok(ApiResponse.successMessage("Order approved"));
  }

  @PostMapping("/{id}/receive")
  public ResponseEntity<ApiResponse<Void>> receive(@PathVariable UUID id) {
    purchaseOrderService.receiveOrder(id);
    return ResponseEntity.ok(ApiResponse.successMessage("Goods received"));
  }

  @PostMapping("/{id}/close")
  public ResponseEntity<ApiResponse<Void>> close(@PathVariable UUID id) {
    purchaseOrderService.closeOrder(id);
    return ResponseEntity.ok(ApiResponse.successMessage("Order closed"));
  }

  @PostMapping("/{id}/void")
  public ResponseEntity<ApiResponse<Void>> voidOrder(@PathVariable UUID id) {
    purchaseOrderService.voidOrder(id);
    return ResponseEntity.ok(ApiResponse.successMessage("Order voided"));
  }

  @PostMapping("/{id}/reopen")
  public ResponseEntity<ApiResponse<Void>> reopen(@PathVariable UUID id) {
    purchaseOrderService.reopenOrder(id);
    return ResponseEntity.ok(ApiResponse.successMessage("Order reopened"));
  }

  private PurchaseOrderResponse toResponse(PurchaseOrder order) {
    PurchaseOrderResponse r = new PurchaseOrderResponse();
    r.setId(order.getId());
    r.setDocumentNo(order.getDocumentNo());
    r.setDocumentDate(order.getDocumentDate());
    r.setVendorId(order.getVendorId());
    r.setWarehouseId(order.getWarehouseId());
    r.setStatus(order.getStatus());
    r.setDescription(order.getDescription());
    r.setCurrency(order.getCurrency());
    r.setTotalAmount(order.getTotalAmount());
    r.setExpectedDate(order.getExpectedDate());
    r.setCreatedAt(order.getCreatedAt());
    r.setUpdatedAt(order.getUpdatedAt());
    r.setIsActive(order.getIsActive());
    try {
      List<PurchaseOrderLineResponse> lines = purchaseOrderService.getLines(order.getId()).stream()
          .map(this::toLineResponse).collect(Collectors.toList());
      r.setLines(lines);
    } catch (Exception e) {
      r.setLines(List.of());
    }
    return r;
  }

  private PurchaseOrderLineResponse toLineResponse(PurchaseOrderLine line) {
    PurchaseOrderLineResponse r = new PurchaseOrderLineResponse();
    r.setId(line.getId());
    r.setOrderId(line.getOrderId());
    r.setLineNo(line.getLineNo());
    r.setProductId(line.getProductId());
    r.setDescription(line.getDescription());
    r.setQuantity(line.getQuantity());
    r.setReceivedQuantity(line.getReceivedQuantity());
    r.setUnitPrice(line.getUnitPrice());
    r.setDiscount(line.getDiscount());
    r.setLineAmount(line.getLineAmount());
    r.setExpectedDate(line.getExpectedDate());
    return r;
  }
}
