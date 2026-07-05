package com.erp.modules.platform.entity;

import com.erp.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "audit_logs")
public class AuditLog extends BaseEntity {

  @Column(name = "action", nullable = false, length = 20)
  private String action;

  @Column(name = "module", length = 50)
  private String module;

  @Column(name = "record_id")
  private String recordId;

  @Column(name = "actor")
  private String actor;

  @Column(name = "field_name", length = 50)
  private String fieldName;

  @Column(name = "old_value", columnDefinition = "text")
  private String oldValue;

  @Column(name = "new_value", columnDefinition = "text")
  private String newValue;

  @Column(name = "summary", columnDefinition = "text")
  private String summary;

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
}
