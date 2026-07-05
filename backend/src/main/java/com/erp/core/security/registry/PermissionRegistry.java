package com.erp.core.security.registry;

import com.erp.core.security.dto.PermissionMetadataDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class PermissionRegistry {

  private final Map<String, PermissionMetadataDto> permissions = new ConcurrentHashMap<>();

  public void registerPermission(PermissionMetadataDto permission) {
    if (permissions.containsKey(permission.getCode())) {
      throw new IllegalArgumentException("Permission already registered: " + permission.getCode());
    }
    permissions.put(permission.getCode(), permission);
  }

  public PermissionMetadataDto findPermission(String code) {
    PermissionMetadataDto permission = permissions.get(code);
    if (permission == null) {
      throw new IllegalArgumentException("Permission not found: " + code);
    }
    return permission;
  }

  public List<PermissionMetadataDto> getAllPermissions() {
    return List.copyOf(permissions.values());
  }

  public List<PermissionMetadataDto> getPermissionsByResource(String resource) {
    return permissions.values().stream()
        .filter(permission -> resource.equals(permission.getResource()))
        .collect(Collectors.toList());
  }
}
