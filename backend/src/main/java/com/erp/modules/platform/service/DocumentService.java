package com.erp.modules.platform.service;

import com.erp.modules.platform.dto.DocumentResponse;
import com.erp.modules.platform.entity.Document;
import com.erp.modules.platform.repository.DocumentRepository;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DocumentService {

  private final DocumentRepository documentRepository;

  public DocumentService(DocumentRepository documentRepository) {
    this.documentRepository = documentRepository;
  }

  @Transactional
  public DocumentResponse upload(String fileName, String mimeType, Long fileSize,
                                String owner, String module, String recordId,
                                String category, String folder, String filePath,
                                String contentType) {
    Document doc = new Document();
    doc.setFileName(fileName);
    doc.setMimeType(mimeType);
    doc.setFileSize(fileSize);
    doc.setOwner(owner);
    doc.setModule(module);
    doc.setRecordId(recordId);
    doc.setCategory(category);
    doc.setFolder(folder);
    doc.setFilePath(filePath);
    doc.setContentType(contentType);
    doc.setVersion(1);
    doc.setChecksum(String.valueOf(System.currentTimeMillis()));
    Document saved = documentRepository.save(doc);
    return toResponse(saved);
  }

  @Transactional
  public DocumentResponse newVersion(UUID id, String filePath, Long fileSize) {
    Document doc = documentRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Document not found: " + id));
    Document newDoc = new Document();
    newDoc.setFileName(doc.getFileName());
    newDoc.setMimeType(doc.getMimeType());
    newDoc.setFileSize(fileSize);
    newDoc.setOwner(doc.getOwner());
    newDoc.setModule(doc.getModule());
    newDoc.setRecordId(doc.getRecordId());
    newDoc.setCategory(doc.getCategory());
    newDoc.setFolder(doc.getFolder());
    newDoc.setFilePath(filePath);
    newDoc.setContentType(doc.getContentType());
    newDoc.setVersion(doc.getVersion() + 1);
    newDoc.setChecksum(String.valueOf(System.currentTimeMillis()));
    Document saved = documentRepository.save(newDoc);
    return toResponse(saved);
  }

  public List<DocumentResponse> getByModuleAndRecord(String module, String recordId) {
    return documentRepository.findByModuleAndRecordId(module, recordId)
        .stream().map(this::toResponse).collect(Collectors.toList());
  }

  public List<DocumentResponse> getByCategory(String category) {
    return documentRepository.findByCategory(category)
        .stream().map(this::toResponse).collect(Collectors.toList());
  }

  public List<DocumentResponse> getAll() {
    return documentRepository.findAll().stream()
        .map(this::toResponse).collect(Collectors.toList());
  }

  public DocumentResponse getById(UUID id) {
    Document doc = documentRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Document not found: " + id));
    return toResponse(doc);
  }

  @Transactional
  public void delete(UUID id) {
    documentRepository.deleteById(id);
  }

  private DocumentResponse toResponse(Document doc) {
    DocumentResponse r = new DocumentResponse();
    r.setId(doc.getId());
    r.setFileName(doc.getFileName());
    r.setMimeType(doc.getMimeType());
    r.setFileSize(doc.getFileSize());
    r.setOwner(doc.getOwner());
    r.setModule(doc.getModule());
    r.setRecordId(doc.getRecordId());
    r.setVersion(doc.getVersion());
    r.setChecksum(doc.getChecksum());
    r.setCategory(doc.getCategory());
    r.setFolder(doc.getFolder());
    r.setFilePath(doc.getFilePath());
    r.setContentType(doc.getContentType());
    r.setCreatedAt(doc.getCreatedAt());
    r.setUpdatedAt(doc.getUpdatedAt());
    return r;
  }
}
