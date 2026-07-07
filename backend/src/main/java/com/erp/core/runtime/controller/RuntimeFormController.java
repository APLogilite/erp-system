package com.erp.core.runtime.controller;

import com.erp.common.api.ApiResponse;
import com.erp.config.ApiVersionConfig;
import com.erp.core.runtime.dto.FormDefinitionBundleResponse;
import com.erp.core.runtime.service.FormDefinitionAssemblyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Runtime controller for dynamic form operations.
 * Handles form definition retrieval for the frontend renderer.
 */
@RestController
@RequestMapping(ApiVersionConfig.API_BASE + "/runtime/forms")
public class RuntimeFormController {

  private final FormDefinitionAssemblyService assemblyService;

  public RuntimeFormController(FormDefinitionAssemblyService assemblyService) {
    this.assemblyService = assemblyService;
  }

  /**
   * GET /api/runtime/forms/{formCode}/definition
   *
   * Returns the complete form definition bundle including fields with types,
   * rules, validations, layout sections, sub-forms, and model columns.
   * Response is cacheable (5 minutes). Frontend should use conditional
   * requests with ETag for efficiency.
   */
  @GetMapping("/{formCode}/definition")
  public ResponseEntity<ApiResponse<FormDefinitionBundleResponse>> getFormDefinition(
      @PathVariable String formCode) {

    FormDefinitionBundleResponse bundle = assemblyService.assembleDefinition(formCode);

    if (bundle == null) {
      return ResponseEntity.notFound().build();
    }

    return ResponseEntity.ok()
        .header("Cache-Control", "max-age=300")
        .body(ApiResponse.success(bundle, "Form definition retrieved."));
  }
}
