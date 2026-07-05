package com.erp.platform.identity.sdk;

import com.erp.platform.identity.dto.RuntimeContext;
import com.erp.platform.identity.dto.RuntimeContextHolder;
import com.erp.platform.identity.authorization.AuthorizationException;

import java.util.List;

public final class AuthorizationHelper {

    private AuthorizationHelper() {}

    public static boolean currentUserHasPermission(String resourceType, String resource, String action) {
        RuntimeContext ctx = RuntimeContextHolder.get();
        if (ctx == null) return false;
        return ctx.getPermissions().contains(resourceType + ":" + resource + ":" + action);
    }

    public static void requirePermission(String resourceType, String resource, String action) {
        if (!currentUserHasPermission(resourceType, resource, action)) {
            throw new AuthorizationException("ACCESS_DENIED",
                    "Missing permission: " + resourceType + ":" + resource + ":" + action);
        }
    }

    public static boolean isSystemAdmin() {
        RuntimeContext ctx = RuntimeContextHolder.get();
        if (ctx == null) return false;
        return ctx.getRoles().stream().anyMatch(r -> r.equals("sys_admin") || r.equals("tnt_admin"));
    }
}
