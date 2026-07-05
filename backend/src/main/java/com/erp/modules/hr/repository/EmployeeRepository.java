package com.erp.modules.hr.repository;

import com.erp.modules.hr.entity.Employee;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {
  Optional<Employee> findByEmployeeCode(String employeeCode);
  List<Employee> findByDepartmentId(UUID departmentId);
  List<Employee> findByManagerId(UUID managerId);
  List<Employee> findByStatus(String status);
}
