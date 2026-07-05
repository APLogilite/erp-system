package com.erp.platform.identity.sdk.plugin;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class PluginRegistryManager {

    private final List<PluginProvider> plugins = new ArrayList<>();
    private final PermissionRegistry permissionRegistry;
    private final RoleRegistry roleRegistry;
    private final MenuRegistry menuRegistry;
    private final ContextExtensionRegistry contextExtensionRegistry;

    public PluginRegistryManager(PermissionRegistry permissionRegistry,
                                 RoleRegistry roleRegistry,
                                 MenuRegistry menuRegistry,
                                 ContextExtensionRegistry contextExtensionRegistry) {
        this.permissionRegistry = permissionRegistry;
        this.roleRegistry = roleRegistry;
        this.menuRegistry = menuRegistry;
        this.contextExtensionRegistry = contextExtensionRegistry;
    }

    public void registerPlugin(PluginProvider plugin) {
        plugins.add(plugin);
        permissionRegistry.registerAll(plugin.getPermissions());
        roleRegistry.registerAll(plugin.getRoles());
        menuRegistry.registerAll(plugin.getMenus());
        plugin.getContextEnrichments().forEach((key, enricher) ->
                contextExtensionRegistry.registerEnrichment(key, enricher));
    }

    @PostConstruct
    public void initialize() {
        plugins.forEach(this::registerPlugin);
    }

    public List<PluginProvider> getPlugins() {
        return List.copyOf(plugins);
    }
}
