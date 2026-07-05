package com.erp.platform.identity.repository;

import com.erp.platform.identity.entity.UserOrganization;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserOrganizationRepository extends JpaRepository<UserOrganization, UUID> {
  List<UserOrganization> findByUserId(UUID userId);
  List<UserOrganization> findByOrganizationId(UUID organizationId);
}
