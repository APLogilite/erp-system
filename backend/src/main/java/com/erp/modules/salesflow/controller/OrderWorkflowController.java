package com.erp.modules.salesflow.controller;

import com.erp.common.api.ApiResponse;
import com.erp.config.ApiVersionConfig;
import com.erp.modules.salesflow.dto.TransitionRequest;
import com.erp.modules.salesflow.dto.TransitionResponse;
import com.erp.modules.salesflow.service.OrderWorkflowService;
import com.erp.platform.identity.dto.RuntimeContext;
import com.erp.platform.identity.dto.RuntimeContextHolder;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Order workflow REST endpoints.
 *
 * POST /api/v1/orders/{id}/transition      — transition order to a new status
 * GET  /api/v1/orders/{id}/allowed-transitions — list valid next statuses
 */
@RestController
@RequestMapping(ApiVersionConfig.API_BASE + "/orders")
public class OrderWorkflowController {

  private static final Logger log = LoggerFactory.getLogger(OrderWorkflowController.class);

  private final OrderWorkflowService service;

  public OrderWorkflowController(OrderWorkflowService service) {
    this.service = service;
  }

  @PostMapping("/{id}/transition")
  public ResponseEntity<ApiResponse<TransitionResponse>> transition(
      @PathVariable UUID id,
      @RequestBody TransitionRequest request) {
    RuntimeContext ctx = RuntimeContextHolder.get();
    if (ctx == null || ctx.getTenantId() == null) {
      return unauthorized();
    }
    try {
      String newStatus = service.transition(
          id, request.getTargetStatus(), ctx.getUserId(), ctx.getTenantId(),
          request.isOverride(), request.getOverrideReason(), ctx.getRoles());
      return ResponseEntity.ok(ApiResponse.success(
          new TransitionResponse(true, newStatus, "Transition successful."),
          "Order transitioned to " + newStatus));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(new ApiResponse<>(false, null, e.getMessage(), "NOT_FOUND", Collections.emptyList()));
    } catch (IllegalStateException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(new ApiResponse<>(false, null, e.getMessage(), "BAD_REQUEST", Collections.emptyList()));
    }
  }

  @GetMapping("/{id}/allowed-transitions")
  public ResponseEntity<ApiResponse<Map<String, Object>>> allowedTransitions(@PathVariable UUID id) {
    RuntimeContext ctx = RuntimeContextHolder.get();
    if (ctx == null || ctx.getTenantId() == null) {
      return unauthorizedMap();
    }
    var transitions = service.getAllowedTransitions(id, ctx.getTenantId());
    return ResponseEntity.ok(ApiResponse.success(
        Map.of("status", "ok", "allowedTransitions", transitions),
        "Allowed transitions retrieved."));
  }

  private ResponseEntity<ApiResponse<TransitionResponse>> unauthorized() {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(new ApiResponse<>(false, null, "Authentication required.", "UNAUTHORIZED", Collections.emptyList()));
  }

  private ResponseEntity<ApiResponse<Map<String, Object>>> unauthorizedMap() {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(new ApiResponse<>(false, null, "Authentication required.", "UNAUTHORIZED", Collections.emptyList()));
  }
}
