package com.erp.modules.sales.service;

import com.erp.common.base.BaseService;
import com.erp.modules.inventory.service.StockMovementService;
import com.erp.modules.sales.dto.SalesOrderLineRequest;
import com.erp.modules.sales.dto.SalesOrderRequest;
import com.erp.modules.sales.entity.SalesOrder;
import com.erp.modules.sales.entity.SalesOrderLine;
import com.erp.modules.sales.repository.SalesOrderLineRepository;
import com.erp.modules.sales.repository.SalesOrderRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SalesOrderService extends BaseService<SalesOrder> {

  private final SalesOrderRepository salesOrderRepository;
  private final SalesOrderLineRepository lineRepository;
  private final StockMovementService stockMovementService;

  public SalesOrderService(
      SalesOrderRepository salesOrderRepository,
      SalesOrderLineRepository lineRepository,
      StockMovementService stockMovementService) {
    this.salesOrderRepository = salesOrderRepository;
    this.lineRepository = lineRepository;
    this.stockMovementService = stockMovementService;
  }

  @Override
  protected JpaRepository<SalesOrder, UUID> getRepository() {
    return salesOrderRepository;
  }

  @Override
  protected void beforeCreate(SalesOrder entity) {
    if (entity.getDocumentNo() == null) {
      entity.setDocumentNo(generateDocumentNo());
    }
    if (entity.getDocumentDate() == null) {
      entity.setDocumentDate(LocalDate.now());
    }
    if (entity.getStatus() == null) {
      entity.setStatus("DRAFT");
    }
    if (entity.getCurrency() == null) {
      entity.setCurrency("USD");
    }
    if (entity.getTotalAmount() == null) {
      entity.setTotalAmount(0.0);
    }
  }

  @Override
  protected void beforeUpdate(SalesOrder newEntity, SalesOrder existingEntity) {
    String status = existingEntity.getStatus();
    if ("COMPLETED".equals(status) || "APPROVED".equals(status) || "CLOSED".equals(status)) {
      throw new IllegalArgumentException("Cannot modify a " + status + " order");
    }
    newEntity.setDocumentNo(existingEntity.getDocumentNo());
  }

  @Transactional
  public UUID createWithLines(SalesOrderRequest request) {
    SalesOrder order = new SalesOrder();
    order.setDocumentDate(request.getDocumentDate() != null ? request.getDocumentDate() : LocalDate.now());
    order.setCustomerId(request.getCustomerId());
    order.setWarehouseId(request.getWarehouseId());
    order.setDescription(request.getDescription());
    order.setCurrency(request.getCurrency() != null ? request.getCurrency() : "USD");
    order.setStatus("DRAFT");

    beforeCreate(order);
    SalesOrder saved = salesOrderRepository.save(order);

    if (request.getLines() != null && !request.getLines().isEmpty()) {
      processLines(saved.getId(), request.getLines());
      saved = findByIdOrThrow(saved.getId());
    }

    return saved.getId();
  }

  @Transactional
  public void addLines(UUID orderId, List<SalesOrderLineRequest> lineRequests) {
    SalesOrder order = findByIdOrThrow(orderId);
    if (!"DRAFT".equals(order.getStatus())) {
      throw new IllegalArgumentException("Can only add lines to DRAFT orders");
    }
    processLines(orderId, lineRequests);
  }

  @Transactional
  public void recalculate(UUID orderId) {
    SalesOrder order = findByIdOrThrow(orderId);
    List<SalesOrderLine> lines = lineRepository.findByOrderId(orderId);
    double total = lines.stream().mapToDouble(SalesOrderLine::getLineAmount).sum();
    order.setTotalAmount(total);
    salesOrderRepository.save(order);
  }

  @Transactional
  public void completeOrder(UUID orderId) {
    SalesOrder order = findByIdOrThrow(orderId);
    if (!"DRAFT".equals(order.getStatus())) {
      throw new IllegalArgumentException("Only DRAFT orders can be completed");
    }
    List<SalesOrderLine> lines = lineRepository.findByOrderId(orderId);
    if (lines.isEmpty()) {
      throw new IllegalArgumentException("Cannot complete an order with no lines");
    }
    if (order.getCustomerId() == null) {
      throw new IllegalArgumentException("Customer is required");
    }
    if (order.getTotalAmount() == null || order.getTotalAmount() <= 0) {
      throw new IllegalArgumentException("Total amount must be greater than zero");
    }
    order.setStatus("COMPLETED");
    salesOrderRepository.save(order);
  }

  @Transactional
  public void approveOrder(UUID orderId) {
    SalesOrder order = findByIdOrThrow(orderId);
    if (!"COMPLETED".equals(order.getStatus())) {
      throw new IllegalArgumentException("Only COMPLETED orders can be approved");
    }
    order.setStatus("APPROVED");
    salesOrderRepository.save(order);
  }

  @Transactional
  public void closeOrder(UUID orderId) {
    SalesOrder order = findByIdOrThrow(orderId);
    if (!"APPROVED".equals(order.getStatus())) {
      throw new IllegalArgumentException("Only APPROVED orders can be closed");
    }
    order.setStatus("CLOSED");
    salesOrderRepository.save(order);
  }

  @Transactional
  public void reopenOrder(UUID orderId) {
    SalesOrder order = findByIdOrThrow(orderId);
    if (!"CLOSED".equals(order.getStatus())) {
      throw new IllegalArgumentException("Only CLOSED orders can be reopened");
    }
    order.setStatus("DRAFT");
    salesOrderRepository.save(order);
  }

  @Transactional
  public void voidOrder(UUID orderId) {
    SalesOrder order = findByIdOrThrow(orderId);
    if (!"DRAFT".equals(order.getStatus())) {
      throw new IllegalArgumentException("Only DRAFT orders can be voided");
    }
    order.setStatus("VOID");
    salesOrderRepository.save(order);
  }

  public List<SalesOrderLine> getLines(UUID orderId) {
    return lineRepository.findByOrderId(orderId);
  }

  private void processLines(UUID orderId, List<SalesOrderLineRequest> lineRequests) {
    int lineNo = lineRepository.findByOrderId(orderId).size() + 1;
    double totalAmount = 0.0;

    for (SalesOrderLineRequest req : lineRequests) {
      if (req.getQuantity() == null || req.getQuantity() <= 0) {
        throw new IllegalArgumentException("Quantity must be greater than 0");
      }
      SalesOrderLine line = new SalesOrderLine();
      line.setOrderId(orderId);
      line.setLineNo(lineNo++);
      line.setProductId(req.getProductId());
      line.setDescription(req.getDescription());
      line.setQuantity(req.getQuantity());
      line.setUom(req.getUom());
      line.setUnitPrice(req.getUnitPrice() != null ? req.getUnitPrice() : 0.0);
      line.setDiscount(req.getDiscount() != null ? req.getDiscount() : 0.0);
      line.setLineAmount((line.getUnitPrice() * line.getQuantity()) - line.getDiscount());
      totalAmount += line.getLineAmount();
      lineRepository.save(line);
    }

    SalesOrder order = findByIdOrThrow(orderId);
    order.setTotalAmount(totalAmount);
    salesOrderRepository.save(order);
  }

  private String generateDocumentNo() {
    return "SO-" + System.currentTimeMillis();
  }
}
