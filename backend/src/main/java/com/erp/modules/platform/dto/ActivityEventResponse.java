package com.erp.modules.platform.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class ActivityEventResponse {
  private UUID id;
  private String eventType;
  private String module;
  private String recordId;
  private String actor;
  private String summary;
  private String details;
  private LocalDateTime occurredAt;

  public UUID getId() { return id; }
  public void setId(UUID id) { this.id = id; }
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
