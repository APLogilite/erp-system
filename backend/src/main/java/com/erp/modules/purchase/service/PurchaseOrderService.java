package com.erp.modules.purchase.service;

import com.erp.common.base.BaseService;
import com.erp.modules.inventory.service.StockMovementService;
import com.erp.modules.purchase.dto.PurchaseOrderLineRequest;
import com.erp.modules.purchase.dto.PurchaseOrderRequest;
import com.erp.modules.purchase.entity.PurchaseOrder;
import com.erp.modules.purchase.entity.PurchaseOrderLine;
import com.erp.modules.purchase.repository.PurchaseOrderLineRepository;
import com.erp.modules.purchase.repository.PurchaseOrderRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PurchaseOrderService extends BaseService<PurchaseOrder> {

  private final PurchaseOrderRepository purchaseOrderRepository;
  private final PurchaseOrderLineRepository lineRepository;
  private final StockMovementService stockMovementService;

  public PurchaseOrderService(
      PurchaseOrderRepository purchaseOrderRepository,
      PurchaseOrderLineRepository lineRepository,
      StockMovementService stockMovementService) {
    this.purchaseOrderRepository = purchaseOrderRepository;
    this.lineRepository = lineRepository;
    this.stockMovementService = stockMovementService;
  }

  @Override
  protected JpaRepository<PurchaseOrder, UUID> getRepository() {
    return purchaseOrderRepository;
  }

  @Override
  protected void beforeCreate(PurchaseOrder entity) {
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
  protected void beforeUpdate(PurchaseOrder newEntity, PurchaseOrder existingEntity) {
    String status = existingEntity.getStatus();
    if ("COMPLETED".equals(status) || "APPROVED".equals(status) || "RECEIVED".equals(status) || "CLOSED".equals(status)) {
      throw new IllegalArgumentException("Cannot modify a " + status + " purchase order");
    }
    newEntity.setDocumentNo(existingEntity.getDocumentNo());
  }

  @Transactional
  public UUID createWithLines(PurchaseOrderRequest request) {
    PurchaseOrder order = new PurchaseOrder();
    order.setDocumentDate(request.getDocumentDate() != null ? request.getDocumentDate() : LocalDate.now());
    order.setVendorId(request.getVendorId());
    order.setWarehouseId(request.getWarehouseId());
    order.setDescription(request.getDescription());
    order.setCurrency(request.getCurrency() != null ? request.getCurrency() : "USD");
    order.setExpectedDate(request.getExpectedDate());
    order.setStatus("DRAFT");

    beforeCreate(order);
    PurchaseOrder saved = purchaseOrderRepository.save(order);

    if (request.getLines() != null && !request.getLines().isEmpty()) {
      processLines(saved.getId(), request.getLines());
      saved = findByIdOrThrow(saved.getId());
    }

    return saved.getId();
  }

  @Transactional
  public void completeOrder(UUID orderId) {
    PurchaseOrder order = findByIdOrThrow(orderId);
    if (!"DRAFT".equals(order.getStatus())) {
      throw new IllegalArgumentException("Only DRAFT purchase orders can be completed");
    }
    List<PurchaseOrderLine> lines = lineRepository.findByOrderId(orderId);
    if (lines.isEmpty()) {
      throw new IllegalArgumentException("Cannot complete a purchase order with no lines");
    }
    if (order.getVendorId() == null) {
      throw new IllegalArgumentException("Vendor is required");
    }
    if (order.getTotalAmount() == null || order.getTotalAmount() <= 0) {
      throw new IllegalArgumentException("Total amount must be greater than zero");
    }
    order.setStatus("COMPLETED");
    purchaseOrderRepository.save(order);
  }

  @Transactional
  public void approveOrder(UUID orderId) {
    PurchaseOrder order = findByIdOrThrow(orderId);
    if (!"COMPLETED".equals(order.getStatus())) {
      throw new IllegalArgumentException("Only COMPLETED purchase orders can be approved");
    }
    order.setStatus("APPROVED");
    purchaseOrderRepository.save(order);
  }

  @Transactional
  public void receiveOrder(UUID orderId) {
    PurchaseOrder order = findByIdOrThrow(orderId);
    if (!"APPROVED".equals(order.getStatus())) {
      throw new IllegalArgumentException("Only APPROVED purchase orders can be received");
    }
    List<PurchaseOrderLine> lines = lineRepository.findByOrderId(orderId);
    for (PurchaseOrderLine line : lines) {
      double toReceive = line.getQuantity() - line.getReceivedQuantity();
      if (toReceive > 0) {
        line.setReceivedQuantity(line.getReceivedQuantity() + toReceive);
        lineRepository.save(line);
        stockMovementService.increaseStock(line.getProductId(), order.getWarehouseId(), toReceive);
      }
    }
    order.setStatus("RECEIVED");
    purchaseOrderRepository.save(order);
  }

  @Transactional
  public void closeOrder(UUID orderId) {
    PurchaseOrder order = findByIdOrThrow(orderId);
    if (!"RECEIVED".equals(order.getStatus())) {
      throw new IllegalArgumentException("Only RECEIVED purchase orders can be closed");
    }
    order.setStatus("CLOSED");
    purchaseOrderRepository.save(order);
  }

  @Transactional
  public void voidOrder(UUID orderId) {
    PurchaseOrder order = findByIdOrThrow(orderId);
    if (!"DRAFT".equals(order.getStatus())) {
      throw new IllegalArgumentException("Only DRAFT purchase orders can be voided");
    }
    order.setStatus("VOID");
    purchaseOrderRepository.save(order);
  }

  @Transactional
  public void reopenOrder(UUID orderId) {
    PurchaseOrder order = findByIdOrThrow(orderId);
    if (!"CLOSED".equals(order.getStatus())) {
      throw new IllegalArgumentException("Only CLOSED purchase orders can be reopened");
    }
    order.setStatus("DRAFT");
    purchaseOrderRepository.save(order);
  }

  public List<PurchaseOrderLine> getLines(UUID orderId) {
    return lineRepository.findByOrderId(orderId);
  }

  private void processLines(UUID orderId, List<PurchaseOrderLineRequest> lineRequests) {
    int lineNo = lineRepository.findByOrderId(orderId).size() + 1;
    double totalAmount = 0.0;

    for (PurchaseOrderLineRequest req : lineRequests) {
      if (req.getQuantity() == null || req.getQuantity() <= 0) {
        throw new IllegalArgumentException("Quantity must be greater than 0");
      }
      PurchaseOrderLine line = new PurchaseOrderLine();
      line.setOrderId(orderId);
      line.setLineNo(lineNo++);
      line.setProductId(req.getProductId());
      line.setDescription(req.getDescription());
      line.setQuantity(req.getQuantity());
      line.setReceivedQuantity(0.0);
      line.setUnitPrice(req.getUnitPrice() != null ? req.getUnitPrice() : 0.0);
      line.setDiscount(req.getDiscount() != null ? req.getDiscount() : 0.0);
      line.setLineAmount((line.getUnitPrice() * line.getQuantity()) - line.getDiscount());
      line.setExpectedDate(req.getExpectedDate());
      totalAmount += line.getLineAmount();
      lineRepository.save(line);
    }

    PurchaseOrder order = findByIdOrThrow(orderId);
    order.setTotalAmount(totalAmount);
    purchaseOrderRepository.save(order);
  }

  public void addLines(UUID orderId, List<PurchaseOrderLineRequest> lineRequests) {
    PurchaseOrder order = findByIdOrThrow(orderId);
    if (!"DRAFT".equals(order.getStatus())) {
      throw new IllegalArgumentException("Can only add lines to DRAFT purchase orders");
    }
    processLines(orderId, lineRequests);
  }

  public void recalculate(UUID orderId) {
    PurchaseOrder order = findByIdOrThrow(orderId);
    List<PurchaseOrderLine> lines = lineRepository.findByOrderId(orderId);
    double total = lines.stream().mapToDouble(PurchaseOrderLine::getLineAmount).sum();
    order.setTotalAmount(total);
    purchaseOrderRepository.save(order);
  }

  private String generateDocumentNo() {
    return "PO-" + System.currentTimeMillis();
  }
}
