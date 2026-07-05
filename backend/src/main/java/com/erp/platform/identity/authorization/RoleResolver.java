package com.erp.platform.identity.authorization;

import com.erp.platform.identity.entity.Role;
import com.erp.platform.identity.entity.UserRole;
import com.erp.platform.identity.repository.RoleRepository;
import com.erp.platform.identity.repository.UserRoleRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class RoleResolver {

  private final UserRoleRepository userRoleRepository;
  private final RoleRepository roleRepository;
  private final PermissionCache permissionCache;

  public RoleResolver(UserRoleRepository userRoleRepository,
                      RoleRepository roleRepository,
                      PermissionCache permissionCache) {
    this.userRoleRepository = userRoleRepository;
    this.roleRepository = roleRepository;
    this.permissionCache = permissionCache;
  }

  public List<Role> getUserRoles(UUID userId) {
    return userRoleRepository.findByUserId(userId).stream()
        .map(UserRole::getRole)
        .collect(Collectors.toList());
  }

  public List<String> getUserRoleCodes(UUID userId) {
    return getUserRoles(userId).stream()
        .map(Role::getCode)
        .collect(Collectors.toList());
  }

  public boolean hasRole(UUID userId, String roleCode) {
    return getUserRoleCodes(userId).contains(roleCode);
  }

  public Optional<Role> findByCode(String code) {
    return roleRepository.findByCode(code);
  }

  public void assignRole(UUID userId, UUID roleId) {
    permissionCache.invalidateUser(userId);
  }

  public void removeRole(UUID userId, UUID roleId) {
    permissionCache.invalidateUser(userId);
  }
}
