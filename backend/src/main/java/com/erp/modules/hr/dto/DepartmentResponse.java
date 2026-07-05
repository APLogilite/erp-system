package com.erp.modules.hr.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class DepartmentResponse {
  private UUID id;
  private String departmentCode;
  private String name;
  private UUID parentDepartmentId;
  private UUID managerId;
  private Boolean isActive;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public UUID getId() { return id; }
  public void setId(UUID id) { this.id = id; }
  public String getDepartmentCode() { return departmentCode; }
  public void setDepartmentCode(String departmentCode) { this.departmentCode = departmentCode; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public UUID getParentDepartmentId() { return parentDepartmentId; }
  public void setParentDepartmentId(UUID parentDepartmentId) { this.parentDepartmentId = parentDepartmentId; }
  public UUID getManagerId() { return managerId; }
  public void setManagerId(UUID managerId) { this.managerId = managerId; }
  public Boolean getIsActive() { return isActive; }
  public void setIsActive(Boolean isActive) { this.isActive = isActive; }
  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
  public LocalDateTime getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
