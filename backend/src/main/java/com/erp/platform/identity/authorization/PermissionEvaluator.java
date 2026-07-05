package com.erp.platform.identity.authorization;

import com.erp.platform.identity.authorization.PermissionCache.PermissionEntry;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component("identityPermissionEvaluator")
public class PermissionEvaluator {

  private final PermissionResolver permissionResolver;
  private final PermissionCache permissionCache;

  public PermissionEvaluator(PermissionResolver permissionResolver,
                             PermissionCache permissionCache) {
    this.permissionResolver = permissionResolver;
    this.permissionCache = permissionCache;
  }

  public boolean hasPermission(UUID userId, String resourceType, String resource, String action) {
    List<PermissionEntry> permissions = permissionResolver.resolveUserPermissions(userId);
    return permissions.stream().anyMatch(p ->
        matches(p, resourceType, resource, action)
    );
  }

  public boolean hasAnyPermission(UUID userId, String resourceType, String resource, String... actions) {
    List<PermissionEntry> permissions = permissionResolver.resolveUserPermissions(userId);
    for (String action : actions) {
      if (permissions.stream().anyMatch(p -> matches(p, resourceType, resource, action))) {
        return true;
      }
    }
    return false;
  }

  public boolean hasModuleAccess(UUID userId, String module) {
    return hasPermission(userId, "MODULE", module, "READ")
        || hasPermission(userId, "MODULE", module, "ADMIN");
  }

  public boolean isAdmin(UUID userId) {
    List<String> roles = permissionResolver.resolveUserRoles(userId);
    return roles.contains("sys_admin") || roles.contains("tnt_admin");
  }

  public void checkPermission(UUID userId, String resourceType, String resource, String action) {
    if (!hasPermission(userId, resourceType, resource, action)) {
      throw new AuthorizationException("IDENTITY_AUTH_005",
          "Permission denied: " + resourceType + "." + resource + "." + action);
    }
  }

  public void invalidateCache(UUID userId) {
    permissionCache.invalidateUser(userId);
  }

  private boolean matches(PermissionEntry p, String resourceType, String resource, String action) {
    if (!p.getResourceType().equalsIgnoreCase(resourceType)) return false;
    if (!p.getResource().equalsIgnoreCase(resource)) return false;
    if (p.getAction().equalsIgnoreCase("ADMIN")) return true;
    if (!p.getAction().equalsIgnoreCase(action)) return false;
    return true;
  }
}
