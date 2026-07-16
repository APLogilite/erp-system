package com.erp.core.runtime.controller;

import com.erp.common.api.ApiResponse;
import com.erp.config.ApiVersionConfig;
import com.erp.core.runtime.service.WindowDataService;
import com.erp.platform.identity.dto.RuntimeContext;
import com.erp.platform.identity.dto.RuntimeContextHolder;
import java.util.Collections;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Runtime controller for window data CRUD operations.
 * Replaces the old /api/runtime/forms/{formCode}/records endpoints.
 *
 * <p>Endpoints:
 * <ul>
 *   <li>GET    /api/v1/runtime/windows/{windowName}/records</li>
 *   <li>GET    /api/v1/runtime/windows/{windowName}/records/{id}</li>
 *   <li>POST   /api/v1/runtime/windows/{windowName}/records</li>
 *   <li>PUT    /api/v1/runtime/windows/{windowName}/records/{id}</li>
 *   <li>DELETE /api/v1/runtime/windows/{windowName}/records/{id}</li>
 * </ul>
 */
@RestController
@RequestMapping(ApiVersionConfig.API_BASE + "/runtime/windows")
public class WindowDataController {

  private static final Logger log = LoggerFactory.getLogger(WindowDataController.class);

  private final WindowDataService windowDataService;

  public WindowDataController(WindowDataService windowDataService) {
    this.windowDataService = windowDataService;
  }

  private RuntimeContext requireContext() {
    RuntimeContext ctx = RuntimeContextHolder.get();
    if (ctx == null) {
      log.warn("No RuntimeContext available for request");
    }
    return ctx;
  }

  /**
   * List records from a window's main tab with pagination.
   */
  @GetMapping("/{windowName}/records")
  public ResponseEntity<ApiResponse<Map<String, Object>>> listRecords(
      @PathVariable String windowName,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(required = false) String sortField,
      @RequestParam(required = false, defaultValue = "asc") String sortDir) {

    RuntimeContext ctx = requireContext();
    if (ctx == null || ctx.getTenantId() == null) {
      ApiResponse<Map<String, Object>> errorResp = new ApiResponse<>(
          false, null, "Authentication required.", "UNAUTHORIZED", Collections.emptyList());
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResp);
    }

    try {
      Map<String, Object> result = windowDataService.listRecords(
          windowName, ctx.getTenantId(), page, size, sortField, sortDir);
      return ResponseEntity.ok(ApiResponse.success(result, "Records retrieved."));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(new ApiResponse<>(false, null, e.getMessage(), "NOT_FOUND", Collections.emptyList()));
    }
  }

  /**
   * Get a single record with all child tab records.
   */
  @GetMapping("/{windowName}/records/{id}")
  public ResponseEntity<ApiResponse<Map<String, Object>>> getRecord(
      @PathVariable String windowName,
      @PathVariable UUID id) {

    RuntimeContext ctx = requireContext();
    if (ctx == null || ctx.getTenantId() == null) {
      ApiResponse<Map<String, Object>> errorResp = new ApiResponse<>(
          false, null, "Authentication required.", "UNAUTHORIZED", Collections.emptyList());
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResp);
    }

    Map<String, Object> result = windowDataService.getRecordWithChildren(
        windowName, id, ctx.getTenantId());

    if (result == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(new ApiResponse<>(false, null, "Record not found.", "NOT_FOUND", Collections.emptyList()));
    }

    return ResponseEntity.ok(ApiResponse.success(result, "Record retrieved."));
  }

  /**
   * Fetches a record from a specific tab's table (drill-down support).
   * The tabId determines which tab's table to query.
   * Optional childTabs comma-separated list fetches child records.
   *
   * GET /api/v1/runtime/windows/{windowName}/tabs/{tabId}/records/{id}?childTabs=uuid1,uuid2
   */
  @GetMapping("/{windowName}/tabs/{tabId}/records/{id}")
  public ResponseEntity<ApiResponse<Map<String, Object>>> getTabRecord(
      @PathVariable String windowName,
      @PathVariable UUID tabId,
      @PathVariable UUID id,
      @RequestParam(name = "childTabs", required = false) String childTabsParam) {

    RuntimeContext ctx = requireContext();
    if (ctx == null || ctx.getTenantId() == null) {
      ApiResponse<Map<String, Object>> errorResp = new ApiResponse<>(
          false, null, "Authentication required.", "UNAUTHORIZED", Collections.emptyList());
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResp);
    }

    // Parse child tab IDs from comma-separated string
    List<UUID> childTabIds = Collections.emptyList();
    if (childTabsParam != null && !childTabsParam.isBlank()) {
      childTabIds = java.util.Arrays.stream(childTabsParam.split(","))
          .map(String::trim)
          .filter(s -> !s.isEmpty())
          .map(UUID::fromString)
          .toList();
    }

    try {
      Map<String, Object> result = windowDataService.getTabRecordWithChildren(
          windowName, tabId, id, ctx.getTenantId(), childTabIds);

      if (result == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ApiResponse<>(false, null, "Record not found.", "NOT_FOUND", Collections.emptyList()));
      }

      return ResponseEntity.ok(ApiResponse.success(result, "Tab record retrieved."));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(new ApiResponse<>(false, null, e.getMessage(), "NOT_FOUND", Collections.emptyList()));
    }
  }

  /**
   * Create a record. If tabId is provided, creates in the child tab's table
   * and auto-sets the parent FK from parentRecordId.
   */
  @PostMapping("/{windowName}/records")
  public ResponseEntity<ApiResponse<Map<String, Object>>> createRecord(
      @PathVariable String windowName,
      @RequestBody Map<String, Object> data,
      @RequestParam(name = "tabId", required = false) UUID tabId,
      @RequestParam(name = "parentRecordId", required = false) UUID parentRecordId) {

    RuntimeContext ctx = requireContext();
    if (ctx == null || ctx.getTenantId() == null) {
      ApiResponse<Map<String, Object>> errorResp = new ApiResponse<>(
          false, null, "Authentication required.", "UNAUTHORIZED", Collections.emptyList());
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResp);
    }

    try {
      Map<String, Object> record = windowDataService.createRecord(
          windowName, data, ctx.getTenantId(), ctx.getUserId(), tabId, parentRecordId);
      return ResponseEntity.ok(ApiResponse.success(record, "Record created."));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(new ApiResponse<>(false, null, e.getMessage(), "VALIDATION_ERROR", Collections.emptyList()));
    }
  }

  /**
   * Update an existing record.
   * Optionally specify tabId to update a record in a child tab's table
   * instead of the main tab's table.
   */
  @PutMapping("/{windowName}/records/{id}")
  public ResponseEntity<ApiResponse<Map<String, Object>>> updateRecord(
      @PathVariable String windowName,
      @PathVariable UUID id,
      @RequestBody Map<String, Object> data,
      @RequestParam(name = "tabId", required = false) UUID tabId) {

    RuntimeContext ctx = requireContext();
    if (ctx == null || ctx.getTenantId() == null) {
      ApiResponse<Map<String, Object>> errorResp = new ApiResponse<>(
          false, null, "Authentication required.", "UNAUTHORIZED", Collections.emptyList());
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResp);
    }

    try {
      Map<String, Object> record = windowDataService.updateRecord(
          windowName, id, data, ctx.getTenantId(), ctx.getUserId(), tabId);
      return ResponseEntity.ok(ApiResponse.success(record, "Record updated."));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(new ApiResponse<>(false, null, e.getMessage(), "VALIDATION_ERROR", Collections.emptyList()));
    }
  }

  /**
   * Lookup records from a table for dropdown/autocomplete (no pagination).
   * Returns records with id + a displayable label field.
   */
  @GetMapping("/lookup/{tableName}")
  public ResponseEntity<ApiResponse<List<Map<String, Object>>>> lookupRecords(
      @PathVariable String tableName,
      @RequestParam(name = "parentRecordId", required = false) UUID parentRecordId,
      @RequestParam(name = "tabId", required = false) UUID tabId,
      @RequestParam(name = "windowId", required = false) UUID windowId,
      @RequestParam(name = "fieldCode", required = false) String fieldCode) {

    RuntimeContext ctx = requireContext();
    if (ctx == null || ctx.getTenantId() == null) {
      ApiResponse<List<Map<String, Object>>> errorResp = new ApiResponse<>(
          false, null, "Authentication required.", "UNAUTHORIZED", Collections.emptyList());
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResp);
    }

    try {
      List<Map<String, Object>> records = windowDataService.lookupRecords(
          tableName, ctx.getTenantId(), parentRecordId, tabId, windowId, fieldCode);
      return ResponseEntity.ok(ApiResponse.success(records, "Lookup records retrieved."));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(new ApiResponse<>(false, null, e.getMessage(), "NOT_FOUND", Collections.emptyList()));
    }
  }

  /**
   * Soft-delete a record.
   */
  @DeleteMapping("/{windowName}/records/{id}")
  public ResponseEntity<ApiResponse<Void>> deleteRecord(
      @PathVariable String windowName,
      @PathVariable UUID id) {

    RuntimeContext ctx = requireContext();
    if (ctx == null || ctx.getTenantId() == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(new ApiResponse<>(false, null, "Authentication required.", "UNAUTHORIZED", Collections.emptyList()));
    }

    try {
      windowDataService.deleteRecord(windowName, id, ctx.getTenantId());
      return ResponseEntity.ok(ApiResponse.success(null, "Record deleted."));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(new ApiResponse<>(false, null, e.getMessage(), "NOT_FOUND", Collections.emptyList()));
    }
  }
}
