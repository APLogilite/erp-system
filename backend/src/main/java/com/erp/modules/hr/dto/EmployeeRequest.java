package com.erp.modules.hr.dto;

import java.time.LocalDate;
import java.util.UUID;

public class EmployeeRequest {
  private String employeeCode;
  private String firstName;
  private String lastName;
  private String email;
  private String phone;
  private UUID departmentId;
  private String designation;
  private UUID managerId;
  private LocalDate joiningDate;

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
}
