package com.erp.modules.manufacturing.service;

import com.erp.common.base.BaseService;
import com.erp.modules.manufacturing.entity.WorkOrder;
import com.erp.modules.manufacturing.repository.WorkOrderRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WorkOrderService extends BaseService<WorkOrder> {

  private final WorkOrderRepository workOrderRepository;

  public WorkOrderService(WorkOrderRepository workOrderRepository) {
    this.workOrderRepository = workOrderRepository;
  }

  @Override
  protected JpaRepository<WorkOrder, UUID> getRepository() {
    return workOrderRepository;
  }

  @Override
  protected void beforeCreate(WorkOrder entity) {
    if (entity.getStatus() == null) {
      entity.setStatus("PLANNED");
    }
  }

  @Override
  protected void beforeUpdate(WorkOrder newEntity, WorkOrder existingEntity) {
    String status = existingEntity.getStatus();
    if ("CLOSED".equals(status)) {
      throw new IllegalArgumentException("Cannot modify a CLOSED work order");
    }
  }

  public List<WorkOrder> getByManufacturingOrder(UUID moId) {
    return workOrderRepository.findByManufacturingOrderIdOrderBySequenceAsc(moId);
  }

  public List<WorkOrder> getByWorkCenter(UUID workCenterId) {
    return workOrderRepository.findByWorkCenterId(workCenterId);
  }

  @Transactional
  public void start(UUID woId) {
    WorkOrder wo = findByIdOrThrow(woId);
    if (!"PLANNED".equals(wo.getStatus()) && !"READY".equals(wo.getStatus())) {
      throw new IllegalArgumentException("Work order must be PLANNED or READY to start");
    }
    wo.setStatus("IN_PROGRESS");
    wo.setActualStart(LocalDateTime.now());
    workOrderRepository.save(wo);
  }

  @Transactional
  public void complete(UUID woId) {
    WorkOrder wo = findByIdOrThrow(woId);
    if (!"IN_PROGRESS".equals(wo.getStatus())) {
      throw new IllegalArgumentException("Only IN_PROGRESS work orders can be completed");
    }
    wo.setStatus("COMPLETED");
    wo.setActualEnd(LocalDateTime.now());
    workOrderRepository.save(wo);
  }

  @Transactional
  public void close(UUID woId) {
    WorkOrder wo = findByIdOrThrow(woId);
    if (!"COMPLETED".equals(wo.getStatus())) {
      throw new IllegalArgumentException("Only COMPLETED work orders can be closed");
    }
    wo.setStatus("CLOSED");
    workOrderRepository.save(wo);
  }
}
