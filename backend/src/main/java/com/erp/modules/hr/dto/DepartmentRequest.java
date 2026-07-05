package com.erp.modules.hr.dto;

import java.util.UUID;

public class DepartmentRequest {
  private String departmentCode;
  private String name;
  private UUID parentDepartmentId;
  private UUID managerId;

  public String getDepartmentCode() { return departmentCode; }
  public void setDepartmentCode(String departmentCode) { this.departmentCode = departmentCode; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public UUID getParentDepartmentId() { return parentDepartmentId; }
  public void setParentDepartmentId(UUID parentDepartmentId) { this.parentDepartmentId = parentDepartmentId; }
  public UUID getManagerId() { return managerId; }
  public void setManagerId(UUID managerId) { this.managerId = managerId; }
}
