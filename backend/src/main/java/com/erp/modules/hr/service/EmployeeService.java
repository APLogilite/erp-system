package com.erp.modules.hr.service;

import com.erp.common.base.BaseService;
import com.erp.modules.hr.entity.Employee;
import com.erp.modules.hr.repository.EmployeeRepository;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService extends BaseService<Employee> {

  private final EmployeeRepository employeeRepository;

  public EmployeeService(EmployeeRepository employeeRepository) {
    this.employeeRepository = employeeRepository;
  }

  @Override
  protected JpaRepository<Employee, UUID> getRepository() {
    return employeeRepository;
  }

  @Override
  protected void beforeCreate(Employee entity) {
    if (entity.getEmployeeCode() == null || entity.getEmployeeCode().trim().isEmpty()) {
      throw new IllegalArgumentException("Employee code is required");
    }
    if (entity.getStatus() == null) {
      entity.setStatus("ACTIVE");
    }
    if (employeeRepository.findByEmployeeCode(entity.getEmployeeCode()).isPresent()) {
      throw new IllegalArgumentException("Employee code must be unique");
    }
  }

  @Override
  protected void beforeUpdate(Employee newEntity, Employee existingEntity) {
    if (!newEntity.getEmployeeCode().equals(existingEntity.getEmployeeCode())
        && employeeRepository.findByEmployeeCode(newEntity.getEmployeeCode()).isPresent()) {
      throw new IllegalArgumentException("Employee code must be unique");
    }
  }
}
