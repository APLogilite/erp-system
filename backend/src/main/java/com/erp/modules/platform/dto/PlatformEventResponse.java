package com.erp.modules.platform.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class PlatformEventResponse {
  private UUID id;
  private String eventType;
  private String sourceModule;
  private String sourceId;
  private String payload;
  private String status;
  private LocalDateTime occurredAt;

  public UUID getId() { return id; }
  public void setId(UUID id) { this.id = id; }
  public String getEventType() { return eventType; }
  public void setEventType(String eventType) { this.eventType = eventType; }
  public String getSourceModule() { return sourceModule; }
  public void setSourceModule(String sourceModule) { this.sourceModule = sourceModule; }
  public String getSourceId() { return sourceId; }
  public void setSourceId(String sourceId) { this.sourceId = sourceId; }
  public String getPayload() { return payload; }
  public void setPayload(String payload) { this.payload = payload; }
  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }
  public LocalDateTime getOccurredAt() { return occurredAt; }
  public void setOccurredAt(LocalDateTime occurredAt) { this.occurredAt = occurredAt; }
}
