package com.erp.platform.identity.repository;

import com.erp.platform.identity.entity.Company;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<Company, UUID> {
  Optional<Company> findByCode(String code);
  List<Company> findByOrganizationId(UUID organizationId);
  List<Company> findByOrganizationIdIn(List<UUID> organizationIds);

  @Query("SELECT c FROM Company c JOIN FETCH c.organization o JOIN FETCH o.tenant")
  List<Company> findAllWithOrganization();

  @Query("SELECT c FROM Company c JOIN FETCH c.organization o JOIN FETCH o.tenant WHERE c.id IN :ids")
  List<Company> findByIdInWithOrganization(@Param("ids") List<UUID> ids);
}
