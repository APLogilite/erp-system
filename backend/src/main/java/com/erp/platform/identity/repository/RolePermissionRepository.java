package com.erp.platform.identity.repository;

import com.erp.platform.identity.entity.RolePermission;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, UUID> {
  List<RolePermission> findByRoleId(UUID roleId);
  List<RolePermission> findByRoleIdIn(List<UUID> roleIds);
  List<RolePermission> findByPermissionId(UUID permissionId);
}
