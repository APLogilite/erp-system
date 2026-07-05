package com.erp.modules.platform.repository;

import com.erp.modules.platform.entity.AuditLog;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
  List<AuditLog> findByModuleAndRecordIdOrderByCreatedAtDesc(String module, String recordId);
  List<AuditLog> findByModuleOrderByCreatedAtDesc(String module);
  List<AuditLog> findAllByOrderByCreatedAtDesc();
}
