package com.erp.platform.identity.event;

import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class IdentityEventPublisher {

  private static final Logger log = LoggerFactory.getLogger(IdentityEventPublisher.class);

  private final ApplicationEventPublisher eventPublisher;

  public IdentityEventPublisher(ApplicationEventPublisher eventPublisher) {
    this.eventPublisher = eventPublisher;
  }

  public void publish(IdentityEventType eventType, UUID userId, String username,
                      String ipAddress, String userAgent, UUID sessionId,
                      String oldValue, String newValue) {
    IdentityEvent event = new IdentityEvent(eventType, userId, username,
        ipAddress, userAgent, sessionId, oldValue, newValue);
    eventPublisher.publishEvent(event);
    log.debug("Published identity event: {} for user {}", eventType, username);
  }

  public void publish(IdentityEventType eventType, UUID userId, String username) {
    publish(eventType, userId, username, null, null, null, null, null);
  }

  public void publish(IdentityEventType eventType, UUID userId, String username,
                      String oldValue, String newValue) {
    publish(eventType, userId, username, null, null, null, oldValue, newValue);
  }

  public void userCreated(UUID userId, String username) {
    publish(IdentityEventType.USER_CREATED, userId, username);
  }

  public void userActivated(UUID userId, String username) {
    publish(IdentityEventType.USER_ACTIVATED, userId, username);
  }

  public void userDeactivated(UUID userId, String username) {
    publish(IdentityEventType.USER_DEACTIVATED, userId, username);
  }

  public void loginSuccess(UUID userId, String username, String ip, String userAgent, UUID sessionId) {
    publish(IdentityEventType.LOGIN_SUCCESS, userId, username, ip, userAgent, sessionId, null, null);
  }

  public void loginFailure(String username, String ip) {
    publish(IdentityEventType.LOGIN_FAILURE, null, username, ip, null, null, null, null);
  }

  public void roleAssigned(UUID userId, String username, String roleCode) {
    publish(IdentityEventType.ROLE_ASSIGNED, userId, username, null, roleCode);
  }

  public void roleRemoved(UUID userId, String username, String roleCode) {
    publish(IdentityEventType.ROLE_REMOVED, userId, username, roleCode, null);
  }

  public void passwordChanged(UUID userId, String username) {
    publish(IdentityEventType.PASSWORD_CHANGED, userId, username);
  }

  public void contextChanged(UUID userId, String username, String oldContext, String newContext) {
    publish(IdentityEventType.CONTEXT_CHANGED, userId, username, oldContext, newContext);
  }
}
