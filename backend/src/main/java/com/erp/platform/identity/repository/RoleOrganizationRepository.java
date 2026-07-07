package com.erp.platform.identity.repository;

import com.erp.platform.identity.entity.RoleOrganization;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleOrganizationRepository extends JpaRepository<RoleOrganization, UUID> {
  List<RoleOrganization> findByRoleId(UUID roleId);
  List<RoleOrganization> findByRoleIdIn(List<UUID> roleIds);
}
