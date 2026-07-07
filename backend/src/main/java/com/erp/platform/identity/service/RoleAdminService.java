package com.erp.platform.identity.service;

import com.erp.platform.identity.authorization.PermissionCache;
import com.erp.platform.identity.entity.Permission;
import com.erp.platform.identity.entity.Role;
import com.erp.platform.identity.entity.RolePermission;
import com.erp.platform.identity.repository.PermissionRepository;
import com.erp.platform.identity.repository.RolePermissionRepository;
import com.erp.platform.identity.repository.RoleRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoleAdminService {

  private final RoleRepository roleRepository;
  private final PermissionRepository permissionRepository;
  private final RolePermissionRepository rolePermissionRepository;
  private final PermissionCache permissionCache;

  public RoleAdminService(RoleRepository roleRepository,
                          PermissionRepository permissionRepository,
                          RolePermissionRepository rolePermissionRepository,
                          PermissionCache permissionCache) {
    this.roleRepository = roleRepository;
    this.permissionRepository = permissionRepository;
    this.rolePermissionRepository = rolePermissionRepository;
    this.permissionCache = permissionCache;
  }

  @Transactional(readOnly = true)
  public List<Role> getAllRoles() { return roleRepository.findAll(); }
  public Role getRole(UUID id) { return roleRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Role not found")); }

  @Transactional
  public Role createRole(Role role) {
    if (roleRepository.findByCode(role.getCode()).isPresent()) {
      throw new IllegalArgumentException("Role code already exists");
    }
    return roleRepository.save(role);
  }

  @Transactional
  public Role updateRole(UUID id, Role req) {
    Role r = getRole(id);
    r.setName(req.getName());
    r.setDescription(req.getDescription());
    if (req.getIsSystem() != null) r.setIsSystem(req.getIsSystem());
    return roleRepository.save(r);
  }

  @Transactional
  public void deleteRole(UUID id) { roleRepository.deleteById(id); }

  @Transactional
  public Role cloneRole(UUID sourceId, String newCode, String newName) {
    Role source = getRole(sourceId);
    Role clone = new Role();
    clone.setCode(newCode);
    clone.setName(newName);
    clone.setDescription(source.getDescription() + " (cloned from " + source.getCode() + ")");
    clone.setTenant(source.getTenant());
    clone.setIsSystem(false);
    Role saved = roleRepository.save(clone);
    List<RolePermission> perms = rolePermissionRepository.findByRoleId(sourceId);
    for (RolePermission rp : perms) {
      RolePermission newRp = new RolePermission();
      newRp.setRole(saved);
      newRp.setPermission(rp.getPermission());
      rolePermissionRepository.save(newRp);
    }
    return saved;
  }

  @Transactional
  public void assignPermission(UUID roleId, UUID permissionId) {
    Role role = getRole(roleId);
    Permission p = permissionRepository.findById(permissionId).orElseThrow(() -> new IllegalArgumentException("Permission not found"));
    RolePermission rp = new RolePermission();
    rp.setRole(role);
    rp.setPermission(p);
    rolePermissionRepository.save(rp);
    permissionCache.invalidateAll();
  }

  @Transactional
  public void removePermission(UUID roleId, UUID permissionId) {
    List<RolePermission> list = rolePermissionRepository.findByRoleId(roleId);
    list.stream()
        .filter(rp -> rp.getPermission().getId().equals(permissionId))
        .findFirst().ifPresent(rolePermissionRepository::delete);
    permissionCache.invalidateAll();
  }

  public List<RolePermission> getRolePermissions(UUID roleId) {
    return rolePermissionRepository.findByRoleId(roleId);
  }

  // --- Permission browsing ---
  public List<Permission> getAllPermissions() { return permissionRepository.findAll(); }
  public List<Permission> getPermissionsByModule(String module) { return permissionRepository.findByModule(module); }
  public List<Permission> getPermissionsByResourceType(String resourceType) { return permissionRepository.findByResourceType(resourceType); }
}
