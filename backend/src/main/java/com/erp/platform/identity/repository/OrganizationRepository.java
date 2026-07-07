package com.erp.platform.identity.repository;

import com.erp.platform.identity.entity.Organization;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, UUID> {
  Optional<Organization> findByCode(String code);

  @Query("SELECT o FROM Organization o JOIN FETCH o.tenant")
  List<Organization> findAllWithTenant();

  @Query("SELECT o FROM Organization o JOIN FETCH o.tenant WHERE o.id IN :ids")
  List<Organization> findByIdInWithTenant(@Param("ids") List<UUID> ids);
  List<Organization> findByTenantId(UUID tenantId);
  List<Organization> findByParentId(UUID parentId);
}
