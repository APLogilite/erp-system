package com.erp.platform.identity.sdk;

import com.erp.platform.identity.dto.RuntimeContext;
import com.erp.platform.identity.entity.UserAccount;
import com.erp.platform.identity.entity.UserSession;
import com.erp.platform.identity.sdk.provider.CurrentContextProvider;
import com.erp.platform.identity.sdk.provider.CurrentUserProvider;
import com.erp.platform.identity.sdk.provider.PermissionProvider;
import com.erp.platform.identity.sdk.provider.SessionProvider;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class IdentityFacade {

    private final CurrentUserProvider currentUser;
    private final CurrentContextProvider currentContext;
    private final PermissionProvider permissions;
    private final SessionProvider sessions;

    public IdentityFacade(CurrentUserProvider currentUser,
                          CurrentContextProvider currentContext,
                          PermissionProvider permissions,
                          SessionProvider sessions) {
        this.currentUser = currentUser;
        this.currentContext = currentContext;
        this.permissions = permissions;
        this.sessions = sessions;
    }

    // User
    public Optional<UserAccount> getCurrentUser() { return currentUser.getCurrentUser(); }
    public String getCurrentUserId() { return currentUser.getCurrentUserId(); }
    public String getCurrentUsername() { return currentUser.getCurrentUsername(); }
    public String getCurrentUserEmail() { return currentUser.getCurrentUserEmail(); }
    public String getCurrentUserDisplayName() { return currentUser.getCurrentUserDisplayName(); }
    public boolean isAuthenticated() { return currentUser.isAuthenticated(); }

    // Context
    public Optional<RuntimeContext> getCurrentContext() { return currentContext.getCurrentContext(); }
    public String getCurrentTenantId() { return currentContext.getCurrentTenantId(); }
    public String getCurrentOrganizationId() { return currentContext.getCurrentOrganizationId(); }
    public String getCurrentCompanyId() { return currentContext.getCurrentCompanyId(); }
    public String getCurrentBranchId() { return currentContext.getCurrentBranchId(); }
    public String getCurrentDepartmentId() { return currentContext.getCurrentDepartmentId(); }
    public List<String> getCurrentRoles() { return currentContext.getCurrentRoles(); }
    public List<String> getCurrentPermissions() { return currentContext.getCurrentPermissions(); }

    // Permissions
    public boolean hasPermission(String resourceType, String resource, String action) {
        return permissions.hasPermission(resourceType, resource, action);
    }
    public boolean hasAnyPermission(String resourceType, String resource, String... actions) {
        return permissions.hasAnyPermission(resourceType, resource, actions);
    }
    public boolean hasModuleAccess(String module) { return permissions.hasModuleAccess(module); }
    public boolean isAdmin() { return permissions.isAdmin(); }
    public void checkPermission(String resourceType, String resource, String action) {
        permissions.checkPermission(resourceType, resource, action);
    }

    // Sessions
    public Optional<String> getCurrentSessionId() { return sessions.getCurrentSessionId(); }
    public Optional<UserSession> getCurrentSession() { return sessions.getCurrentSession(); }
    public List<UserSession> getActiveSessions(String userId) { return sessions.getActiveSessions(userId); }
    public void forceLogout(String sessionId) { sessions.forceLogout(sessionId); }
    public boolean isSessionActive(String sessionId) { return sessions.isSessionActive(sessionId); }

    public CurrentUserProvider user() { return currentUser; }
    public CurrentContextProvider context() { return currentContext; }
    public PermissionProvider permission() { return permissions; }
    public SessionProvider session() { return sessions; }
}
