package com.erp.platform.identity.repository;

import com.erp.platform.identity.entity.UserBranch;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserBranchRepository extends JpaRepository<UserBranch, UUID> {
  List<UserBranch> findByUserId(UUID userId);
  Optional<UserBranch> findByUserIdAndIsDefaultTrue(UUID userId);
}
