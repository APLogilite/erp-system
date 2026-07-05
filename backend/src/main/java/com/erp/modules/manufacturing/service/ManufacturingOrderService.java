package com.erp.modules.manufacturing.service;

import com.erp.common.base.BaseService;
import com.erp.modules.manufacturing.entity.ManufacturingOrder;
import com.erp.modules.manufacturing.entity.WorkOrder;
import com.erp.modules.manufacturing.repository.ManufacturingOrderRepository;
import com.erp.modules.manufacturing.repository.WorkOrderRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ManufacturingOrderService extends BaseService<ManufacturingOrder> {

  private final ManufacturingOrderRepository moRepository;
  private final WorkOrderRepository workOrderRepository;

  public ManufacturingOrderService(ManufacturingOrderRepository moRepository,
                                   WorkOrderRepository workOrderRepository) {
    this.moRepository = moRepository;
    this.workOrderRepository = workOrderRepository;
  }

  @Override
  protected JpaRepository<ManufacturingOrder, UUID> getRepository() {
    return moRepository;
  }

  @Override
  protected void beforeCreate(ManufacturingOrder entity) {
    if (entity.getDocumentNo() == null) {
      entity.setDocumentNo("MO-" + System.currentTimeMillis());
    }
    if (entity.getStatus() == null) {
      entity.setStatus("DRAFT");
    }
    if (entity.getPriority() == null) {
      entity.setPriority("MEDIUM");
    }
    if (entity.getCompletedQuantity() == null) {
      entity.setCompletedQuantity(0.0);
    }
    if (entity.getPlannedQuantity() == null || entity.getPlannedQuantity() <= 0) {
      throw new IllegalArgumentException("Planned quantity must be positive");
    }
  }

  @Override
  protected void beforeUpdate(ManufacturingOrder newEntity, ManufacturingOrder existingEntity) {
    String status = existingEntity.getStatus();
    if ("COMPLETED".equals(status) || "CLOSED".equals(status)) {
      throw new IllegalArgumentException("Cannot modify a " + status + " manufacturing order");
    }
    newEntity.setDocumentNo(existingEntity.getDocumentNo());
  }

  @Transactional
  public UUID plan(UUID moId) {
    ManufacturingOrder mo = findByIdOrThrow(moId);
    if (!"DRAFT".equals(mo.getStatus())) {
      throw new IllegalArgumentException("Only DRAFT orders can be planned");
    }
    mo.setStatus("PLANNED");
    return moRepository.save(mo).getId();
  }

  @Transactional
  public UUID release(UUID moId) {
    ManufacturingOrder mo = findByIdOrThrow(moId);
    if (!"PLANNED".equals(mo.getStatus())) {
      throw new IllegalArgumentException("Only PLANNED orders can be released");
    }
    mo.setStatus("RELEASED");
    generateWorkOrders(mo);
    return moRepository.save(mo).getId();
  }

  @Transactional
  public UUID startProduction(UUID moId) {
    ManufacturingOrder mo = findByIdOrThrow(moId);
    if (!"RELEASED".equals(mo.getStatus())) {
      throw new IllegalArgumentException("Only RELEASED orders can start production");
    }
    mo.setStatus("IN_PRODUCTION");

    List<WorkOrder> workOrders = workOrderRepository.findByManufacturingOrderIdOrderBySequenceAsc(moId);
    for (WorkOrder wo : workOrders) {
      if ("PLANNED".equals(wo.getStatus()) || "READY".equals(wo.getStatus())) {
        wo.setStatus("IN_PROGRESS");
        wo.setActualStart(LocalDateTime.now());
        workOrderRepository.save(wo);
        break;
      }
    }

    return moRepository.save(mo).getId();
  }

  @Transactional
  public UUID complete(UUID moId) {
    ManufacturingOrder mo = findByIdOrThrow(moId);
    if (!"IN_PRODUCTION".equals(mo.getStatus())) {
      throw new IllegalArgumentException("Only IN_PRODUCTION orders can be completed");
    }
    mo.setStatus("COMPLETED");
    mo.setCompletedQuantity(mo.getPlannedQuantity());

    List<WorkOrder> workOrders = workOrderRepository.findByManufacturingOrderIdOrderBySequenceAsc(moId);
    for (WorkOrder wo : workOrders) {
      if (!"COMPLETED".equals(wo.getStatus())) {
        wo.setStatus("COMPLETED");
        wo.setActualEnd(LocalDateTime.now());
        workOrderRepository.save(wo);
      }
    }

    return moRepository.save(mo).getId();
  }

  @Transactional
  public UUID close(UUID moId) {
    ManufacturingOrder mo = findByIdOrThrow(moId);
    if (!"COMPLETED".equals(mo.getStatus())) {
      throw new IllegalArgumentException("Only COMPLETED orders can be closed");
    }
    mo.setStatus("CLOSED");

    List<WorkOrder> workOrders = workOrderRepository.findByManufacturingOrderIdOrderBySequenceAsc(moId);
    for (WorkOrder wo : workOrders) {
      if (!"CLOSED".equals(wo.getStatus())) {
        wo.setStatus("CLOSED");
        workOrderRepository.save(wo);
      }
    }

    return moRepository.save(mo).getId();
  }

  @Transactional
  public UUID voidOrder(UUID moId) {
    ManufacturingOrder mo = findByIdOrThrow(moId);
    if ("COMPLETED".equals(mo.getStatus()) || "CLOSED".equals(mo.getStatus())) {
      throw new IllegalArgumentException("Cannot void a " + mo.getStatus() + " order");
    }
    mo.setStatus("VOID");

    List<WorkOrder> workOrders = workOrderRepository.findByManufacturingOrderIdOrderBySequenceAsc(moId);
    for (WorkOrder wo : workOrders) {
      wo.setStatus("CLOSED");
      workOrderRepository.save(wo);
    }

    return moRepository.save(mo).getId();
  }

  private void generateWorkOrders(ManufacturingOrder mo) {
    List<WorkOrder> existing = workOrderRepository.findByManufacturingOrderIdOrderBySequenceAsc(mo.getId());
    if (!existing.isEmpty()) {
      return;
    }
    int sequence = 1;
    WorkOrder wo = new WorkOrder();
    wo.setManufacturingOrderId(mo.getId());
    wo.setSequence(sequence);
    wo.setStatus("PLANNED");
    wo.setPlannedStart(mo.getPlannedStart() != null ? mo.getPlannedStart().atStartOfDay() : LocalDateTime.now());
    wo.setPlannedEnd(mo.getPlannedEnd() != null ? mo.getPlannedEnd().atStartOfDay() : LocalDateTime.now().plusDays(1));
    workOrderRepository.save(wo);
  }
}
