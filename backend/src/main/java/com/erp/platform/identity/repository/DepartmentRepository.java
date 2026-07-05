package com.erp.platform.identity.repository;

import com.erp.platform.identity.entity.Department;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("identityDepartmentRepository")
public interface DepartmentRepository extends JpaRepository<Department, UUID> {
  Optional<Department> findByCode(String code);
  List<Department> findByBranchId(UUID branchId);
  List<Department> findByParentId(UUID parentId);
}
