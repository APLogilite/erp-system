package com.erp.platform.identity.controller;

import com.erp.common.api.ApiResponse;
import com.erp.platform.identity.entity.Permission;
import com.erp.platform.identity.service.RoleAdminService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/identity/permissions")
public class PermissionAdminController {

  private final RoleAdminService roleAdminService;
  public PermissionAdminController(RoleAdminService roleAdminService) { this.roleAdminService = roleAdminService; }

  @GetMapping public ResponseEntity<ApiResponse<List<Permission>>> getAll() { return ResponseEntity.ok(ApiResponse.success(roleAdminService.getAllPermissions(), "Permissions retrieved")); }
  @GetMapping("/by-module") public ResponseEntity<ApiResponse<List<Permission>>> byModule(@RequestParam String module) { return ResponseEntity.ok(ApiResponse.success(roleAdminService.getPermissionsByModule(module), "Permissions by module")); }
  @GetMapping("/by-type") public ResponseEntity<ApiResponse<List<Permission>>> byType(@RequestParam String resourceType) { return ResponseEntity.ok(ApiResponse.success(roleAdminService.getPermissionsByResourceType(resourceType), "Permissions by type")); }
}
