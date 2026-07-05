package com.erp.platform.identity.entity;

import com.erp.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "identity_audit_records")
public class AuditRecord extends BaseEntity {

  @Column(name = "event_type", nullable = false, length = 50)
  private String eventType;

  @Column(name = "user_id")
  private UUID userId;

  @Column(name = "username", length = 100)
  private String username;

  @Column(name = "ip_address", length = 45)
  private String ipAddress;

  @Column(name = "user_agent", columnDefinition = "TEXT")
  private String userAgent;

  @Column(name = "session_id")
  private UUID sessionId;

  @Column(name = "old_value", columnDefinition = "TEXT")
  private String oldValue;

  @Column(name = "new_value", columnDefinition = "TEXT")
  private String newValue;

  @Column(name = "occurred_at", nullable = false)
  private LocalDateTime occurredAt;

  public String getEventType() { return eventType; }
  public void setEventType(String eventType) { this.eventType = eventType; }
  public UUID getUserId() { return userId; }
  public void setUserId(UUID userId) { this.userId = userId; }
  public String getUsername() { return username; }
  public void setUsername(String username) { this.username = username; }
  public String getIpAddress() { return ipAddress; }
  public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
  public String getUserAgent() { return userAgent; }
  public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
  public UUID getSessionId() { return sessionId; }
  public void setSessionId(UUID sessionId) { this.sessionId = sessionId; }
  public String getOldValue() { return oldValue; }
  public void setOldValue(String oldValue) { this.oldValue = oldValue; }
  public String getNewValue() { return newValue; }
  public void setNewValue(String newValue) { this.newValue = newValue; }
  public LocalDateTime getOccurredAt() { return occurredAt; }
  public void setOccurredAt(LocalDateTime occurredAt) { this.occurredAt = occurredAt; }
}
