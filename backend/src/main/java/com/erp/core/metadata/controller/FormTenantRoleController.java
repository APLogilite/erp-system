package com.erp.core.metadata.controller;

import com.erp.common.api.ApiResponse;
import com.erp.config.ApiVersionConfig;
import com.erp.core.metadata.dto.FormTenantRoleCreateRequest;
import com.erp.core.metadata.dto.FormTenantRoleDto;
import com.erp.core.metadata.service.FormTenantRoleService;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiVersionConfig.API_BASE + "/metadata/forms/{formId}/tenant-roles")
public class FormTenantRoleController {
  private final FormTenantRoleService service;
  public FormTenantRoleController(FormTenantRoleService service) { this.service = service; }

  @GetMapping
  public ResponseEntity<ApiResponse<List<FormTenantRoleDto>>> getRoles(@PathVariable UUID formId, @RequestParam UUID tenantId) {
    return ResponseEntity.ok(ApiResponse.success(service.getRoles(formId, tenantId), "Roles retrieved."));
  }
  @PostMapping
  public ResponseEntity<ApiResponse<FormTenantRoleDto>> assignRole(@RequestBody FormTenantRoleCreateRequest req) {
    return ResponseEntity.ok(ApiResponse.success(service.assignRole(req), "Role assigned."));
  }
  @DeleteMapping("/{roleId}")
  public ResponseEntity<ApiResponse<Void>> removeRole(@PathVariable UUID formId, @RequestParam UUID tenantId, @PathVariable UUID roleId) {
    service.removeRole(formId, tenantId, roleId);
    return ResponseEntity.ok(ApiResponse.success(null, "Role removed."));
  }
}
