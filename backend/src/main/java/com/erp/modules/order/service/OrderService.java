package com.erp.modules.order.service;

import com.erp.common.base.BaseService;
import com.erp.modules.order.entity.Order;
import com.erp.modules.order.entity.OrderLine;
import com.erp.modules.order.repository.OrderRepository;
import com.erp.modules.order.repository.OrderLineRepository;
import com.erp.modules.order.dto.SalesOrderCreateRequestDto;
import com.erp.modules.inventory.entity.StockMovement;
import com.erp.modules.inventory.repository.StockMovementRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Order service.
 * Manages unified order system for SALES and PURCHASE orders.
 */
@Service
public class OrderService extends BaseService<Order> {

  private final OrderRepository orderRepository;
  private final OrderLineRepository orderLineRepository;
  private final StockMovementRepository stockMovementRepository;

  public OrderService(
      OrderRepository orderRepository,
      OrderLineRepository orderLineRepository,
      StockMovementRepository stockMovementRepository) {
    this.orderRepository = orderRepository;
    this.orderLineRepository = orderLineRepository;
    this.stockMovementRepository = stockMovementRepository;
  }

  @Override
  protected JpaRepository<Order, UUID> getRepository() {
    return orderRepository;
  }

  @Override
  protected void beforeCreate(Order entity) {
    // Generate order number based on type
    String orderNumber = generateOrderNumber(entity.getOrderType());
    entity.setOrderNumber(orderNumber);

    // Ensure status is DRAFT by default
    if (entity.getStatus() == null) {
      entity.setStatus("DRAFT");
    }

    // Set order date if not provided
    if (entity.getOrderDate() == null) {
      entity.setOrderDate(LocalDateTime.now());
    }

    // Initialize totalAmount to 0
    if (entity.getTotalAmount() == null) {
      entity.setTotalAmount(0.0);
    }

    // Validate orderType is not null
    if (entity.getOrderType() == null || entity.getOrderType().trim().isEmpty()) {
      throw new IllegalArgumentException("orderType must not be null");
    }
  }

  @Override
  protected void afterCreate(Order entity) {
    // Validation happens after entity is created, but lines are processed separately
  }

  @Override
  protected void beforeUpdate(Order newEntity, Order existingEntity) {
    // Prevent update if status is COMPLETED
    if ("COMPLETED".equals(existingEntity.getStatus())) {
      throw new IllegalArgumentException("Cannot update a COMPLETED order");
    }

    // Preserve order number
    newEntity.setOrderNumber(existingEntity.getOrderNumber());
  }

  @Override
  protected void afterUpdate(Order entity) {
    // Hook for after update logic
  }

  /**
   * Create order lines for the given order.
   * Validates quantity > 0 and computes lineTotal.
   * Updates order totalAmount.
   */
  @Transactional
  public void createOrderLines(UUID orderId, List<OrderLine> lines) {
    Order order =
        orderRepository
            .findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("Order not found for id: " + orderId));

    double totalAmount = 0.0;

    for (OrderLine line : lines) {
      // Validate quantity > 0
      if (line.getQuantity() <= 0) {
        throw new IllegalArgumentException("Quantity must be greater than 0");
      }

      // Set orderId and compute lineTotal
      line.setOrderId(orderId);
      line.setLineTotal(line.getQuantity() * line.getUnitPrice());
      totalAmount += line.getLineTotal();

      orderLineRepository.save(line);
    }

    // Update order totalAmount
    order.setTotalAmount(totalAmount);
    orderRepository.save(order);
  }

  /**
   * Confirm order and create stock movements.
   * If orderType = SALES, create negative stock movement.
   * If orderType = PURCHASE, create positive stock movement.
   */
  @Transactional
  public void confirmOrder(UUID orderId, UUID warehouseId) {
    Order order =
        orderRepository
            .findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("Order not found for id: " + orderId));

    // Get order lines
    List<OrderLine> lines = orderLineRepository.findByOrderId(orderId);

    // Create stock movements for each line
    for (OrderLine line : lines) {
      StockMovement movement = new StockMovement();
      movement.setProductId(line.getProductId());
      movement.setWarehouseId(warehouseId);
      movement.setMovementDate(LocalDateTime.now());
      movement.setReferenceId(orderId);

      if ("SALES".equals(order.getOrderType())) {
        // SALES: negative quantity (OUT)
        movement.setQuantity(-line.getQuantity());
        movement.setMovementType("SALE");
        movement.setReferenceType("SALES_ORDER");
      } else if ("PURCHASE".equals(order.getOrderType())) {
        // PURCHASE: positive quantity (IN)
        movement.setQuantity(line.getQuantity());
        movement.setMovementType("PURCHASE");
        movement.setReferenceType("PURCHASE_ORDER");
      } else {
        throw new IllegalArgumentException("Invalid orderType: " + order.getOrderType());
      }

      stockMovementRepository.save(movement);
    }

    // Update order status to CONFIRMED
    order.setStatus("CONFIRMED");
    orderRepository.save(order);
  }

  /**
   * Get all order lines for a given order.
   */
  @Transactional(readOnly = true)
  public List<OrderLine> getOrderLines(UUID orderId) {
    return orderLineRepository.findByOrderId(orderId);
  }

  /**
   * Generate order number based on type.
   * SALES → SO-0001, PURCHASE → PO-0001
   */
  private String generateOrderNumber(String orderType) {
    String prefix = "SALES".equals(orderType) ? "SO" : "PURCHASE".equals(orderType) ? "PO" : "ORD";

    // Get the count of orders with this type to generate sequential number
    List<Order> orders = orderRepository.findByOrderType(orderType);
    long nextNumber = orders.size() + 1;

    return String.format("%s-%04d", prefix, nextNumber);
  }

  /**
   * Create sales order with nested lines in a single transaction.
   * This implements the M2 nested form requirement: header + lines saved together.
   */
  @Transactional
  public UUID createSalesOrderWithLines(SalesOrderCreateRequestDto requestDto) {
    // Create and save order header
    Order order = new Order();
    order.setOrderType("SALES");
    order.setPartyId(requestDto.getCustomerId());
    order.setOrderDate(
        requestDto.getOrderDate() != null ? requestDto.getOrderDate() : LocalDateTime.now());
    order.setStatus(requestDto.getStatus() != null ? requestDto.getStatus() : "DRAFT");

    // beforeCreate hook sets orderNumber and totalAmount
    beforeCreate(order);
    Order savedOrder = orderRepository.save(order);

    // Create and save order lines
    if (requestDto.getLines() != null && !requestDto.getLines().isEmpty()) {
      double totalAmount = 0.0;
      for (SalesOrderCreateRequestDto.OrderLineCreateDto lineDto : requestDto.getLines()) {
        OrderLine line = new OrderLine();
        line.setOrderId(savedOrder.getId());
        line.setProductId(lineDto.getProductId());
        line.setQuantity(lineDto.getQuantity());
        line.setUnitPrice(lineDto.getUnitPrice());
        line.setLineTotal(lineDto.getQuantity() * lineDto.getUnitPrice());

        if (line.getQuantity() <= 0) {
          throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        totalAmount += line.getLineTotal();
        orderLineRepository.save(line);
      }

      // Update order total
      savedOrder.setTotalAmount(totalAmount);
      orderRepository.save(savedOrder);
    }

    return savedOrder.getId();
  }
}
