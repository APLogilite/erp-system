package com.erp.platform.identity.sdk.integration;

import com.erp.platform.identity.sdk.provider.PermissionProvider;
import org.springframework.stereotype.Component;

@Component
public class MetadataIntegration {

    private final PermissionProvider permissionProvider;

    public MetadataIntegration(PermissionProvider permissionProvider) {
        this.permissionProvider = permissionProvider;
    }

    public boolean canAccessModel(String modelName) {
        return permissionProvider.hasModuleAccess(modelName);
    }

    public boolean canAccessView(String modelName, String viewType) {
        return permissionProvider.hasPermission("VIEW", modelName + ":" + viewType, "READ");
    }

    public boolean canExecuteAction(String modelName, String action) {
        return permissionProvider.hasPermission("ACTION", modelName, action);
    }
}
