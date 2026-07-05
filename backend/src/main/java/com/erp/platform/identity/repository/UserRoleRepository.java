package com.erp.platform.identity.repository;

import com.erp.platform.identity.entity.UserRole;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UUID> {
  List<UserRole> findByUserId(UUID userId);
  List<UserRole> findByRoleId(UUID roleId);
  void deleteByUserIdAndRoleId(UUID userId, UUID roleId);
}
