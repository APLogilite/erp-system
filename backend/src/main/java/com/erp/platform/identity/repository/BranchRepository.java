package com.erp.platform.identity.repository;

import com.erp.platform.identity.entity.Branch;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BranchRepository extends JpaRepository<Branch, UUID> {
  Optional<Branch> findByCode(String code);
  List<Branch> findByCompanyId(UUID companyId);
  List<Branch> findByCompanyIdIn(List<UUID> companyIds);

  @Query("SELECT b FROM Branch b JOIN FETCH b.company c JOIN FETCH c.organization o JOIN FETCH o.tenant")
  List<Branch> findAllWithCompany();

  @Query("SELECT b FROM Branch b JOIN FETCH b.company c JOIN FETCH c.organization o JOIN FETCH o.tenant WHERE b.id IN :ids")
  List<Branch> findByIdInWithCompany(@Param("ids") List<UUID> ids);
}
