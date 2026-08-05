package com.erp.modules.common.controller;

import com.erp.common.api.ApiResponse;
import com.erp.config.ApiVersionConfig;
import com.erp.modules.common.dto.DocumentNumberResponse;
import com.erp.modules.common.service.DocumentSequenceService;
import com.erp.platform.identity.dto.RuntimeContext;
import com.erp.platform.identity.dto.RuntimeContextHolder;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Document Sequence REST endpoints.
 *
 * POST /api/v1/sequences/{name}/next  — get and increment the next number
 * GET  /api/v1/sequences/{name}/peek  — preview the next number without incrementing
 */
@RestController
@RequestMapping(ApiVersionConfig.API_BASE + "/sequences")
public class DocumentSequenceController {

  private static final Logger log = LoggerFactory.getLogger(DocumentSequenceController.class);

  private final DocumentSequenceService service;

  public DocumentSequenceController(DocumentSequenceService service) {
    this.service = service;
  }

  @PostMapping("/{name}/next")
  public ResponseEntity<ApiResponse<DocumentNumberResponse>> getNext(@PathVariable String name) {
    RuntimeContext ctx = requireContext();
    if (ctx == null) {
      return authError();
    }

    try {
      String number = service.getNextNumber(name, ctx.getTenantId());
      return ResponseEntity.ok(ApiResponse.success(new DocumentNumberResponse(number), "Next number assigned."));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(new ApiResponse<>(false, null, e.getMessage(), "NOT_FOUND", Collections.emptyList()));
    }
  }

  @GetMapping("/{name}/peek")
  public ResponseEntity<ApiResponse<DocumentNumberResponse>> peekNext(@PathVariable String name) {
    RuntimeContext ctx = requireContext();
    if (ctx == null) {
      return authError();
    }

    try {
      String number = service.peekNextNumber(name, ctx.getTenantId());
      DocumentNumberResponse resp = new DocumentNumberResponse();
      resp.setNextNumber(number);
      return ResponseEntity.ok(ApiResponse.success(resp, "Peeked next number."));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(new ApiResponse<>(false, null, e.getMessage(), "NOT_FOUND", Collections.emptyList()));
    }
  }

  private RuntimeContext requireContext() {
    return RuntimeContextHolder.get();
  }

  private ResponseEntity<ApiResponse<DocumentNumberResponse>> authError() {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(new ApiResponse<>(false, null, "Authentication required.", "UNAUTHORIZED", Collections.emptyList()));
  }
}
