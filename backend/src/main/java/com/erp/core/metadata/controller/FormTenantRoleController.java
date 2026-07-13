package com.erp.core.metadata.controller;

import com.erp.common.api.ApiResponse;
import com.erp.config.ApiVersionConfig;
import com.erp.core.metadata.dto.GlobalFormDto;
import com.erp.core.metadata.dto.TenantRoleRequest;
import com.erp.core.metadata.dto.TenantRoleResponse;
import com.erp.core.metadata.service.FormTenantRoleService;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiVersionConfig.API_BASE + "/metadata/forms")
public class FormTenantRoleController {
  private final FormTenantRoleService service;

  public FormTenantRoleController(FormTenantRoleService service) {
    this.service = service;
  }

  /** Tenant Admin: view role assignments for their tenant on a form */
  @GetMapping("/{formId}/tenant-roles")
  @PreAuthorize("hasAnyAuthority('sys_admin','tnt_admin')")
  public ResponseEntity<ApiResponse<TenantRoleResponse>> getRoles(@PathVariable UUID formId) {
    UUID tenantId = service.getCurrentTenantId();
    return ResponseEntity.ok(
        ApiResponse.success(service.getRoles(formId, tenantId), "Roles retrieved."));
  }

  /** Tenant Admin: replace all role assignments for their tenant */
  @PutMapping("/{formId}/tenant-roles")
  @PreAuthorize("hasAnyAuthority('sys_admin','tnt_admin')")
  public ResponseEntity<ApiResponse<TenantRoleResponse>> setRoles(
      @PathVariable UUID formId, @RequestBody TenantRoleRequest req) {
    UUID tenantId = service.getCurrentTenantId();
    return ResponseEntity.ok(
        ApiResponse.success(service.setRoles(formId, tenantId, req), "Roles updated."));
  }

  /** System Admin: view all tenant role assignments across all tenants */
  @GetMapping("/{formId}/global-tenant-roles")
  @PreAuthorize("hasAuthority('sys_admin')")
  public ResponseEntity<ApiResponse<List<TenantRoleResponse>>> getGlobalTenantRoles(
      @PathVariable UUID formId) {
    return ResponseEntity.ok(
        ApiResponse.success(service.getGlobalTenantRoles(formId), "Global roles retrieved."));
  }

  /** Tenant Admin: list global forms available to their tenant */
  @GetMapping("/global")
  @PreAuthorize("hasAnyAuthority('sys_admin','tnt_admin')")
  public ResponseEntity<ApiResponse<List<GlobalFormDto>>> getGlobalForms() {
    UUID tenantId = service.getCurrentTenantId();
    return ResponseEntity.ok(
        ApiResponse.success(service.getGlobalForms(tenantId), "Global forms retrieved."));
  }
}
