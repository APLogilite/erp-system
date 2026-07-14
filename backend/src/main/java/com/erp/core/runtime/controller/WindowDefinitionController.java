package com.erp.core.runtime.controller;

import com.erp.common.api.ApiResponse;
import com.erp.config.ApiVersionConfig;
import com.erp.core.runtime.dto.window.WindowDefinitionResponse;
import com.erp.core.runtime.service.WindowDefinitionAssemblyService;
import com.erp.platform.identity.dto.RuntimeContext;
import com.erp.platform.identity.dto.RuntimeContextHolder;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

/**
 * Runtime controller for window definitions.
 * Replaces the old /api/runtime/forms/{formCode}/definition endpoint.
 *
 * GET /api/v1/runtime/windows/{windowName}/definition
 */
@RestController
@RequestMapping(ApiVersionConfig.API_BASE + "/runtime/windows")
public class WindowDefinitionController {

  private static final Logger log = LoggerFactory.getLogger(WindowDefinitionController.class);

  private final WindowDefinitionAssemblyService assemblyService;

  public WindowDefinitionController(WindowDefinitionAssemblyService assemblyService) {
    this.assemblyService = assemblyService;
  }

  /**
   * Returns the full window definition bundle for the given window name.
   * The response is cacheable (ETag + Cache-Control).
   *
   * @param windowName the window name (e.g. "sales_order")
   * @param request    the web request (for ETag support)
   * @return 200 with window bundle, 404 if not found, 401 if unauthenticated
   */
  @GetMapping("/{windowName}/definition")
  public ResponseEntity<ApiResponse<WindowDefinitionResponse>> getWindowDefinition(
      @PathVariable String windowName,
      WebRequest request) {

    RuntimeContext ctx = RuntimeContextHolder.get();
    if (ctx == null || ctx.getTenantId() == null) {
      ApiResponse<WindowDefinitionResponse> errorResp = new ApiResponse<>(
          false, null, "Authentication required.", "UNAUTHORIZED", Collections.emptyList());
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResp);
    }

    // Simple ETag based on window name + tenant
    String etag = "\"" + windowName + "-" + ctx.getTenantId() + "\"";

    // Check If-None-Match
    String ifNoneMatch = request.getHeader("If-None-Match");
    if (ifNoneMatch != null && ifNoneMatch.equals(etag)) {
      return ResponseEntity.status(HttpStatus.NOT_MODIFIED)
          .eTag(etag)
          .build();
    }

    WindowDefinitionResponse bundle = assemblyService.assembleDefinition(windowName);

    if (bundle == null) {
      ApiResponse<WindowDefinitionResponse> notFoundResp = new ApiResponse<>(
          false, null, "Window not found: " + windowName, "NOT_FOUND", Collections.emptyList());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFoundResp);
    }

    return ResponseEntity.ok()
        .eTag(etag)
        .header("Cache-Control", "max-age=300")
        .body(ApiResponse.success(bundle, "Window definition loaded."));
  }
}
