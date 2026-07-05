package com.erp.modules.hr.entity;

import com.erp.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "employees")
public class Employee extends BaseEntity {

  @Column(name = "employee_code", nullable = false, unique = true)
  private String employeeCode;

  @Column(name = "first_name", nullable = false)
  private String firstName;

  @Column(name = "last_name", nullable = false)
  private String lastName;

  @Column
  private String email;

  @Column
  private String phone;

  @Column(name = "department_id")
  private UUID departmentId;

  @Column
  private String designation;

  @Column(name = "manager_id")
  private UUID managerId;

  @Column(name = "joining_date")
  private LocalDate joiningDate;

  @Column(nullable = false)
  private String status = "ACTIVE";

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
}
