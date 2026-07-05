package com.erp.modules.platform.entity;

import com.erp.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "activity_events")
public class ActivityEvent extends BaseEntity {

  @Column(name = "event_type", nullable = false, length = 30)
  private String eventType;

  @Column(name = "module", length = 50)
  private String module;

  @Column(name = "record_id")
  private String recordId;

  @Column(name = "actor")
  private String actor;

  @Column(name = "summary", columnDefinition = "text")
  private String summary;

  @Column(name = "details", columnDefinition = "json")
  private String details;

  @Column(name = "occurred_at", nullable = false)
  private LocalDateTime occurredAt;

  public String getEventType() { return eventType; }
  public void setEventType(String eventType) { this.eventType = eventType; }
  public String getModule() { return module; }
  public void setModule(String module) { this.module = module; }
  public String getRecordId() { return recordId; }
  public void setRecordId(String recordId) { this.recordId = recordId; }
  public String getActor() { return actor; }
  public void setActor(String actor) { this.actor = actor; }
  public String getSummary() { return summary; }
  public void setSummary(String summary) { this.summary = summary; }
  public String getDetails() { return details; }
  public void setDetails(String details) { this.details = details; }
  public LocalDateTime getOccurredAt() { return occurredAt; }
  public void setOccurredAt(LocalDateTime occurredAt) { this.occurredAt = occurredAt; }
}
