package com.erp.modules.hr.service;

import com.erp.common.base.BaseService;
import com.erp.modules.hr.entity.Department;
import com.erp.modules.hr.repository.DepartmentRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class DepartmentService extends BaseService<Department> {

  private final DepartmentRepository departmentRepository;

  public DepartmentService(DepartmentRepository departmentRepository) {
    this.departmentRepository = departmentRepository;
  }

  @Override
  protected JpaRepository<Department, UUID> getRepository() {
    return departmentRepository;
  }

  @Override
  protected void beforeCreate(Department entity) {
    if (entity.getDepartmentCode() == null || entity.getDepartmentCode().trim().isEmpty()) {
      throw new IllegalArgumentException("Department code is required");
    }
    if (departmentRepository.findByDepartmentCode(entity.getDepartmentCode()).isPresent()) {
      throw new IllegalArgumentException("Department code must be unique");
    }
  }

  @Override
  protected void beforeUpdate(Department newEntity, Department existingEntity) {
    if (!newEntity.getDepartmentCode().equals(existingEntity.getDepartmentCode())
        && departmentRepository.findByDepartmentCode(newEntity.getDepartmentCode()).isPresent()) {
      throw new IllegalArgumentException("Department code must be unique");
    }
  }

  public List<Department> getRootDepartments() {
    return departmentRepository.findByParentDepartmentIdIsNull();
  }

  public List<Department> getChildren(UUID parentId) {
    return departmentRepository.findByParentDepartmentId(parentId);
  }
}
