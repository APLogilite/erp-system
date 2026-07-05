package com.erp.platform.identity.sdk;

import com.erp.platform.identity.authorization.PermissionEvaluator;
import com.erp.platform.identity.authorization.PermissionResolver;
import com.erp.platform.identity.entity.UserAccount;
import com.erp.platform.identity.entity.UserSession;
import com.erp.platform.identity.repository.UserAccountRepository;
import com.erp.platform.identity.repository.UserSessionRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class IdentityClient {

    private final UserAccountRepository userAccountRepository;
    private final UserSessionRepository userSessionRepository;
    private final PermissionEvaluator permissionEvaluator;
    private final PermissionResolver permissionResolver;

    public IdentityClient(UserAccountRepository userAccountRepository,
                          UserSessionRepository userSessionRepository,
                          PermissionEvaluator permissionEvaluator,
                          PermissionResolver permissionResolver) {
        this.userAccountRepository = userAccountRepository;
        this.userSessionRepository = userSessionRepository;
        this.permissionEvaluator = permissionEvaluator;
        this.permissionResolver = permissionResolver;
    }

    public Optional<UserAccount> findUserById(String userId) {
        return userAccountRepository.findById(UUID.fromString(userId));
    }

    public Optional<UserAccount> findUserByUsername(String username) {
        return userAccountRepository.findByUsername(username);
    }

    public Optional<UserSession> findSessionById(String sessionId) {
        return userSessionRepository.findById(UUID.fromString(sessionId));
    }

    public boolean hasPermission(String userId, String resourceType, String resource, String action) {
        return permissionEvaluator.hasPermission(UUID.fromString(userId), resourceType, resource, action);
    }

    public boolean isAdmin(String userId) {
        return permissionEvaluator.isAdmin(UUID.fromString(userId));
    }

    public List<String> getUserRoles(String userId) {
        return permissionResolver.resolveUserRoles(UUID.fromString(userId));
    }

    public List<String> getUserPermissionStrings(String userId) {
        return permissionResolver.resolveUserPermissions(UUID.fromString(userId)).stream()
                .map(p -> p.getResourceType() + ":" + p.getResource() + ":" + p.getAction())
                .toList();
    }

    public List<UserSession> getActiveSessions(String userId) {
        return userSessionRepository.findByUserIdAndIsActiveTrue(UUID.fromString(userId));
    }
}
