package com.erp.modules.manufacturing.entity;

import com.erp.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "routing_operations")
public class RoutingOperation extends BaseEntity {

  @Column(name = "routing_id", nullable = false)
  private UUID routingId;

  @Column(name = "sequence")
  private Integer sequence;

  @Column(name = "work_center_id")
  private UUID workCenterId;

  @Column(name = "operation_name", nullable = false)
  private String operationName;

  @Column(name = "setup_time")
  private Double setupTime = 0.0;

  @Column(name = "run_time")
  private Double runTime = 0.0;

  @Column(name = "queue_time")
  private Double queueTime = 0.0;

  public UUID getRoutingId() { return routingId; }
  public void setRoutingId(UUID routingId) { this.routingId = routingId; }
  public Integer getSequence() { return sequence; }
  public void setSequence(Integer sequence) { this.sequence = sequence; }
  public UUID getWorkCenterId() { return workCenterId; }
  public void setWorkCenterId(UUID workCenterId) { this.workCenterId = workCenterId; }
  public String getOperationName() { return operationName; }
  public void setOperationName(String operationName) { this.operationName = operationName; }
  public Double getSetupTime() { return setupTime; }
  public void setSetupTime(Double setupTime) { this.setupTime = setupTime; }
  public Double getRunTime() { return runTime; }
  public void setRunTime(Double runTime) { this.runTime = runTime; }
  public Double getQueueTime() { return queueTime; }
  public void setQueueTime(Double queueTime) { this.queueTime = queueTime; }
}
