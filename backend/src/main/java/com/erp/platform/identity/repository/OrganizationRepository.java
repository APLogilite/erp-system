package com.erp.platform.identity.repository;

import com.erp.platform.identity.entity.Organization;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, UUID> {
  Optional<Organization> findByCode(String code);
  List<Organization> findByTenantId(UUID tenantId);
  List<Organization> findByParentId(UUID parentId);
}
