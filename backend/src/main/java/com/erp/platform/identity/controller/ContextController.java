package com.erp.platform.identity.controller;

import com.erp.common.api.ApiResponse;
import com.erp.platform.identity.dto.ContextOptionsResponse;
import com.erp.platform.identity.dto.ContextSwitchRequest;
import com.erp.platform.identity.dto.RuntimeContext;
import com.erp.platform.identity.security.JwtPrincipal;
import com.erp.platform.identity.service.RuntimeContextService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/context")
public class ContextController {

  private final RuntimeContextService runtimeContextService;

  public ContextController(RuntimeContextService runtimeContextService) {
    this.runtimeContextService = runtimeContextService;
  }

  @GetMapping("/current")
  public ResponseEntity<ApiResponse<RuntimeContext>> current(
      @AuthenticationPrincipal JwtPrincipal principal) {
    RuntimeContext ctx = runtimeContextService.resolve(principal.getUserId());
    return ResponseEntity.ok(ApiResponse.success(ctx, "Current context"));
  }

  @GetMapping("/options")
  public ResponseEntity<ApiResponse<ContextOptionsResponse>> options(
      @AuthenticationPrincipal JwtPrincipal principal) {
    ContextOptionsResponse opts = runtimeContextService.getAvailableOptions(principal.getUserId());
    return ResponseEntity.ok(ApiResponse.success(opts, "Available context options"));
  }

  @PostMapping("/switch")
  public ResponseEntity<ApiResponse<RuntimeContext>> switchContext(
      @RequestBody ContextSwitchRequest request,
      @AuthenticationPrincipal JwtPrincipal principal) {
    RuntimeContext ctx = runtimeContextService.switchContext(principal.getUserId(), request);
    return ResponseEntity.ok(ApiResponse.success(ctx, "Context switched"));
  }
}
