package com.erp.core.metadata.controller;

import com.erp.common.api.ApiResponse;
import com.erp.config.ApiVersionConfig;
import com.erp.core.metadata.dto.FormFieldRuleCreateRequest;
import com.erp.core.metadata.dto.FormFieldRuleDto;
import com.erp.core.metadata.service.FormRuleService;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiVersionConfig.API_BASE + "/metadata/forms/{formId}/fields/{fieldId}/rules")
@PreAuthorize("hasRole('SYSTEM_ADMIN')")
public class FormRuleController {
  private final FormRuleService service;
  public FormRuleController(FormRuleService service) { this.service = service; }

  @GetMapping
  public ResponseEntity<ApiResponse<List<FormFieldRuleDto>>> getRules(@PathVariable UUID fieldId) {
    return ResponseEntity.ok(ApiResponse.success(service.getRules(fieldId), "Rules retrieved."));
  }
  @PostMapping
  public ResponseEntity<ApiResponse<FormFieldRuleDto>> addRule(@PathVariable UUID fieldId, @RequestBody FormFieldRuleCreateRequest req) {
    return ResponseEntity.ok(ApiResponse.success(service.addRule(fieldId, req), "Rule added."));
  }
  @PutMapping("/{ruleId}")
  public ResponseEntity<ApiResponse<FormFieldRuleDto>> updateRule(@PathVariable UUID fieldId, @PathVariable UUID ruleId, @RequestBody FormFieldRuleCreateRequest req) {
    return ResponseEntity.ok(ApiResponse.success(service.updateRule(fieldId, ruleId, req), "Rule updated."));
  }
  @DeleteMapping("/{ruleId}")
  public ResponseEntity<ApiResponse<Void>> deleteRule(@PathVariable UUID fieldId, @PathVariable UUID ruleId) {
    service.deleteRule(fieldId, ruleId);
    return ResponseEntity.ok(ApiResponse.success(null, "Rule deleted."));
  }
}
