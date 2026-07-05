package com.erp.platform.identity.repository;

import com.erp.platform.identity.entity.Tenant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, UUID> {
  Optional<Tenant> findByCode(String code);
  Optional<Tenant> findByDomain(String domain);
}
