package com.erp.platform.identity.event;

import com.erp.platform.identity.entity.AuditRecord;
import com.erp.platform.identity.repository.AuditRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class IdentityEventListener {

  private static final Logger log = LoggerFactory.getLogger(IdentityEventListener.class);

  private final AuditRecordRepository auditRecordRepository;

  public IdentityEventListener(AuditRecordRepository auditRecordRepository) {
    this.auditRecordRepository = auditRecordRepository;
  }

  @EventListener
  @Transactional
  public void handleIdentityEvent(IdentityEvent event) {
    AuditRecord record = new AuditRecord();
    record.setEventType(event.getEventType().name());
    record.setUserId(event.getUserId());
    record.setUsername(event.getUsername());
    record.setIpAddress(event.getIpAddress());
    record.setUserAgent(event.getUserAgent());
    record.setSessionId(event.getSessionId());
    record.setOldValue(event.getOldValue());
    record.setNewValue(event.getNewValue());
    record.setOccurredAt(event.getOccurredAt());
    auditRecordRepository.save(record);
    log.info("Audit record saved: {} for user {}", event.getEventType(), event.getUsername());
  }
}
