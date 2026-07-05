package com.erp.modules.manufacturing.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class WorkCenterResponse {
  private UUID id;
  private String code;
  private String name;
  private Double capacity;
  private Double costPerHour;
  private Double efficiency;
  private String calendar;
  private Boolean isActive;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public UUID getId() { return id; }
  public void setId(UUID id) { this.id = id; }
  public String getCode() { return code; }
  public void setCode(String code) { this.code = code; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public Double getCapacity() { return capacity; }
  public void setCapacity(Double capacity) { this.capacity = capacity; }
  public Double getCostPerHour() { return costPerHour; }
  public void setCostPerHour(Double costPerHour) { this.costPerHour = costPerHour; }
  public Double getEfficiency() { return efficiency; }
  public void setEfficiency(Double efficiency) { this.efficiency = efficiency; }
  public String getCalendar() { return calendar; }
  public void setCalendar(String calendar) { this.calendar = calendar; }
  public Boolean getIsActive() { return isActive; }
  public void setIsActive(Boolean isActive) { this.isActive = isActive; }
  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
  public LocalDateTime getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
