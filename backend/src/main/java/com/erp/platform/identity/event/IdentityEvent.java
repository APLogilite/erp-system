package com.erp.platform.identity.event;

import java.time.LocalDateTime;
import java.util.UUID;

public class IdentityEvent {

  private final IdentityEventType eventType;
  private final UUID userId;
  private final String username;
  private final String ipAddress;
  private final String userAgent;
  private final UUID sessionId;
  private final String oldValue;
  private final String newValue;
  private final LocalDateTime occurredAt;

  public IdentityEvent(IdentityEventType eventType, UUID userId, String username,
                       String ipAddress, String userAgent, UUID sessionId,
                       String oldValue, String newValue) {
    this.eventType = eventType;
    this.userId = userId;
    this.username = username;
    this.ipAddress = ipAddress;
    this.userAgent = userAgent;
    this.sessionId = sessionId;
    this.oldValue = oldValue;
    this.newValue = newValue;
    this.occurredAt = LocalDateTime.now();
  }

  public IdentityEventType getEventType() { return eventType; }
  public UUID getUserId() { return userId; }
  public String getUsername() { return username; }
  public String getIpAddress() { return ipAddress; }
  public String getUserAgent() { return userAgent; }
  public UUID getSessionId() { return sessionId; }
  public String getOldValue() { return oldValue; }
  public String getNewValue() { return newValue; }
  public LocalDateTime getOccurredAt() { return occurredAt; }
}
