package com.erp.core.security.controller;

import com.erp.common.api.ApiResponse;
import com.erp.config.ApiVersionConfig;
import com.erp.core.security.dto.PermissionCheckRequestDto;
import com.erp.core.security.dto.PermissionCheckResponseDto;
import com.erp.core.security.service.PermissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiVersionConfig.API_BASE + "/security")
public class PermissionController {

  private final PermissionService permissionService;

  public PermissionController(PermissionService permissionService) {
    this.permissionService = permissionService;
  }

  @PostMapping("/check")
  public ResponseEntity<ApiResponse<PermissionCheckResponseDto>> checkPermission(
      @RequestBody PermissionCheckRequestDto request) {
    PermissionCheckResponseDto response = permissionService.checkPermission(request);
    return ResponseEntity.ok(ApiResponse.success(response, response.getMessage()));
  }

  @GetMapping("/model/{modelCode}/fields")
  public ResponseEntity<ApiResponse<Object>> getFieldPermissions(
      @PathVariable String modelCode,
      String userRole) {
    Object response = permissionService.getFieldPermissions(userRole, modelCode, null);
    return ResponseEntity.ok(ApiResponse.success(response, "Field permissions retrieved."));
  }
}
