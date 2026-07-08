package com.erp.core.metadata.controller;

import com.erp.common.api.ApiResponse;
import com.erp.config.ApiVersionConfig;
import com.erp.core.metadata.dto.FieldReorderRequest;
import com.erp.core.metadata.dto.FormCloneRequest;
import com.erp.core.metadata.dto.FormCreateRequest;
import com.erp.core.metadata.dto.FormDesignDto;
import com.erp.core.metadata.dto.FormFieldCreateRequest;
import com.erp.core.metadata.dto.FormFieldDto;
import com.erp.core.metadata.dto.FormLayoutSectionCreateRequest;
import com.erp.core.metadata.dto.FormLayoutSectionDto;
import com.erp.core.metadata.dto.FormSectionFieldDto;
import com.erp.core.metadata.dto.FormUpdateRequest;
import com.erp.core.metadata.dto.SectionFieldAssignmentRequest;
import com.erp.core.metadata.service.FormDesignerService;
import com.erp.core.metadata.service.FormFieldService;
import com.erp.core.metadata.service.FormLayoutService;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST API for the Form Designer.
 * Handles CRUD operations on form definitions, fields, and layout.
 */
@RestController
@RequestMapping(ApiVersionConfig.API_BASE + "/metadata/forms")
@PreAuthorize("hasAnyRole('SYSTEM_ADMIN','TENANT_ADMIN')")
public class FormDesignerController {

  private final FormDesignerService formDesignerService;
  private final FormFieldService formFieldService;
  private final FormLayoutService formLayoutService;

  public FormDesignerController(
      FormDesignerService formDesignerService,
      FormFieldService formFieldService,
      FormLayoutService formLayoutService) {
    this.formDesignerService = formDesignerService;
    this.formFieldService = formFieldService;
    this.formLayoutService = formLayoutService;
  }

  // ---------------------------------------------------------------
  // Form CRUD
  // ---------------------------------------------------------------

  /**
   * GET /api/metadata/forms — List all forms.
   */
  @GetMapping
  public ResponseEntity<ApiResponse<List<FormDesignDto>>> listForms(
      @RequestParam(required = false) String scope,
      @RequestParam(required = false) UUID tenantId) {
    List<FormDesignDto> forms = formDesignerService.listForms(scope, tenantId);
    return ResponseEntity.ok(ApiResponse.success(forms, "Forms retrieved."));
  }

  /**
   * GET /api/metadata/forms/{id} — Get form with all configuration.
   */
  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<FormDesignDto>> getForm(@PathVariable UUID id) {
    FormDesignDto form = formDesignerService.getForm(id);
    return ResponseEntity.ok(ApiResponse.success(form, "Form retrieved."));
  }

  /**
   * POST /api/metadata/forms — Create a new form definition.
   */
  @PostMapping
  public ResponseEntity<ApiResponse<FormDesignDto>> createForm(@RequestBody FormCreateRequest request) {
    FormDesignDto form = formDesignerService.createForm(request);
    return ResponseEntity.ok(ApiResponse.success(form, "Form created."));
  }

  /**
   * PUT /api/metadata/forms/{id} — Update form header.
   */
  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<FormDesignDto>> updateForm(
      @PathVariable UUID id,
      @RequestBody FormUpdateRequest request) {
    FormDesignDto form = formDesignerService.updateForm(id, request);
    return ResponseEntity.ok(ApiResponse.success(form, "Form updated."));
  }

  /**
   * DELETE /api/metadata/forms/{id} — Delete form with cascaded data.
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> deleteForm(@PathVariable UUID id) {
    formDesignerService.deleteForm(id);
    return ResponseEntity.ok(ApiResponse.success(null, "Form deleted."));
  }

  /**
   * POST /api/metadata/forms/{id}/clone — Clone a form definition.
   */
  @PostMapping("/{id}/clone")
  public ResponseEntity<ApiResponse<FormDesignDto>> cloneForm(
      @PathVariable UUID id,
      @RequestBody FormCloneRequest request) {
    FormDesignDto cloned = formDesignerService.cloneForm(id, request.getName(), request.getLabel());
    return ResponseEntity.ok(ApiResponse.success(cloned, "Form cloned."));
  }

  /**
   * GET /api/metadata/forms/available-tables — Get tables available for form creation.
   */
  @GetMapping("/available-tables")
  public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAvailableTables() {
    List<Map<String, Object>> tables = formDesignerService.getAvailableTables();
    return ResponseEntity.ok(ApiResponse.success(tables, "Available tables retrieved."));
  }

  // ---------------------------------------------------------------
  // Field CRUD
  // ---------------------------------------------------------------

  /**
   * GET /api/metadata/forms/{formId}/fields — Get all fields for a form.
   */
  @GetMapping("/{formId}/fields")
  public ResponseEntity<ApiResponse<List<FormFieldDto>>> getFields(@PathVariable UUID formId) {
    List<FormFieldDto> fields = formFieldService.getFields(formId);
    return ResponseEntity.ok(ApiResponse.success(fields, "Fields retrieved."));
  }

  /**
   * POST /api/metadata/forms/{formId}/fields — Add a field to a form.
   */
  @PostMapping("/{formId}/fields")
  public ResponseEntity<ApiResponse<FormFieldDto>> addField(
      @PathVariable UUID formId,
      @RequestBody FormFieldCreateRequest request) {
    FormFieldDto field = formFieldService.addField(formId, request);
    return ResponseEntity.ok(ApiResponse.success(field, "Field added."));
  }

  /**
   * PUT /api/metadata/forms/{formId}/fields/{fieldId} — Update a field.
   */
  @PutMapping("/{formId}/fields/{fieldId}")
  public ResponseEntity<ApiResponse<FormFieldDto>> updateField(
      @PathVariable UUID formId,
      @PathVariable UUID fieldId,
      @RequestBody FormFieldCreateRequest request) {
    FormFieldDto field = formFieldService.updateField(formId, fieldId, request);
    return ResponseEntity.ok(ApiResponse.success(field, "Field updated."));
  }

  /**
   * DELETE /api/metadata/forms/{formId}/fields/{fieldId} — Remove a field.
   */
  @DeleteMapping("/{formId}/fields/{fieldId}")
  public ResponseEntity<ApiResponse<Void>> deleteField(
      @PathVariable UUID formId,
      @PathVariable UUID fieldId) {
    formFieldService.deleteField(formId, fieldId);
    return ResponseEntity.ok(ApiResponse.success(null, "Field deleted."));
  }

  /**
   * PUT /api/metadata/forms/{formId}/fields/reorder — Reorder fields.
   */
  @PutMapping("/{formId}/fields/reorder")
  public ResponseEntity<ApiResponse<List<FormFieldDto>>> reorderFields(
      @PathVariable UUID formId,
      @RequestBody FieldReorderRequest request) {
    List<FormFieldDto> fields = formFieldService.reorderFields(formId, request.getFieldIds());
    return ResponseEntity.ok(ApiResponse.success(fields, "Fields reordered."));
  }

  // ---------------------------------------------------------------
  // Layout CRUD
  // ---------------------------------------------------------------

  /**
   * GET /api/metadata/forms/{formId}/layout — Get layout sections.
   */
  @GetMapping("/{formId}/layout")
  public ResponseEntity<ApiResponse<List<FormLayoutSectionDto>>> getLayout(@PathVariable UUID formId) {
    List<FormLayoutSectionDto> sections = formLayoutService.getSections(formId);
    return ResponseEntity.ok(ApiResponse.success(sections, "Layout sections retrieved."));
  }

  /**
   * POST /api/metadata/forms/{formId}/layout/sections — Add a section.
   */
  @PostMapping("/{formId}/layout/sections")
  public ResponseEntity<ApiResponse<FormLayoutSectionDto>> addSection(
      @PathVariable UUID formId,
      @RequestBody FormLayoutSectionCreateRequest request) {
    FormLayoutSectionDto section = formLayoutService.addSection(formId, request);
    return ResponseEntity.ok(ApiResponse.success(section, "Section added."));
  }

  /**
   * PUT /api/metadata/forms/{formId}/layout/sections/{sectionId} — Update section.
   */
  @PutMapping("/{formId}/layout/sections/{sectionId}")
  public ResponseEntity<ApiResponse<FormLayoutSectionDto>> updateSection(
      @PathVariable UUID formId,
      @PathVariable UUID sectionId,
      @RequestBody FormLayoutSectionCreateRequest request) {
    FormLayoutSectionDto section = formLayoutService.updateSection(formId, sectionId, request);
    return ResponseEntity.ok(ApiResponse.success(section, "Section updated."));
  }

  /**
   * DELETE /api/metadata/forms/{formId}/layout/sections/{sectionId} — Remove section.
   */
  @DeleteMapping("/{formId}/layout/sections/{sectionId}")
  public ResponseEntity<ApiResponse<Void>> deleteSection(
      @PathVariable UUID formId,
      @PathVariable UUID sectionId) {
    formLayoutService.deleteSection(formId, sectionId);
    return ResponseEntity.ok(ApiResponse.success(null, "Section deleted."));
  }

  /**
   * PUT /api/metadata/forms/{formId}/layout/sections/{sectionId}/fields — Assign fields to section.
   */
  @PutMapping("/{formId}/layout/sections/{sectionId}/fields")
  public ResponseEntity<ApiResponse<List<FormSectionFieldDto>>> assignFieldsToSection(
      @PathVariable UUID formId,
      @PathVariable UUID sectionId,
      @RequestBody SectionFieldAssignmentRequest request) {
    List<FormSectionFieldDto> assignments = formLayoutService.assignFieldsToSection(
        formId, sectionId, request.getFieldIds());
    return ResponseEntity.ok(ApiResponse.success(assignments, "Fields assigned to section."));
  }
}
