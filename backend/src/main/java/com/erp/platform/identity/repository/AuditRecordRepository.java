package com.erp.platform.identity.repository;

import com.erp.platform.identity.entity.AuditRecord;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditRecordRepository extends JpaRepository<AuditRecord, UUID> {
  List<AuditRecord> findByUserIdOrderByOccurredAtDesc(UUID userId);
  List<AuditRecord> findByEventTypeOrderByOccurredAtDesc(String eventType);
  List<AuditRecord> findByUsernameOrderByOccurredAtDesc(String username);
  List<AuditRecord> findByOccurredAtBetweenOrderByOccurredAtDesc(LocalDateTime from, LocalDateTime to);
  List<AuditRecord> findAllByOrderByOccurredAtDesc();
}
