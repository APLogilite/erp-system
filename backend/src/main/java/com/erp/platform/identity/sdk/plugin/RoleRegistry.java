package com.erp.platform.identity.sdk.plugin;

import com.erp.platform.identity.entity.Role;
import com.erp.platform.identity.entity.RolePermission;
import com.erp.platform.identity.entity.Permission;
import com.erp.platform.identity.repository.RoleRepository;
import com.erp.platform.identity.repository.RolePermissionRepository;
import com.erp.platform.identity.repository.PermissionRepository;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class RoleRegistry {

    private final RoleRepository roleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final PermissionRepository permissionRepository;
    private final Map<String, RegisteredRole> dynamicRoles = new LinkedHashMap<>();

    public RoleRegistry(RoleRepository roleRepository,
                        RolePermissionRepository rolePermissionRepository,
                        PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.rolePermissionRepository = rolePermissionRepository;
        this.permissionRepository = permissionRepository;
    }

    public void register(RegisteredRole role) {
        dynamicRoles.put(role.code(), role);
    }

    public void registerAll(List<RegisteredRole> roles) {
        roles.forEach(this::register);
    }

    public List<RegisteredRole> getDynamicRoles() {
        return List.copyOf(dynamicRoles.values());
    }

    public void persistToDatabase() {
        for (RegisteredRole rr : dynamicRoles.values()) {
            Optional<Role> existing = roleRepository.findByCode(rr.code());
            Role role = existing.orElseGet(() -> {
                Role r = new Role();
                r.setCode(rr.code());
                r.setName(rr.name());
                r.setDescription(rr.description());
                r.setIsSystem(true);
                return roleRepository.save(r);
            });

            for (String permCode : rr.permissionCodes()) {
                permissionRepository.findByCode(permCode).ifPresent(perm -> {
                    boolean alreadyAssigned = rolePermissionRepository.findByRoleId(role.getId()).stream()
                            .anyMatch(rp -> rp.getPermission().getId().equals(perm.getId()));
                    if (!alreadyAssigned) {
                        RolePermission rp = new RolePermission();
                        rp.setRole(role);
                        rp.setPermission(perm);
                        rolePermissionRepository.save(rp);
                    }
                });
            }
        }
    }

    public record RegisteredRole(
            String code,
            String name,
            String description,
            List<String> permissionCodes
    ) {}
}
