package com.erp.modules.service.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class ServiceRequestResponse {
  private UUID id;
  private String ticketNumber;
  private UUID customerId;
  private UUID assetId;
  private String priority;
  private String category;
  private UUID assignedEngineerId;
  private String status;
  private String description;
  private String resolution;
  private Boolean isActive;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public UUID getId() { return id; }
  public void setId(UUID id) { this.id = id; }
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
  public Boolean getIsActive() { return isActive; }
  public void setIsActive(Boolean isActive) { this.isActive = isActive; }
  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
  public LocalDateTime getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
