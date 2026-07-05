package com.erp.core.security.service;

import com.erp.core.security.dto.PermissionCheckRequestDto;
import com.erp.core.security.dto.PermissionCheckResponseDto;
import com.erp.core.security.enums.PermissionLevel;
import java.util.List;
import java.util.Map;

public interface PermissionService {

  boolean hasPermission(String userRole, String resource, String action, Map<String, Object> context);

  List<String> getPermissions(String userRole, String resource);

  Map<String, Object> evaluate(String userRole, String modelCode, Map<String, Object> context);

  Map<String, Object> getFieldPermissions(String userRole, String modelCode, String fieldName);

  Map<String, Object> getActionPermissions(String userRole, String modelCode, String actionCode);

  Map<String, Object> getRowPermissions(String userRole, String modelCode, Map<String, Object> context);

  PermissionCheckResponseDto checkPermission(PermissionCheckRequestDto request);
}
