package com.erp.platform.identity.repository;

import com.erp.platform.identity.entity.UserAccount;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, UUID> {
  Optional<UserAccount> findByUsername(String username);
  Optional<UserAccount> findByEmail(String email);
  boolean existsByUsername(String username);
  boolean existsByEmail(String email);
}
