package com.erp.modules.order.controller;

import com.erp.config.ApiVersionConfig;
import com.erp.common.api.ApiResponse;
import com.erp.modules.order.dto.OrderRequestDTO;
import com.erp.modules.order.dto.OrderResponseDTO;
import com.erp.modules.order.dto.OrderLineRequestDTO;
import com.erp.modules.order.dto.OrderLineResponseDTO;
import com.erp.modules.order.dto.SalesOrderCreateRequestDto;
import com.erp.modules.order.entity.Order;
import com.erp.modules.order.entity.OrderLine;
import com.erp.modules.order.service.OrderService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Order controller.
 * Base path: /api/v1/orders
 */
@RestController
@RequestMapping(ApiVersionConfig.API_V1 + "/orders")
public class OrderController {

  private final OrderService orderService;

  public OrderController(OrderService orderService) {
    this.orderService = orderService;
  }

  /**
   * Create a new order.
   * POST /api/v1/orders
   */
  @PostMapping
  public ResponseEntity<OrderResponseDTO> createOrder(@RequestBody OrderRequestDTO dto) {
    Order order = mapToEntity(dto);
    Order saved = orderService.create(order);

    // Create order lines if provided
    if (dto.getLines() != null && !dto.getLines().isEmpty()) {
      List<OrderLine> lines = dto.getLines().stream()
          .map(this::mapLineToEntity)
          .collect(Collectors.toList());
      orderService.createOrderLines(saved.getId(), lines);
      // Reload to get updated totalAmount
      saved = orderService.findByIdOrThrow(saved.getId());
    }

    OrderResponseDTO response = mapToResponse(saved);
    return ResponseEntity.ok(response);
  }

  /**
   * Create a new sales order with nested lines in a single request.
   * POST /api/v1/orders/sales-orders/nested
   * M2 requirement: header + lines saved together in one transaction
   */
  @PostMapping("/sales-orders/nested")
  public ResponseEntity<ApiResponse<UUID>> createSalesOrderNested(
      @RequestBody SalesOrderCreateRequestDto requestDto) {
    UUID orderId = orderService.createSalesOrderWithLines(requestDto);
    return ResponseEntity.ok(
        new ApiResponse<>(
            true,
            orderId,
            "Sales order created successfully with lines",
            null,
            null));
  }

  /**
   * Get all active orders.
   * GET /api/v1/orders
   */
  @GetMapping
  public ResponseEntity<List<OrderResponseDTO>> getOrders() {
    List<Order> orders = orderService.findAll();
    List<OrderResponseDTO> responses = orders.stream()
        .map(this::mapToResponse)
        .collect(Collectors.toList());
    return ResponseEntity.ok(responses);
  }

  /**
   * Get order by ID.
   * GET /api/v1/orders/{id}
   */
  @GetMapping("/{id}")
  public ResponseEntity<OrderResponseDTO> getOrder(@PathVariable UUID id) {
    Optional<Order> order = orderService.findById(id);
    if (order.isEmpty()) {
      return ResponseEntity.notFound().build();
    }
    OrderResponseDTO response = mapToResponse(order.get());
    return ResponseEntity.ok(response);
  }

  /**
   * Update an order.
   * PUT /api/v1/orders/{id}
   */
  @PutMapping("/{id}")
  public ResponseEntity<OrderResponseDTO> updateOrder(
      @PathVariable UUID id, @RequestBody OrderRequestDTO dto) {
    try {
      Order order = mapToEntity(dto);
      order.setId(id);
      Order updated = orderService.update(order);
      OrderResponseDTO response = mapToResponse(updated);
      return ResponseEntity.ok(response);
    } catch (IllegalArgumentException e) {
      if (e.getMessage().contains("COMPLETED")) {
        return ResponseEntity.badRequest().build();
      }
      return ResponseEntity.notFound().build();
    }
  }

  /**
   * Confirm an order and create stock movements.
   * POST /api/v1/orders/{id}/confirm
   * @param warehouseId UUID of the warehouse for stock movement
   */
  @PostMapping("/{id}/confirm")
  public ResponseEntity<OrderResponseDTO> confirmOrder(
      @PathVariable UUID id, @RequestParam UUID warehouseId) {
    try {
      orderService.confirmOrder(id, warehouseId);
      Order confirmed = orderService.findByIdOrThrow(id);
      OrderResponseDTO response = mapToResponse(confirmed);
      return ResponseEntity.ok(response);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().build();
    }
  }

  /**
   * Delete (soft delete) an order. Only allowed if status is DRAFT.
   * DELETE /api/v1/orders/{id}
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteOrder(@PathVariable UUID id) {
    try {
      Order order = orderService.findByIdOrThrow(id);
      if (!"DRAFT".equals(order.getStatus())) {
        return ResponseEntity.badRequest().build(); // Can only delete DRAFT orders
      }
      orderService.delete(id);
      return ResponseEntity.noContent().build();
    } catch (IllegalArgumentException e) {
      return ResponseEntity.notFound().build();
    }
  }

  /**
   * Get order lines for a specific order.
   * GET /api/v1/orders/{id}/lines
   */
  @GetMapping("/{id}/lines")
  public ResponseEntity<List<OrderLineResponseDTO>> getOrderLines(@PathVariable UUID id) {
    try {
      List<OrderLine> lines = orderService.getOrderLines(id);
      List<OrderLineResponseDTO> responses = lines.stream()
          .map(this::mapLineToResponse)
          .collect(Collectors.toList());
      return ResponseEntity.ok(responses);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.notFound().build();
    }
  }

  // Helper methods for mapping
  private Order mapToEntity(OrderRequestDTO dto) {
    Order order = new Order();
    order.setOrderType(dto.getOrderType());
    order.setPartyId(dto.getPartyId());
    order.setOrderDate(dto.getOrderDate());
    if (dto.getStatus() != null) {
      order.setStatus(dto.getStatus());
    }
    return order;
  }

  private OrderLine mapLineToEntity(OrderLineRequestDTO dto) {
    OrderLine line = new OrderLine();
    line.setProductId(dto.getProductId());
    line.setQuantity(dto.getQuantity());
    line.setUnitPrice(dto.getUnitPrice());
    return line;
  }

  private OrderResponseDTO mapToResponse(Order order) {
    OrderResponseDTO dto = new OrderResponseDTO();
    dto.setId(order.getId());
    dto.setOrderNumber(order.getOrderNumber());
    dto.setOrderType(order.getOrderType());
    dto.setPartyId(order.getPartyId());
    dto.setOrderDate(order.getOrderDate());
    dto.setStatus(order.getStatus());
    dto.setTotalAmount(order.getTotalAmount());
    dto.setCreatedAt(order.getCreatedAt());
    dto.setUpdatedAt(order.getUpdatedAt());
    dto.setIsActive(order.getIsActive());

    // Include order lines
    try {
      List<OrderLine> lines = orderService.getOrderLines(order.getId());
      List<OrderLineResponseDTO> lineResponses = lines.stream()
          .map(this::mapLineToResponse)
          .collect(Collectors.toList());
      dto.setLines(lineResponses);
    } catch (Exception e) {
      // Lines not available, set empty list
      dto.setLines(List.of());
    }

    return dto;
  }

  private OrderLineResponseDTO mapLineToResponse(OrderLine line) {
    OrderLineResponseDTO dto = new OrderLineResponseDTO();
    dto.setId(line.getId());
    dto.setOrderId(line.getOrderId());
    dto.setProductId(line.getProductId());
    dto.setQuantity(line.getQuantity());
    dto.setUnitPrice(line.getUnitPrice());
    dto.setLineTotal(line.getLineTotal());
    return dto;
  }
}
