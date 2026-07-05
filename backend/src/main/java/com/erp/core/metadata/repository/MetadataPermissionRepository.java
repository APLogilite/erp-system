package com.erp.core.metadata.repository;

import com.erp.core.metadata.entity.MetadataPermission;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MetadataPermissionRepository extends JpaRepository<MetadataPermission, UUID> {
  Optional<MetadataPermission> findByName(String name);
  List<MetadataPermission> findByRole(String role);
}
