package com.erp.platform.identity.sdk.provider.impl;

import com.erp.platform.identity.authorization.AuthorizationService;
import com.erp.platform.identity.sdk.provider.PermissionProvider;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PermissionProviderImpl implements PermissionProvider {

    private final AuthorizationService authorizationService;

    public PermissionProviderImpl(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    @Override
    public boolean hasPermission(String resourceType, String resource, String action) {
        return authorizationService.hasPermission(resourceType, resource, action);
    }

    @Override
    public boolean hasAnyPermission(String resourceType, String resource, String... actions) {
        for (String action : actions) {
            if (authorizationService.hasPermission(resourceType, resource, action)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasModuleAccess(String module) {
        return authorizationService.hasModuleAccess(module);
    }

    @Override
    public boolean isAdmin() {
        return authorizationService.isAdmin();
    }

    @Override
    public void checkPermission(String resourceType, String resource, String action) {
        authorizationService.checkPermission(resourceType, resource, action);
    }

    @Override
    public List<String> getEffectivePermissions() {
        return authorizationService.getEffectivePermissions().stream()
                .map(p -> p.getResourceType() + ":" + p.getResource() + ":" + p.getAction())
                .toList();
    }
}
