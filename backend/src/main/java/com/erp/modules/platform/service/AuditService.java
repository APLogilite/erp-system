package com.erp.modules.platform.service;

import com.erp.modules.platform.dto.AuditLogResponse;
import com.erp.modules.platform.entity.AuditLog;
import com.erp.modules.platform.repository.AuditLogRepository;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditService {

  private final AuditLogRepository auditLogRepository;

  public AuditService(AuditLogRepository auditLogRepository) {
    this.auditLogRepository = auditLogRepository;
  }

  @Transactional
  public AuditLogResponse log(String action, String module, String recordId,
                             String actor, String fieldName,
                             String oldValue, String newValue, String summary) {
    AuditLog log = new AuditLog();
    log.setAction(action);
    log.setModule(module);
    log.setRecordId(recordId);
    log.setActor(actor);
    log.setFieldName(fieldName);
    log.setOldValue(oldValue);
    log.setNewValue(newValue);
    log.setSummary(summary);
    AuditLog saved = auditLogRepository.save(log);
    return toResponse(saved);
  }

  public List<AuditLogResponse> getByModuleAndRecord(String module, String recordId) {
    return auditLogRepository.findByModuleAndRecordIdOrderByCreatedAtDesc(module, recordId)
        .stream().map(this::toResponse).collect(Collectors.toList());
  }

  public List<AuditLogResponse> getByModule(String module) {
    return auditLogRepository.findByModuleOrderByCreatedAtDesc(module)
        .stream().map(this::toResponse).collect(Collectors.toList());
  }

  public List<AuditLogResponse> getAll() {
    return auditLogRepository.findAllByOrderByCreatedAtDesc()
        .stream().map(this::toResponse).collect(Collectors.toList());
  }

  private AuditLogResponse toResponse(AuditLog log) {
    AuditLogResponse r = new AuditLogResponse();
    r.setId(log.getId());
    r.setAction(log.getAction());
    r.setModule(log.getModule());
    r.setRecordId(log.getRecordId());
    r.setActor(log.getActor());
    r.setFieldName(log.getFieldName());
    r.setOldValue(log.getOldValue());
    r.setNewValue(log.getNewValue());
    r.setSummary(log.getSummary());
    r.setCreatedAt(log.getCreatedAt());
    return r;
  }
}
