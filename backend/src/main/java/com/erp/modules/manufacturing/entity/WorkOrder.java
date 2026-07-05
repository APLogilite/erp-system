package com.erp.modules.manufacturing.entity;

import com.erp.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "work_orders")
public class WorkOrder extends BaseEntity {

  @Column(name = "manufacturing_order_id", nullable = false)
  private UUID manufacturingOrderId;

  @Column(name = "sequence")
  private Integer sequence;

  @Column(name = "operation_id")
  private UUID operationId;

  @Column(name = "work_center_id")
  private UUID workCenterId;

  @Column(name = "planned_start")
  private LocalDateTime plannedStart;

  @Column(name = "planned_end")
  private LocalDateTime plannedEnd;

  @Column(name = "actual_start")
  private LocalDateTime actualStart;

  @Column(name = "actual_end")
  private LocalDateTime actualEnd;

  @Column(nullable = false)
  private String status = "PLANNED";

  public UUID getManufacturingOrderId() { return manufacturingOrderId; }
  public void setManufacturingOrderId(UUID manufacturingOrderId) { this.manufacturingOrderId = manufacturingOrderId; }
  public Integer getSequence() { return sequence; }
  public void setSequence(Integer sequence) { this.sequence = sequence; }
  public UUID getOperationId() { return operationId; }
  public void setOperationId(UUID operationId) { this.operationId = operationId; }
  public UUID getWorkCenterId() { return workCenterId; }
  public void setWorkCenterId(UUID workCenterId) { this.workCenterId = workCenterId; }
  public LocalDateTime getPlannedStart() { return plannedStart; }
  public void setPlannedStart(LocalDateTime plannedStart) { this.plannedStart = plannedStart; }
  public LocalDateTime getPlannedEnd() { return plannedEnd; }
  public void setPlannedEnd(LocalDateTime plannedEnd) { this.plannedEnd = plannedEnd; }
  public LocalDateTime getActualStart() { return actualStart; }
  public void setActualStart(LocalDateTime actualStart) { this.actualStart = actualStart; }
  public LocalDateTime getActualEnd() { return actualEnd; }
  public void setActualEnd(LocalDateTime actualEnd) { this.actualEnd = actualEnd; }
  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }
}
