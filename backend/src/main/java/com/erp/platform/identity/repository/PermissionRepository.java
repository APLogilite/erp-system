package com.erp.platform.identity.repository;

import com.erp.platform.identity.entity.Permission;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, UUID> {
  Optional<Permission> findByCode(String code);
  List<Permission> findByModule(String module);
  List<Permission> findByResourceType(String resourceType);
}
