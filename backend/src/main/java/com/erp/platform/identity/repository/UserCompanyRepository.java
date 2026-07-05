package com.erp.platform.identity.repository;

import com.erp.platform.identity.entity.UserCompany;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCompanyRepository extends JpaRepository<UserCompany, UUID> {
  List<UserCompany> findByUserId(UUID userId);
  Optional<UserCompany> findByUserIdAndIsDefaultTrue(UUID userId);
}
