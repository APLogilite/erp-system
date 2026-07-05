package com.erp.modules.platform.entity;

import com.erp.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "attachments")
public class Attachment extends BaseEntity {

  @Column(name = "file_name", nullable = false)
  private String fileName;

  @Column(name = "mime_type", length = 100)
  private String mimeType;

  @Column(name = "file_size")
  private Long fileSize;

  @Column(name = "module", nullable = false, length = 50)
  private String module;

  @Column(name = "record_id", nullable = false)
  private String recordId;

  @Column(name = "version", nullable = false)
  private Integer version;

  @Column(name = "checksum", length = 64)
  private String checksum;

  @Column(name = "file_path", nullable = false)
  private String filePath;

  @Column(name = "description", columnDefinition = "text")
  private String description;

  public String getFileName() { return fileName; }
  public void setFileName(String fileName) { this.fileName = fileName; }
  public String getMimeType() { return mimeType; }
  public void setMimeType(String mimeType) { this.mimeType = mimeType; }
  public Long getFileSize() { return fileSize; }
  public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
  public String getModule() { return module; }
  public void setModule(String module) { this.module = module; }
  public String getRecordId() { return recordId; }
  public void setRecordId(String recordId) { this.recordId = recordId; }
  public Integer getVersion() { return version; }
  public void setVersion(Integer version) { this.version = version; }
  public String getChecksum() { return checksum; }
  public void setChecksum(String checksum) { this.checksum = checksum; }
  public String getFilePath() { return filePath; }
  public void setFilePath(String filePath) { this.filePath = filePath; }
  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }
}
