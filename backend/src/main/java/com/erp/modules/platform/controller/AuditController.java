package com.erp.modules.platform.controller;

import com.erp.common.api.ApiResponse;
import com.erp.config.ApiVersionConfig;
import com.erp.modules.platform.dto.AuditLogResponse;
import com.erp.modules.platform.service.AuditService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiVersionConfig.API_V1 + "/audit")
public class AuditController {

  private final AuditService auditService;

  public AuditController(AuditService auditService) {
    this.auditService = auditService;
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<AuditLogResponse>>> getAll() {
    return ResponseEntity.ok(ApiResponse.success(auditService.getAll(), "Audit logs retrieved"));
  }

  @GetMapping("/module/{module}")
  public ResponseEntity<ApiResponse<List<AuditLogResponse>>> getByModule(@PathVariable String module) {
    return ResponseEntity.ok(ApiResponse.success(auditService.getByModule(module), "Audit logs retrieved"));
  }

  @GetMapping("/by-relation")
  public ResponseEntity<ApiResponse<List<AuditLogResponse>>> getByRelation(
      @RequestParam String module, @RequestParam String recordId) {
    return ResponseEntity.ok(ApiResponse.success(
        auditService.getByModuleAndRecord(module, recordId), "Audit logs retrieved"));
  }
}
