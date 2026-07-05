package com.erp.modules.hr.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class EmployeeResponse {
  private UUID id;
  private String employeeCode;
  private String firstName;
  private String lastName;
  private String email;
  private String phone;
  private UUID departmentId;
  private String designation;
  private UUID managerId;
  private LocalDate joiningDate;
  private String status;
  private Boolean isActive;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public UUID getId() { return id; }
  public void setId(UUID id) { this.id = id; }
  public String getEmployeeCode() { return employeeCode; }
  public void setEmployeeCode(String employeeCode) { this.employeeCode = employeeCode; }
  public String getFirstName() { return firstName; }
  public void setFirstName(String firstName) { this.firstName = firstName; }
  public String getLastName() { return lastName; }
  public void setLastName(String lastName) { this.lastName = lastName; }
  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }
  public String getPhone() { return phone; }
  public void setPhone(String phone) { this.phone = phone; }
  public UUID getDepartmentId() { return departmentId; }
  public void setDepartmentId(UUID departmentId) { this.departmentId = departmentId; }
  public String getDesignation() { return designation; }
  public void setDesignation(String designation) { this.designation = designation; }
  public UUID getManagerId() { return managerId; }
  public void setManagerId(UUID managerId) { this.managerId = managerId; }
  public LocalDate getJoiningDate() { return joiningDate; }
  public void setJoiningDate(LocalDate joiningDate) { this.joiningDate = joiningDate; }
  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }
  public Boolean getIsActive() { return isActive; }
  public void setIsActive(Boolean isActive) { this.isActive = isActive; }
  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
  public LocalDateTime getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
