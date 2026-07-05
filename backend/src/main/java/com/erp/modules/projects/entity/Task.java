package com.erp.modules.projects.entity;

import com.erp.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "project_tasks")
public class Task extends BaseEntity {

  @Column(name = "task_number", nullable = false, unique = true)
  private String taskNumber;

  @Column(nullable = false)
  private String title;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column
  private String priority = "MEDIUM";

  @Column(name = "assigned_to")
  private UUID assignedTo;

  @Column(name = "project_id", nullable = false)
  private UUID projectId;

  @Column(name = "planned_hours")
  private Double plannedHours = 0.0;

  @Column(name = "actual_hours")
  private Double actualHours = 0.0;

  @Column(nullable = false)
  private String status = "OPEN";

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
}
