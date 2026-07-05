package com.erp.platform.identity.sdk.plugin;

import com.erp.platform.identity.entity.Permission;
import com.erp.platform.identity.repository.PermissionRepository;
import org.springframework.stereotype.Component;

import java.util.*;

@Component("identityPermissionRegistry")
public class PermissionRegistry {

    private final PermissionRepository permissionRepository;
    private final Map<String, RegisteredPermission> dynamicPermissions = new LinkedHashMap<>();

    public PermissionRegistry(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public void register(RegisteredPermission permission) {
        dynamicPermissions.put(permission.code(), permission);
    }

    public void registerAll(List<RegisteredPermission> permissions) {
        permissions.forEach(this::register);
    }

    public List<RegisteredPermission> getDynamicPermissions() {
        return List.copyOf(dynamicPermissions.values());
    }

    public void persistToDatabase() {
        for (RegisteredPermission rp : dynamicPermissions.values()) {
            if (permissionRepository.findByCode(rp.code()).isEmpty()) {
                Permission entity = new Permission();
                entity.setCode(rp.code());
                entity.setName(rp.name());
                entity.setResourceType(rp.resourceType());
                entity.setResource(rp.resource());
                entity.setAction(rp.action());
                entity.setModule(rp.module());
                entity.setIsSystem(false);
                permissionRepository.save(entity);
            }
        }
    }

    public record RegisteredPermission(
            String code,
            String name,
            String resourceType,
            String resource,
            String action,
            String module
    ) {}
}
