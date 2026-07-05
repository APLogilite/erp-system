package com.erp.platform.identity.sdk.plugin;

import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class MenuRegistry {

    private final Map<String, MenuItem> menus = new LinkedHashMap<>();

    public void register(MenuItem menu) {
        menus.put(menu.code(), menu);
    }

    public void registerAll(List<MenuItem> items) {
        items.forEach(this::register);
    }

    public List<MenuItem> getMenus() {
        return List.copyOf(menus.values());
    }

    public List<MenuItem> getMenusForPermission(List<String> userPermissions) {
        return menus.values().stream()
                .filter(m -> m.requiredPermission() == null
                        || userPermissions.contains(m.requiredPermission()))
                .toList();
    }

    public record MenuItem(
            String code,
            String label,
            String icon,
            String path,
            String parentCode,
            int sortOrder,
            String module,
            String requiredPermission
    ) {}
}
