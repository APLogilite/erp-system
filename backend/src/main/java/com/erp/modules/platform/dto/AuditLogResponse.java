package com.erp.modules.platform.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class AuditLogResponse {
  private UUID id;
  private String action;
  private String module;
  private String recordId;
  private String actor;
  private String fieldName;
  private String oldValue;
  private String newValue;
  private String summary;
  private LocalDateTime createdAt;

  public UUID getId() { return id; }
  public void setId(UUID id) { this.id = id; }
  public String getAction() { return action; }
  public void setAction(String action) { this.action = action; }
  public String getModule() { return module; }
  public void setModule(String module) { this.module = module; }
  public String getRecordId() { return recordId; }
  public void setRecordId(String recordId) { this.recordId = recordId; }
  public String getActor() { return actor; }
  public void setActor(String actor) { this.actor = actor; }
  public String getFieldName() { return fieldName; }
  public void setFieldName(String fieldName) { this.fieldName = fieldName; }
  public String getOldValue() { return oldValue; }
  public void setOldValue(String oldValue) { this.oldValue = oldValue; }
  public String getNewValue() { return newValue; }
  public void setNewValue(String newValue) { this.newValue = newValue; }
  public String getSummary() { return summary; }
  public void setSummary(String summary) { this.summary = summary; }
  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
