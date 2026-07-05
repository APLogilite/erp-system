package com.erp.platform.identity.service;

import com.erp.platform.identity.entity.UserSession;
import com.erp.platform.identity.repository.UserSessionRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SessionAdminService {

  private final UserSessionRepository sessionRepository;

  public SessionAdminService(UserSessionRepository sessionRepository) {
    this.sessionRepository = sessionRepository;
  }

  public List<UserSession> getActiveSessions() {
    return sessionRepository.findAll().stream()
        .filter(s -> s.getIsActive() != null && s.getIsActive())
        .toList();
  }

  public List<UserSession> getUserSessions(UUID userId) {
    return sessionRepository.findByUserId(userId);
  }

  @Transactional
  public void forceLogout(UUID sessionId) {
    UserSession session = sessionRepository.findById(sessionId)
        .orElseThrow(() -> new IllegalArgumentException("Session not found"));
    session.softDelete();
    sessionRepository.save(session);
  }

  @Transactional
  public void forceLogoutAll(UUID userId) {
    List<UserSession> sessions = sessionRepository.findByUserId(userId);
    for (UserSession s : sessions) {
      s.softDelete();
    }
    sessionRepository.saveAll(sessions);
  }
}
