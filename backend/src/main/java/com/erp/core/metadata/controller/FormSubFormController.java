package com.erp.core.metadata.controller;

import com.erp.common.api.ApiResponse;
import com.erp.config.ApiVersionConfig;
import com.erp.core.metadata.dto.AvailableRelationDto;
import com.erp.core.metadata.dto.FormSubFormCreateRequest;
import com.erp.core.metadata.dto.FormSubFormDto;
import com.erp.core.metadata.dto.SubFormReorderRequest;
import com.erp.core.metadata.service.FormSubFormService;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiVersionConfig.API_BASE + "/metadata/forms/{formId}/subforms")
@PreAuthorize("hasAuthority('sys_admin')")
public class FormSubFormController {
  private final FormSubFormService service;
  public FormSubFormController(FormSubFormService service) { this.service = service; }

  @GetMapping
  public ResponseEntity<ApiResponse<List<FormSubFormDto>>> getSubForms(@PathVariable UUID formId) {
    return ResponseEntity.ok(ApiResponse.success(service.getSubForms(formId), "Sub-forms retrieved."));
  }
  @PostMapping
  public ResponseEntity<ApiResponse<FormSubFormDto>> addSubForm(@PathVariable UUID formId, @RequestBody FormSubFormCreateRequest req) {
    return ResponseEntity.ok(ApiResponse.success(service.addSubForm(formId, req), "Sub-form added."));
  }
  @PutMapping("/{subFormId}")
  public ResponseEntity<ApiResponse<FormSubFormDto>> updateSubForm(@PathVariable UUID formId, @PathVariable UUID subFormId, @RequestBody FormSubFormCreateRequest req) {
    return ResponseEntity.ok(ApiResponse.success(service.updateSubForm(formId, subFormId, req), "Sub-form updated."));
  }
  @DeleteMapping("/{subFormId}")
  public ResponseEntity<ApiResponse<Void>> deleteSubForm(@PathVariable UUID formId, @PathVariable UUID subFormId) {
    service.deleteSubForm(formId, subFormId);
    return ResponseEntity.ok(ApiResponse.success(null, "Sub-form deleted."));
  }
  @PutMapping("/reorder")
  public ResponseEntity<ApiResponse<Void>> reorderSubForms(@PathVariable UUID formId, @RequestBody SubFormReorderRequest req) {
    service.reorderSubForms(formId, req);
    return ResponseEntity.ok(ApiResponse.success(null, "Sub-forms reordered."));
  }
  @GetMapping("/available-relations")
  public ResponseEntity<ApiResponse<List<AvailableRelationDto>>> getAvailableRelations(@PathVariable UUID formId) {
    return ResponseEntity.ok(ApiResponse.success(
        service.getAvailableRelations(formId), "Available relations retrieved."));
  }
}
