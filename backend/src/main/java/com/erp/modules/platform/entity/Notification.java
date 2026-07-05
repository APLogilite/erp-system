package com.erp.modules.platform.entity;

import com.erp.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification extends BaseEntity {

  @Column(name = "title", nullable = false)
  private String title;

  @Column(name = "message", nullable = false, columnDefinition = "text")
  private String message;

  @Column(name = "type", nullable = false, length = 20)
  private String type;

  @Column(name = "priority", length = 10)
  private String priority;

  @Column(name = "recipient", nullable = false)
  private String recipient;

  @Column(name = "module", length = 50)
  private String module;

  @Column(name = "record_id")
  private String recordId;

  @Column(name = "status", nullable = false, length = 20)
  private String status;

  @Column(name = "read_at")
  private LocalDateTime readAt;

  public String getTitle() { return title; }
  public void setTitle(String title) { this.title = title; }
  public String getMessage() { return message; }
  public void setMessage(String message) { this.message = message; }
  public String getType() { return type; }
  public void setType(String type) { this.type = type; }
  public String getPriority() { return priority; }
  public void setPriority(String priority) { this.priority = priority; }
  public String getRecipient() { return recipient; }
  public void setRecipient(String recipient) { this.recipient = recipient; }
  public String getModule() { return module; }
  public void setModule(String module) { this.module = module; }
  public String getRecordId() { return recordId; }
  public void setRecordId(String recordId) { this.recordId = recordId; }
  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }
  public LocalDateTime getReadAt() { return readAt; }
  public void setReadAt(LocalDateTime readAt) { this.readAt = readAt; }
}
