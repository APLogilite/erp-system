package com.erp.platform.identity.controller;

import com.erp.common.api.ApiResponse;
import com.erp.platform.identity.entity.Tenant;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/identity/tenants")
public class TenantAdminController {

  private final AdminService adminService;
  public TenantAdminController(AdminService adminService) { this.adminService = adminService; }

  @GetMapping public ResponseEntity<ApiResponse<List<Tenant>>> getAll() { return ResponseEntity.ok(ApiResponse.success(adminService.getAllTenants(), "Tenants retrieved")); }
  @GetMapping("/{id}") public ResponseEntity<ApiResponse<Tenant>> getById(@PathVariable UUID id) { return ResponseEntity.ok(ApiResponse.success(adminService.getTenant(id), "Tenant retrieved")); }
  @PostMapping public ResponseEntity<ApiResponse<Tenant>> create(@RequestBody Tenant t) { return ResponseEntity.ok(ApiResponse.success(adminService.createTenant(t), "Tenant created")); }
  @PutMapping("/{id}") public ResponseEntity<ApiResponse<Tenant>> update(@PathVariable UUID id, @RequestBody Tenant t) { return ResponseEntity.ok(ApiResponse.success(adminService.updateTenant(id, t), "Tenant updated")); }
  @DeleteMapping("/{id}") public ResponseEntity<ApiResponse<Void>> deactivate(@PathVariable UUID id) { adminService.deactivateTenant(id); return ResponseEntity.ok(ApiResponse.successMessage("Tenant deactivated")); }
}
