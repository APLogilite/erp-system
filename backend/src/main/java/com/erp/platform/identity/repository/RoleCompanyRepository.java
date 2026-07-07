package com.erp.platform.identity.repository;

import com.erp.platform.identity.entity.RoleCompany;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleCompanyRepository extends JpaRepository<RoleCompany, UUID> {
  List<RoleCompany> findByRoleId(UUID roleId);
  List<RoleCompany> findByRoleIdIn(List<UUID> roleIds);
}
