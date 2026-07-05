package com.erp.platform.identity.sdk.provider;

import com.erp.platform.identity.entity.UserSession;

import java.util.List;
import java.util.Optional;

public interface SessionProvider {
    Optional<String> getCurrentSessionId();
    Optional<UserSession> getCurrentSession();
    List<UserSession> getActiveSessions(String userId);
    void forceLogout(String sessionId);
    boolean isSessionActive(String sessionId);
}
