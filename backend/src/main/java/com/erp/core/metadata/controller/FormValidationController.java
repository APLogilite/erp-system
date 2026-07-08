package com.erp.core.metadata.controller;

import com.erp.common.api.ApiResponse;
import com.erp.config.ApiVersionConfig;
import com.erp.core.metadata.dto.FormFieldValidationCreateRequest;
import com.erp.core.metadata.dto.FormFieldValidationDto;
import com.erp.core.metadata.service.FormValidationService;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiVersionConfig.API_BASE + "/metadata/forms/{formId}/fields/{fieldId}/validations")
public class FormValidationController {
  private final FormValidationService service;
  public FormValidationController(FormValidationService service) { this.service = service; }

  @GetMapping
  public ResponseEntity<ApiResponse<List<FormFieldValidationDto>>> getValidations(@PathVariable UUID fieldId) {
    return ResponseEntity.ok(ApiResponse.success(service.getValidations(fieldId), "Validations retrieved."));
  }
  @PostMapping
  public ResponseEntity<ApiResponse<FormFieldValidationDto>> addValidation(@PathVariable UUID fieldId, @RequestBody FormFieldValidationCreateRequest req) {
    return ResponseEntity.ok(ApiResponse.success(service.addValidation(fieldId, req), "Validation added."));
  }
  @PutMapping("/{valId}")
  public ResponseEntity<ApiResponse<FormFieldValidationDto>> updateValidation(@PathVariable UUID fieldId, @PathVariable UUID valId, @RequestBody FormFieldValidationCreateRequest req) {
    return ResponseEntity.ok(ApiResponse.success(service.updateValidation(fieldId, valId, req), "Validation updated."));
  }
  @DeleteMapping("/{valId}")
  public ResponseEntity<ApiResponse<Void>> deleteValidation(@PathVariable UUID fieldId, @PathVariable UUID valId) {
    service.deleteValidation(fieldId, valId);
    return ResponseEntity.ok(ApiResponse.success(null, "Validation deleted."));
  }
}
