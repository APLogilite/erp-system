package com.erp.core.layout.repository;

import com.erp.core.layout.entity.SysWindowAccess;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SysWindowAccessRepository extends JpaRepository<SysWindowAccess, UUID> {
  List<SysWindowAccess> findByWindowId(UUID windowId);
  List<SysWindowAccess> findByRoleId(UUID roleId);
  List<SysWindowAccess> findByRoleIdIn(List<UUID> roleIds);
  Optional<SysWindowAccess> findByWindowIdAndRoleId(UUID windowId, UUID roleId);
}
