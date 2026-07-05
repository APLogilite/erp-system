package com.erp.platform.identity.controller;

import com.erp.common.api.ApiResponse;
import com.erp.platform.identity.entity.Organization;
import com.erp.platform.identity.service.AdminService;
import java.util.List;
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
@RequestMapping("/api/v1/identity/organizations")
public class OrganizationAdminController {

  private final AdminService adminService;
  public OrganizationAdminController(AdminService adminService) { this.adminService = adminService; }

  @GetMapping public ResponseEntity<ApiResponse<List<Organization>>> getAll() { return ResponseEntity.ok(ApiResponse.success(adminService.getAllOrganizations(), "Organizations retrieved")); }
  @GetMapping("/{id}") public ResponseEntity<ApiResponse<Organization>> getById(@PathVariable UUID id) { return ResponseEntity.ok(ApiResponse.success(adminService.getOrganization(id), "Organization retrieved")); }
  @PostMapping public ResponseEntity<ApiResponse<Organization>> create(@RequestBody Organization o) { return ResponseEntity.ok(ApiResponse.success(adminService.createOrganization(o), "Organization created")); }
  @PutMapping("/{id}") public ResponseEntity<ApiResponse<Organization>> update(@PathVariable UUID id, @RequestBody Organization o) { return ResponseEntity.ok(ApiResponse.success(adminService.updateOrganization(id, o), "Organization updated")); }
  @DeleteMapping("/{id}") public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) { adminService.deleteOrganization(id); return ResponseEntity.ok(ApiResponse.successMessage("Organization deleted")); }
  @GetMapping("/tree") public ResponseEntity<ApiResponse<List<Organization>>> tree(@RequestParam UUID tenantId) { return ResponseEntity.ok(ApiResponse.success(adminService.getOrganizationTree(tenantId), "Organization tree")); }
}
