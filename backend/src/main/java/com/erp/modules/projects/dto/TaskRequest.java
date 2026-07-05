package com.erp.modules.projects.dto;

import java.util.UUID;

public class TaskRequest {
  private String taskNumber;
  private String title;
  private String description;
  private String priority;
  private UUID assignedTo;
  private UUID projectId;
  private Double plannedHours;

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
}
