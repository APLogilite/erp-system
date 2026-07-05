package com.erp.modules.projects.dto;

import java.time.LocalDate;
import java.util.UUID;

public class ProjectRequest {
  private String projectCode;
  private String name;
  private UUID customerId;
  private UUID managerId;
  private LocalDate startDate;
  private LocalDate endDate;
  private Double budget;

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
  public Double getBudget() { return budget; }
  public void setBudget(Double budget) { this.budget = budget; }
}
