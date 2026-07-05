package com.erp.platform.identity.sdk.provider.impl;

import com.erp.platform.identity.dto.RuntimeContext;
import com.erp.platform.identity.dto.RuntimeContextHolder;
import com.erp.platform.identity.entity.UserSession;
import com.erp.platform.identity.repository.UserSessionRepository;
import com.erp.platform.identity.sdk.provider.SessionProvider;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class SessionProviderImpl implements SessionProvider {

    private final UserSessionRepository userSessionRepository;

    public SessionProviderImpl(UserSessionRepository userSessionRepository) {
        this.userSessionRepository = userSessionRepository;
    }

    @Override
    public Optional<String> getCurrentSessionId() {
        return Optional.ofNullable(RuntimeContextHolder.get())
                .map(RuntimeContext::getSessionId);
    }

    @Override
    public Optional<UserSession> getCurrentSession() {
        return getCurrentSessionId()
                .flatMap(id -> userSessionRepository.findById(UUID.fromString(id)));
    }

    @Override
    public List<UserSession> getActiveSessions(String userId) {
        return userSessionRepository.findByUserIdAndIsActiveTrue(UUID.fromString(userId));
    }

    @Override
    public void forceLogout(String sessionId) {
        userSessionRepository.findById(UUID.fromString(sessionId)).ifPresent(session -> {
            session.softDelete();
            userSessionRepository.save(session);
        });
    }

    @Override
    public boolean isSessionActive(String sessionId) {
        return userSessionRepository.findById(UUID.fromString(sessionId))
                .filter(UserSession::getIsActive)
                .isPresent();
    }
}
