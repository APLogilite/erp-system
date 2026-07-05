package com.erp.platform.identity.repository;

import com.erp.platform.identity.entity.UserSession;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, UUID> {
  Optional<UserSession> findByToken(String token);
  List<UserSession> findByUserId(UUID userId);
  List<UserSession> findByUserIdAndIsActiveTrue(UUID userId);
  void deleteByUserId(UUID userId);
}
