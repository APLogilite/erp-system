package com.erp.modules.service.entity;

import com.erp.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "service_requests")
public class ServiceRequest extends BaseEntity {

  @Column(name = "ticket_number", nullable = false, unique = true)
  private String ticketNumber;

  @Column(name = "customer_id", nullable = false)
  private UUID customerId;

  @Column(name = "asset_id")
  private UUID assetId;

  @Column
  private String priority = "MEDIUM";

  @Column
  private String category;

  @Column(name = "assigned_engineer_id")
  private UUID assignedEngineerId;

  @Column(nullable = false)
  private String status = "NEW";

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(columnDefinition = "TEXT")
  private String resolution;

  public String getTicketNumber() { return ticketNumber; }
  public void setTicketNumber(String ticketNumber) { this.ticketNumber = ticketNumber; }
  public UUID getCustomerId() { return customerId; }
  public void setCustomerId(UUID customerId) { this.customerId = customerId; }
  public UUID getAssetId() { return assetId; }
  public void setAssetId(UUID assetId) { this.assetId = assetId; }
  public String getPriority() { return priority; }
  public void setPriority(String priority) { this.priority = priority; }
  public String getCategory() { return category; }
  public void setCategory(String category) { this.category = category; }
  public UUID getAssignedEngineerId() { return assignedEngineerId; }
  public void setAssignedEngineerId(UUID assignedEngineerId) { this.assignedEngineerId = assignedEngineerId; }
  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }
  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }
  public String getResolution() { return resolution; }
  public void setResolution(String resolution) { this.resolution = resolution; }
}
