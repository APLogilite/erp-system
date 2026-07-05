package com.erp.modules.platform.repository;

import com.erp.modules.platform.entity.Attachment;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, UUID> {
  List<Attachment> findByModuleAndRecordIdOrderByVersionDesc(String module, String recordId);
  List<Attachment> findByModuleAndRecordIdAndVersion(String module, String recordId, Integer version);
}
