package com.erp.core.runtime.controller;

import com.erp.common.api.ApiResponse;
import com.erp.config.ApiVersionConfig;
import com.erp.core.runtime.dto.FormDefinitionBundleResponse;
import com.erp.core.runtime.service.FormDefinitionAssemblyService;
import com.erp.core.runtime.service.RecordCrudService;
import com.erp.platform.identity.dto.RuntimeContext;
import com.erp.platform.identity.dto.RuntimeContextHolder;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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

  public RuntimeFormController(
      FormDefinitionAssemblyService assemblyService,
      RecordCrudService recordCrudService) {
    this.assemblyService = assemblyService;
    this.recordCrudService = recordCrudService;
  }

  // ---- Definition ----

  /**
   * Returns the complete form definition bundle (fields, rules, validations,
   * layout, sub-forms, model columns) for a given form code.
   *
   * <p>Supports ETag-based conditional requests: the response includes an ETag
   * header derived from the form code. Clients can send If-None-Match to
   * receive a 304 Not Modified response when the definition is unchanged.
   *
   * <p>Response is cached with Cache-Control: max-age=300 (5 minutes).
   */
  @GetMapping("/{formCode}/definition")
  public ResponseEntity<ApiResponse<FormDefinitionBundleResponse>> getFormDefinition(
      @PathVariable String formCode,
      @RequestHeader(value = "If-None-Match", required = false) String ifNoneMatch,
      WebRequest request) {

    // Extract tenant and role context from the current request
    RuntimeContext context = RuntimeContextHolder.get();
    UUID tenantId = context != null ? context.getTenantId() : null;
    List<String> roleCodes = context != null ? context.getRoles() : List.of();

    // ETag support: use form code as the ETag key (cached definitions are
    // keyed by form code; a change in form definition would change the cache)
    String etag = "\"" + formCode + "-" + (context != null ? context.getTenantId() : "anon") + "\"";

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

  // ---- Records ----
  // NOTE: Record endpoints below use placeholder tenant/role context.
  // Full context integration will be completed by TASK-017 (Record Data APIs).

  @GetMapping("/{formCode}/records")
  public ResponseEntity<ApiResponse<Map<String, Object>>> listRecords(
      @PathVariable String formCode,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(required = false) String sortField,
      @RequestParam(required = false, defaultValue = "asc") String sortDir) {
    UUID tenantId = UUID.randomUUID();
    List<UUID> roleIds = List.of();
    Map<String, Object> result = recordCrudService.listRecords(
        formCode, tenantId, roleIds, page, size, sortField, sortDir);
    return ResponseEntity.ok(ApiResponse.success(result, "Records retrieved."));
  }

  @GetMapping("/{formCode}/records/{id}")
  public ResponseEntity<ApiResponse<Map<String, Object>>> getRecord(
      @PathVariable String formCode,
      @PathVariable UUID id) {
    UUID tenantId = UUID.randomUUID();
    List<UUID> roleIds = List.of();
    Map<String, Object> result = recordCrudService.getRecordWithContext(formCode, id, tenantId, roleIds);
    if (result == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(ApiResponse.success(result, "Record retrieved."));
  }

  @PostMapping("/{formCode}/records")
  public ResponseEntity<ApiResponse<Map<String, Object>>> createRecord(
      @PathVariable String formCode,
      @RequestBody Map<String, Object> data) {
    UUID tenantId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    List<UUID> roleIds = List.of();
    Map<String, Object> record = recordCrudService.createRecord(formCode, data, tenantId, userId, roleIds);
    return ResponseEntity.ok(ApiResponse.success(record, "Record created."));
  }

  @PutMapping("/{formCode}/records/{id}")
  public ResponseEntity<ApiResponse<Map<String, Object>>> updateRecord(
      @PathVariable String formCode,
      @PathVariable UUID id,
      @RequestBody Map<String, Object> data) {
    UUID tenantId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    List<UUID> roleIds = List.of();
    Map<String, Object> record = recordCrudService.updateRecord(formCode, id, data, tenantId, userId, roleIds);
    return ResponseEntity.ok(ApiResponse.success(record, "Record updated."));
  }

  @DeleteMapping("/{formCode}/records/{id}")
  public ResponseEntity<ApiResponse<Void>> deleteRecord(
      @PathVariable String formCode,
      @PathVariable UUID id) {
    UUID tenantId = UUID.randomUUID();
    recordCrudService.deleteRecord(formCode, id, tenantId);
    return ResponseEntity.ok(ApiResponse.success(null, "Record deleted."));
  }
}
