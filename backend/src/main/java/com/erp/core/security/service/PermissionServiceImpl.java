package com.erp.core.security.service;

import com.erp.core.metadata.dto.PermissionMetadataDto;
import com.erp.core.metadata.registry.MetadataRegistry;
import com.erp.core.security.dto.PermissionCheckRequestDto;
import com.erp.core.security.dto.PermissionCheckResponseDto;
import com.erp.core.security.enums.PermissionLevel;
import com.erp.core.security.evaluator.PermissionEvaluator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class PermissionServiceImpl implements PermissionService {

  private final MetadataRegistry metadataRegistry;
  private final PermissionEvaluator permissionEvaluator;

  public PermissionServiceImpl(
      MetadataRegistry metadataRegistry,
      PermissionEvaluator permissionEvaluator) {
    this.metadataRegistry = metadataRegistry;
    this.permissionEvaluator = permissionEvaluator;
  }

  @Override
  public boolean hasPermission(String userRole, String resource, String action, Map<String, Object> context) {
    List<String> permissions = getPermissions(userRole, resource);
    if (permissions.contains(action) || permissions.contains("*")) {
      return permissionEvaluator.evaluateExpressions(userRole, resource, action, context);
    }
    return false;
  }

  @Override
  public List<String> getPermissions(String userRole, String resource) {
    return permissionEvaluator.evaluateRoles(userRole, resource);
  }

  @Override
  public Map<String, Object> evaluate(String userRole, String modelCode, Map<String, Object> context) {
    Map<String, Object> evaluation = new HashMap<>();
    evaluation.put("fieldPermissions", getFieldPermissions(userRole, modelCode, null));
    evaluation.put("actionPermissions", getActionPermissions(userRole, modelCode, null));
    evaluation.put("rowPermissions", getRowPermissions(userRole, modelCode, context));
    return evaluation;
  }

  @Override
  public Map<String, Object> getFieldPermissions(String userRole, String modelCode, String fieldName) {
    Map<String, Object> permissions = new HashMap<>();
    permissions.put("hidden", false);
    permissions.put("readonly", false);
    permissions.put("editable", true);
    permissions.put("required", false);
    return permissions;
  }

  @Override
  public Map<String, Object> getActionPermissions(String userRole, String modelCode, String actionCode) {
    Map<String, Object> permissions = new HashMap<>();
    permissions.put("allowed", hasPermission(userRole, modelCode, actionCode == null ? "EXECUTE" : actionCode, null));
    return permissions;
  }

  @Override
  public Map<String, Object> getRowPermissions(String userRole, String modelCode, Map<String, Object> context) {
    Map<String, Object> permissions = new HashMap<>();
    permissions.put("visible", true);
    permissions.put("editable", true);
    return permissions;
  }

  @Override
  public PermissionCheckResponseDto checkPermission(PermissionCheckRequestDto request) {
    boolean allowed = hasPermission(request.getUserRole(), request.getResource(), request.getAction(), null);
    PermissionCheckResponseDto response = new PermissionCheckResponseDto();
    response.setAllowed(allowed);
    response.setResource(request.getResource());
    response.setAction(request.getAction());
    response.setMessage(allowed ? "Permission granted." : "Permission denied.");
    return response;
  }
}
