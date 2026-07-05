package com.erp.platform.identity.event;

import com.erp.platform.identity.entity.AuditRecord;
import com.erp.platform.identity.repository.AuditRecordRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class AuditQueryService {

  private final AuditRecordRepository auditRecordRepository;

  public AuditQueryService(AuditRecordRepository auditRecordRepository) {
    this.auditRecordRepository = auditRecordRepository;
  }

  public List<AuditRecord> getAll() {
    return auditRecordRepository.findAllByOrderByOccurredAtDesc();
  }

  public List<AuditRecord> getByUser(UUID userId) {
    return auditRecordRepository.findByUserIdOrderByOccurredAtDesc(userId);
  }

  public List<AuditRecord> getByUsername(String username) {
    return auditRecordRepository.findByUsernameOrderByOccurredAtDesc(username);
  }

  public List<AuditRecord> getByEventType(String eventType) {
    return auditRecordRepository.findByEventTypeOrderByOccurredAtDesc(eventType);
  }

  public List<AuditRecord> getByDateRange(LocalDateTime from, LocalDateTime to) {
    return auditRecordRepository.findByOccurredAtBetweenOrderByOccurredAtDesc(from, to);
  }
}
