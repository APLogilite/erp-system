package com.erp.platform.identity.repository;

import com.erp.platform.identity.entity.Branch;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BranchRepository extends JpaRepository<Branch, UUID> {
  Optional<Branch> findByCode(String code);
  List<Branch> findByCompanyId(UUID companyId);
}
