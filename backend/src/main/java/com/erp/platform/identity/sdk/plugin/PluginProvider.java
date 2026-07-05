package com.erp.platform.identity.sdk.plugin;

import com.erp.platform.identity.dto.RuntimeContext;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public interface PluginProvider {
    String getPluginName();
    String getPluginVersion();

    default List<PermissionRegistry.RegisteredPermission> getPermissions() {
        return Collections.emptyList();
    }

    default List<RoleRegistry.RegisteredRole> getRoles() {
        return Collections.emptyList();
    }

    default List<MenuRegistry.MenuItem> getMenus() {
        return Collections.emptyList();
    }

    default Map<String, Consumer<RuntimeContext>> getContextEnrichments() {
        return Collections.emptyMap();
    }
}
