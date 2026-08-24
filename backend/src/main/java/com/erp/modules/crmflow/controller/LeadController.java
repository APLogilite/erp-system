package com.erp.modules.crmflow.controller;

import com.erp.common.api.ApiResponse;
import com.erp.config.ApiVersionConfig;
import com.erp.modules.crmflow.dto.LeadConvertResponse;
import com.erp.modules.crmflow.dto.LeadTransitionRequest;
import com.erp.modules.crmflow.dto.LeadTransitionResponse;
import com.erp.modules.crmflow.service.LeadService;
import com.erp.platform.identity.dto.RuntimeContext;
import com.erp.platform.identity.dto.RuntimeContextHolder;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Lead workflow and conversion endpoints.
 */
@RestController
@RequestMapping(ApiVersionConfig.API_BASE + "/leads")
public class LeadController {

  private static final Logger log = LoggerFactory.getLogger(LeadController.class);

  private final LeadService leadService;

  public LeadController(LeadService leadService) {
    this.leadService = leadService;
  }

  @PostMapping("/{id}/transition")
  public ResponseEntity<ApiResponse<LeadTransitionResponse>> transition(
      @PathVariable UUID id,
      @RequestBody LeadTransitionRequest request) {
    RuntimeContext ctx = RuntimeContextHolder.get();
    if (ctx == null || ctx.getTenantId() == null) {
      return unauthorized();
    }
    try {
      String newStatus = leadService.transition(id, request.getTargetStatus(), ctx.getTenantId());
      return ResponseEntity.ok(ApiResponse.success(
          new LeadTransitionResponse(true, newStatus, "Lead transitioned to " + newStatus),
          "Lead transitioned successfully."));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(new ApiResponse<>(false, null, e.getMessage(), "NOT_FOUND", Collections.emptyList()));
    } catch (IllegalStateException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(new ApiResponse<>(false, null, e.getMessage(), "BAD_REQUEST", Collections.emptyList()));
    }
  }

  @PostMapping("/{id}/convert")
  public ResponseEntity<ApiResponse<LeadConvertResponse>> convert(@PathVariable UUID id) {
    RuntimeContext ctx = RuntimeContextHolder.get();
    if (ctx == null || ctx.getTenantId() == null) {
      return unauthorizedConvert();
    }
    try {
      Map<String, String> result = leadService.convertToOpportunity(id, ctx.getTenantId());
      return ResponseEntity.ok(ApiResponse.success(
          new LeadConvertResponse(
              result.get("partnerId"),
              result.get("opportunityId"),
              result.get("opportunityNumber")),
          "Lead converted to opportunity successfully."));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(new ApiResponse<>(false, null, e.getMessage(), "NOT_FOUND", Collections.emptyList()));
    } catch (IllegalStateException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(new ApiResponse<>(false, null, e.getMessage(), "BAD_REQUEST", Collections.emptyList()));
    }
  }

  private ResponseEntity<ApiResponse<LeadTransitionResponse>> unauthorized() {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(new ApiResponse<>(false, null, "Authentication required.", "UNAUTHORIZED", Collections.emptyList()));
  }

  private ResponseEntity<ApiResponse<LeadConvertResponse>> unauthorizedConvert() {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(new ApiResponse<>(false, null, "Authentication required.", "UNAUTHORIZED", Collections.emptyList()));
  }
}
