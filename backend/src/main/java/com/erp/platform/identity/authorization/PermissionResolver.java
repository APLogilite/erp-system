package com.erp.platform.identity.authorization;

import com.erp.platform.identity.authorization.PermissionCache.PermissionEntry;
import com.erp.platform.identity.entity.Permission;
import com.erp.platform.identity.entity.RolePermission;
import com.erp.platform.identity.entity.UserRole;
import com.erp.platform.identity.repository.RolePermissionRepository;
import com.erp.platform.identity.repository.UserRoleRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class PermissionResolver {

  private final UserRoleRepository userRoleRepository;
  private final RolePermissionRepository rolePermissionRepository;
  private final PermissionCache permissionCache;

  public PermissionResolver(UserRoleRepository userRoleRepository,
                            RolePermissionRepository rolePermissionRepository,
                            PermissionCache permissionCache) {
    this.userRoleRepository = userRoleRepository;
    this.rolePermissionRepository = rolePermissionRepository;
    this.permissionCache = permissionCache;
  }

  public List<PermissionEntry> resolveUserPermissions(UUID userId) {
    List<PermissionEntry> cached = permissionCache.getUserPermissions(userId);
    if (!cached.isEmpty()) return cached;

    List<UserRole> userRoles = userRoleRepository.findByUserId(userId);
    List<UUID> roleIds = userRoles.stream()
        .map(ur -> ur.getRole().getId())
        .distinct()
        .collect(Collectors.toList());

    List<PermissionEntry> entries = new ArrayList<>();

    if (!roleIds.isEmpty()) {
      List<RolePermission> rolePerms = rolePermissionRepository.findByRoleIdIn(roleIds);
      for (RolePermission rp : rolePerms) {
        Permission p = rp.getPermission();
        entries.add(new PermissionEntry(p.getResourceType(), p.getResource(), p.getAction()));
      }
    }

    List<PermissionEntry> distinct = entries.stream().distinct().collect(Collectors.toList());
    permissionCache.putUserPermissions(userId, distinct);
    return distinct;
  }

  public List<String> resolveUserRoles(UUID userId) {
    List<String> cached = permissionCache.getUserRoles(userId);
    if (!cached.isEmpty()) return cached;

    List<String> roleCodes = userRoleRepository.findByUserId(userId).stream()
        .map(ur -> ur.getRole().getCode())
        .collect(Collectors.toList());

    permissionCache.putUserRoles(userId, roleCodes);
    return roleCodes;
  }
}
