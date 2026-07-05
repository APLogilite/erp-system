package com.erp.platform.identity.sdk.provider;

import java.util.List;

public interface PermissionProvider {
    boolean hasPermission(String resourceType, String resource, String action);
    boolean hasAnyPermission(String resourceType, String resource, String... actions);
    boolean hasModuleAccess(String module);
    boolean isAdmin();
    void checkPermission(String resourceType, String resource, String action);
    List<String> getEffectivePermissions();
}
