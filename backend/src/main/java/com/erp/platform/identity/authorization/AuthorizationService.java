package com.erp.platform.identity.authorization;

import com.erp.platform.identity.authorization.PermissionCache.PermissionEntry;
import com.erp.platform.identity.dto.RuntimeContext;
import com.erp.platform.identity.dto.RuntimeContextHolder;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService {

  private final PermissionEvaluator permissionEvaluator;
  private final PermissionResolver permissionResolver;
  private final RoleResolver roleResolver;

  public AuthorizationService(PermissionEvaluator permissionEvaluator,
                              PermissionResolver permissionResolver,
                              RoleResolver roleResolver) {
    this.permissionEvaluator = permissionEvaluator;
    this.permissionResolver = permissionResolver;
    this.roleResolver = roleResolver;
  }

  public boolean hasPermission(String resourceType, String resource, String action) {
    UUID userId = resolveUserId();
    return permissionEvaluator.hasPermission(userId, resourceType, resource, action);
  }

  public void checkPermission(String resourceType, String resource, String action) {
    permissionEvaluator.checkPermission(resolveUserId(), resourceType, resource, action);
  }

  public boolean hasModuleAccess(String module) {
    return permissionEvaluator.hasModuleAccess(resolveUserId(), module);
  }

  public boolean isAdmin() {
    return permissionEvaluator.isAdmin(resolveUserId());
  }

  public List<PermissionEntry> getEffectivePermissions() {
    return permissionResolver.resolveUserPermissions(resolveUserId());
  }

  public List<String> getEffectiveRoles() {
    return permissionResolver.resolveUserRoles(resolveUserId());
  }

  public List<PermissionEntry> getEffectivePermissionsForUser(UUID userId) {
    return permissionResolver.resolveUserPermissions(userId);
  }

  public void invalidateCache() {
    permissionEvaluator.invalidateCache(resolveUserId());
  }

  public void invalidateCacheForUser(UUID userId) {
    permissionEvaluator.invalidateCache(userId);
  }

  private UUID resolveUserId() {
    RuntimeContext ctx = RuntimeContextHolder.get();
    if (ctx != null) return ctx.getUserId();
    throw new IllegalStateException("No RuntimeContext available");
  }
}
