package com.erp.modules.platform.entity;

import com.erp.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "platform_events")
public class PlatformEvent extends BaseEntity {

  @Column(name = "event_type", nullable = false, length = 50)
  private String eventType;

  @Column(name = "source_module", length = 50)
  private String sourceModule;

  @Column(name = "source_id")
  private String sourceId;

  @Column(name = "payload", columnDefinition = "json")
  private String payload;

  @Column(name = "status", length = 20)
  private String status;

  @Column(name = "occurred_at", nullable = false)
  private LocalDateTime occurredAt;

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
