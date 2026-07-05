package com.erp.modules.projects.entity;

import com.erp.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "projects")
public class Project extends BaseEntity {

  @Column(name = "project_code", nullable = false, unique = true)
  private String projectCode;

  @Column(nullable = false)
  private String name;

  @Column(name = "customer_id")
  private UUID customerId;

  @Column(name = "manager_id")
  private UUID managerId;

  @Column(name = "start_date")
  private LocalDate startDate;

  @Column(name = "end_date")
  private LocalDate endDate;

  @Column(nullable = false)
  private String status = "OPEN";

  @Column
  private Double budget = 0.0;

  public String getProjectCode() { return projectCode; }
  public void setProjectCode(String projectCode) { this.projectCode = projectCode; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public UUID getCustomerId() { return customerId; }
  public void setCustomerId(UUID customerId) { this.customerId = customerId; }
  public UUID getManagerId() { return managerId; }
  public void setManagerId(UUID managerId) { this.managerId = managerId; }
  public LocalDate getStartDate() { return startDate; }
  public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
  public LocalDate getEndDate() { return endDate; }
  public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }
  public Double getBudget() { return budget; }
  public void setBudget(Double budget) { this.budget = budget; }
}
