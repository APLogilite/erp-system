package com.erp.core.runtime.controller;

import com.erp.common.api.ApiResponse;
import com.erp.config.ApiVersionConfig;
import com.erp.core.metadata.entity.FormTenantRoleEntity;
import com.erp.core.metadata.entity.MetadataModel;
import com.erp.core.metadata.entity.MetadataView;
import com.erp.core.metadata.repository.FormTenantRoleRepository;
import com.erp.core.metadata.repository.MetadataModelRepository;
import com.erp.core.metadata.repository.MetadataViewRepository;
import com.erp.core.runtime.dto.FormDefinitionBundleResponse;
import com.erp.core.runtime.service.FormDefinitionAssemblyService;
import com.erp.core.runtime.service.RecordCrudService;
import com.erp.platform.identity.dto.RuntimeContext;
import com.erp.platform.identity.dto.RuntimeContextHolder;
import com.erp.platform.identity.entity.Role;
import com.erp.platform.identity.repository.RoleRepository;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

@RestController
@RequestMapping(ApiVersionConfig.API_BASE + "/runtime/forms")
public class RuntimeFormController {

  private static final Logger log = LoggerFactory.getLogger(RuntimeFormController.class);

  private final FormDefinitionAssemblyService assemblyService;
  private final RecordCrudService recordCrudService;
  private final MetadataViewRepository metadataViewRepository;
  private final MetadataModelRepository metadataModelRepository;
  private final FormTenantRoleRepository formTenantRoleRepository;
  private final RoleRepository roleRepository;

  public RuntimeFormController(
      FormDefinitionAssemblyService assemblyService,
      RecordCrudService recordCrudService,
      MetadataViewRepository metadataViewRepository,
      MetadataModelRepository metadataModelRepository,
      FormTenantRoleRepository formTenantRoleRepository,
      RoleRepository roleRepository) {
    this.assemblyService = assemblyService;
    this.recordCrudService = recordCrudService;
    this.metadataViewRepository = metadataViewRepository;
    this.metadataModelRepository = metadataModelRepository;
    this.formTenantRoleRepository = formTenantRoleRepository;
    this.roleRepository = roleRepository;
  }

  /**
   * Ensures the controller has a valid RuntimeContext.
   * Returns null if no authenticated context is present (unauthenticated request).
   */
  private RuntimeContext requireContext() {
    RuntimeContext ctx = RuntimeContextHolder.get();
    if (ctx == null) {
      log.warn("No RuntimeContext available for request");
    }
    return ctx;
  }

  // ----
  // List accessible forms
  // ----

  /**
   * Returns all forms the current user has role-based access to.
   * Filters by tenant (global forms with tenant role assignments + tenant-scoped forms).
   */
  @GetMapping
  public ResponseEntity<ApiResponse<List<Map<String, Object>>>> listAccessibleForms() {
    RuntimeContext ctx = requireContext();
    if (ctx == null || ctx.getTenantId() == null) {
      ApiResponse<List<Map<String, Object>>> errorResp = new ApiResponse<>(
          false, null, "Authentication required.", "UNAUTHORIZED", Collections.emptyList());
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResp);
    }

    UUID tenantId = ctx.getTenantId();
    List<String> roleCodes = ctx.getRoles() != null ? ctx.getRoles() : List.of();

    // System admin sees all active forms
    boolean isSystemAdmin = roleCodes.contains("sys_admin");

    // Convert role codes to UUIDs
    List<Role> userRoles = roleRepository.findByCodeIn(roleCodes);
    Set<UUID> userRoleIds = userRoles.stream().map(Role::getId).collect(Collectors.toSet());

    // Load all active form-type views
    List<MetadataView> allForms = metadataViewRepository.findAll().stream()
        .filter(v -> "form".equals(v.getType()))
        .filter(v -> Boolean.TRUE.equals(v.getIsActive()))
        .toList();

    List<Map<String, Object>> accessibleForms = new java.util.ArrayList<>();

    for (MetadataView view : allForms) {
      boolean hasAccess = false;

      if (isSystemAdmin) {
        hasAccess = true;
      } else if ("global".equals(view.getScope())) {
        // Global form: check if user's tenant has assigned any of user's roles
        List<FormTenantRoleEntity> tenantRoles = formTenantRoleRepository
            .findByFormIdAndTenantId(view.getId(), tenantId);
        hasAccess = tenantRoles.stream()
            .anyMatch(tr -> userRoleIds.contains(tr.getRoleId()));
      } else {
        // Tenant form: must belong to user's tenant AND have role assignment
        if (tenantId.equals(view.getTenantId())) {
          List<FormTenantRoleEntity> tenantRoles = formTenantRoleRepository
              .findByFormIdAndTenantId(view.getId(), tenantId);
          hasAccess = tenantRoles.isEmpty() || tenantRoles.stream()
              .anyMatch(tr -> userRoleIds.contains(tr.getRoleId()));
        }
      }

      if (hasAccess) {
        Map<String, Object> entry = new LinkedHashMap<>();
        entry.put("formId", view.getId().toString());
        entry.put("formCode", view.getName());
        entry.put("formLabel", view.getDefinition() != null
            && view.getDefinition().containsKey("label")
            ? view.getDefinition().get("label")
            : view.getName());
        entry.put("modelName", view.getModelName());
        entry.put("scope", view.getScope());
        entry.put("description", view.getDescription());

        // Resolve model label
        metadataModelRepository.findByName(view.getModelName()).ifPresent(model -> {
          entry.put("modelLabel", model.getLabel());
          entry.put("tableName", model.getTableName());
        });

        accessibleForms.add(entry);
      }
    }

    return ResponseEntity.ok(ApiResponse.success(accessibleForms,
        "Accessible forms retrieved. Total: " + accessibleForms.size()));
  }

  // ----
  // Definition
  // ----

  @GetMapping("/{formCode}/definition")
  public ResponseEntity<ApiResponse<FormDefinitionBundleResponse>> getFormDefinition(
      @PathVariable String formCode,
      @RequestHeader(value = "If-None-Match", required = false) String ifNoneMatch,
      WebRequest request) {

    RuntimeContext ctx = requireContext();
    UUID tenantId = ctx != null ? ctx.getTenantId() : null;
    List<String> roleCodes = ctx != null ? ctx.getRoles() : List.of();

    String etag = "\"" + formCode + "-" + (tenantId != null ? tenantId : "anon") + "\"";

    if (ifNoneMatch != null && ifNoneMatch.equals(etag)) {
      return ResponseEntity.status(HttpStatus.NOT_MODIFIED)
          .eTag(etag)
          .build();
    }

    FormDefinitionBundleResponse bundle = assemblyService.assembleDefinition(
        formCode, tenantId, roleCodes);

    if (bundle == null) {
      return ResponseEntity.notFound().build();
    }

    return ResponseEntity.ok()
        .eTag(etag)
        .header("Cache-Control", "max-age=300")
        .body(ApiResponse.success(bundle, "Form definition retrieved."));
  }

  // ----
  // Records
  // ----

  @GetMapping("/{formCode}/records")
  public ResponseEntity<ApiResponse<Map<String, Object>>> listRecords(
      @PathVariable String formCode,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(required = false) String sortField,
      @RequestParam(required = false, defaultValue = "asc") String sortDir) {

    RuntimeContext ctx = requireContext();
    UUID tenantId = ctx != null ? ctx.getTenantId() : null;
    List<String> roleCodes = ctx != null ? ctx.getRoles() : List.of();
    UUID userId = ctx != null ? ctx.getUserId() : null;

    Map<String, Object> result = recordCrudService.listRecords(
        formCode, tenantId, roleCodes, userId, page, size, sortField, sortDir);
    return ResponseEntity.ok(ApiResponse.success(result, "Records retrieved."));
  }

  @GetMapping("/{formCode}/records/{id}")
  public ResponseEntity<ApiResponse<Map<String, Object>>> getRecord(
      @PathVariable String formCode,
      @PathVariable UUID id) {

    RuntimeContext ctx = requireContext();
    UUID tenantId = ctx != null ? ctx.getTenantId() : null;
    List<String> roleCodes = ctx != null ? ctx.getRoles() : List.of();
    UUID userId = ctx != null ? ctx.getUserId() : null;

    Map<String, Object> result = recordCrudService.getRecordWithContext(
        formCode, id, tenantId, roleCodes, userId);
    if (result == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(ApiResponse.success(result, "Record retrieved."));
  }

  @PostMapping("/{formCode}/records")
  public ResponseEntity<ApiResponse<Map<String, Object>>> createRecord(
      @PathVariable String formCode,
      @RequestBody Map<String, Object> data) {

    RuntimeContext ctx = requireContext();
    UUID tenantId = ctx != null ? ctx.getTenantId() : null;
    UUID userId = ctx != null ? ctx.getUserId() : null;
    List<String> roleCodes = ctx != null ? ctx.getRoles() : List.of();

    Map<String, Object> record = recordCrudService.createRecord(
        formCode, data, tenantId, userId, roleCodes);
    return ResponseEntity.ok(ApiResponse.success(record, "Record created."));
  }

  @PutMapping("/{formCode}/records/{id}")
  public ResponseEntity<ApiResponse<Map<String, Object>>> updateRecord(
      @PathVariable String formCode,
      @PathVariable UUID id,
      @RequestBody Map<String, Object> data) {

    RuntimeContext ctx = requireContext();
    UUID tenantId = ctx != null ? ctx.getTenantId() : null;
    UUID userId = ctx != null ? ctx.getUserId() : null;
    List<String> roleCodes = ctx != null ? ctx.getRoles() : List.of();

    Map<String, Object> record = recordCrudService.updateRecord(
        formCode, id, data, tenantId, userId, roleCodes);
    return ResponseEntity.ok(ApiResponse.success(record, "Record updated."));
  }

  @DeleteMapping("/{formCode}/records/{id}")
  public ResponseEntity<ApiResponse<Void>> deleteRecord(
      @PathVariable String formCode,
      @PathVariable UUID id) {

    RuntimeContext ctx = requireContext();
    UUID tenantId = ctx != null ? ctx.getTenantId() : null;

    recordCrudService.deleteRecord(formCode, id, tenantId);
    return ResponseEntity.ok(ApiResponse.success(null, "Record deleted."));
  }
}
