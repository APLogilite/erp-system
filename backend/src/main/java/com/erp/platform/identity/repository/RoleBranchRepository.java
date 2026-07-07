package com.erp.platform.identity.repository;

import com.erp.platform.identity.entity.RoleBranch;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleBranchRepository extends JpaRepository<RoleBranch, UUID> {
  List<RoleBranch> findByRoleId(UUID roleId);
  List<RoleBranch> findByRoleIdIn(List<UUID> roleIds);
}
