package com.erp.modules.platform.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class DocumentResponse {
  private UUID id;
  private String fileName;
  private String mimeType;
  private Long fileSize;
  private String owner;
  private String module;
  private String recordId;
  private Integer version;
  private String checksum;
  private String category;
  private String folder;
  private String filePath;
  private String contentType;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public UUID getId() { return id; }
  public void setId(UUID id) { this.id = id; }
  public String getFileName() { return fileName; }
  public void setFileName(String fileName) { this.fileName = fileName; }
  public String getMimeType() { return mimeType; }
  public void setMimeType(String mimeType) { this.mimeType = mimeType; }
  public Long getFileSize() { return fileSize; }
  public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
  public String getOwner() { return owner; }
  public void setOwner(String owner) { this.owner = owner; }
  public String getModule() { return module; }
  public void setModule(String module) { this.module = module; }
  public String getRecordId() { return recordId; }
  public void setRecordId(String recordId) { this.recordId = recordId; }
  public Integer getVersion() { return version; }
  public void setVersion(Integer version) { this.version = version; }
  public String getChecksum() { return checksum; }
  public void setChecksum(String checksum) { this.checksum = checksum; }
  public String getCategory() { return category; }
  public void setCategory(String category) { this.category = category; }
  public String getFolder() { return folder; }
  public void setFolder(String folder) { this.folder = folder; }
  public String getFilePath() { return filePath; }
  public void setFilePath(String filePath) { this.filePath = filePath; }
  public String getContentType() { return contentType; }
  public void setContentType(String contentType) { this.contentType = contentType; }
  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
  public LocalDateTime getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
