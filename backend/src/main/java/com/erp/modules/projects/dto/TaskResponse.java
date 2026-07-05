package com.erp.modules.projects.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class TaskResponse {
  private UUID id;
  private String taskNumber;
  private String title;
  private String description;
  private String priority;
  private UUID assignedTo;
  private UUID projectId;
  private Double plannedHours;
  private Double actualHours;
  private String status;
  private Boolean isActive;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public UUID getId() { return id; }
  public void setId(UUID id) { this.id = id; }
  public String getTaskNumber() { return taskNumber; }
  public void setTaskNumber(String taskNumber) { this.taskNumber = taskNumber; }
  public String getTitle() { return title; }
  public void setTitle(String title) { this.title = title; }
  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }
  public String getPriority() { return priority; }
  public void setPriority(String priority) { this.priority = priority; }
  public UUID getAssignedTo() { return assignedTo; }
  public void setAssignedTo(UUID assignedTo) { this.assignedTo = assignedTo; }
  public UUID getProjectId() { return projectId; }
  public void setProjectId(UUID projectId) { this.projectId = projectId; }
  public Double getPlannedHours() { return plannedHours; }
  public void setPlannedHours(Double plannedHours) { this.plannedHours = plannedHours; }
  public Double getActualHours() { return actualHours; }
  public void setActualHours(Double actualHours) { this.actualHours = actualHours; }
  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }
  public Boolean getIsActive() { return isActive; }
  public void setIsActive(Boolean isActive) { this.isActive = isActive; }
  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
  public LocalDateTime getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
