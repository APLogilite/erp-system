package com.erp.modules.platform.service;

import com.erp.modules.platform.entity.Attachment;
import com.erp.modules.platform.repository.AttachmentRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AttachmentService {

  private final AttachmentRepository attachmentRepository;

  public AttachmentService(AttachmentRepository attachmentRepository) {
    this.attachmentRepository = attachmentRepository;
  }

  @Transactional
  public Attachment attach(String fileName, String mimeType, Long fileSize,
                          String module, String recordId, String filePath,
                          String description) {
    List<Attachment> existing = attachmentRepository
        .findByModuleAndRecordIdOrderByVersionDesc(module, recordId);
    int nextVersion = existing.isEmpty() ? 1 : existing.get(0).getVersion() + 1;

    Attachment a = new Attachment();
    a.setFileName(fileName);
    a.setMimeType(mimeType);
    a.setFileSize(fileSize);
    a.setModule(module);
    a.setRecordId(recordId);
    a.setVersion(nextVersion);
    a.setChecksum(String.valueOf(System.currentTimeMillis()));
    a.setFilePath(filePath);
    a.setDescription(description);
    return attachmentRepository.save(a);
  }

  public List<Attachment> getAttachments(String module, String recordId) {
    return attachmentRepository.findByModuleAndRecordIdOrderByVersionDesc(module, recordId);
  }

  public Attachment getById(UUID id) {
    return attachmentRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Attachment not found: " + id));
  }

  @Transactional
  public void detach(UUID id) {
    attachmentRepository.deleteById(id);
  }

  @Transactional
  public void detachAll(String module, String recordId) {
    List<Attachment> list = attachmentRepository.findByModuleAndRecordIdOrderByVersionDesc(module, recordId);
    attachmentRepository.deleteAll(list);
  }
}
