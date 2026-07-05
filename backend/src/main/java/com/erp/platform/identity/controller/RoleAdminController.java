package com.erp.platform.identity.controller;

import com.erp.common.api.ApiResponse;
import com.erp.platform.identity.entity.Permission;
import com.erp.platform.identity.entity.Role;
import com.erp.platform.identity.entity.RolePermission;
import com.erp.platform.identity.service.RoleAdminService;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/identity/roles")
public class RoleAdminController {

  private final RoleAdminService roleAdminService;
  public RoleAdminController(RoleAdminService roleAdminService) { this.roleAdminService = roleAdminService; }

  @GetMapping public ResponseEntity<ApiResponse<List<Role>>> getAll() { return ResponseEntity.ok(ApiResponse.success(roleAdminService.getAllRoles(), "Roles retrieved")); }
  @GetMapping("/{id}") public ResponseEntity<ApiResponse<Role>> getById(@PathVariable UUID id) { return ResponseEntity.ok(ApiResponse.success(roleAdminService.getRole(id), "Role retrieved")); }
  @PostMapping public ResponseEntity<ApiResponse<Role>> create(@RequestBody Role r) { return ResponseEntity.ok(ApiResponse.success(roleAdminService.createRole(r), "Role created")); }
  @PutMapping("/{id}") public ResponseEntity<ApiResponse<Role>> update(@PathVariable UUID id, @RequestBody Role r) { return ResponseEntity.ok(ApiResponse.success(roleAdminService.updateRole(id, r), "Role updated")); }
  @DeleteMapping("/{id}") public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) { roleAdminService.deleteRole(id); return ResponseEntity.ok(ApiResponse.successMessage("Role deleted")); }
  @PostMapping("/clone") public ResponseEntity<ApiResponse<Role>> clone(@RequestBody Map<String, String> body) { return ResponseEntity.ok(ApiResponse.success(roleAdminService.cloneRole(UUID.fromString(body.get("sourceId")), body.get("newCode"), body.get("newName")), "Role cloned")); }
  @GetMapping("/{id}/permissions") public ResponseEntity<ApiResponse<List<RolePermission>>> getPermissions(@PathVariable UUID id) { return ResponseEntity.ok(ApiResponse.success(roleAdminService.getRolePermissions(id), "Role permissions")); }
  @PostMapping("/{id}/permissions") public ResponseEntity<ApiResponse<Void>> assignPermission(@PathVariable UUID id, @RequestBody Map<String, UUID> body) { roleAdminService.assignPermission(id, body.get("permissionId")); return ResponseEntity.ok(ApiResponse.successMessage("Permission assigned")); }
  @DeleteMapping("/{id}/permissions/{permissionId}") public ResponseEntity<ApiResponse<Void>> removePermission(@PathVariable UUID id, @PathVariable UUID permissionId) { roleAdminService.removePermission(id, permissionId); return ResponseEntity.ok(ApiResponse.successMessage("Permission removed")); }
}
