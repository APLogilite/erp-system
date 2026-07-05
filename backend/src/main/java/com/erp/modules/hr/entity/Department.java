package com.erp.modules.hr.entity;

import com.erp.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "departments")
public class Department extends BaseEntity {

  @Column(name = "department_code", nullable = false, unique = true)
  private String departmentCode;

  @Column(nullable = false)
  private String name;

  @Column(name = "parent_department_id")
  private UUID parentDepartmentId;

  @Column(name = "manager_id")
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
