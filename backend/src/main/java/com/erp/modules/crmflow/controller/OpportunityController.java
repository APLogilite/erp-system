package com.erp.modules.crmflow.controller;

import com.erp.common.api.ApiResponse;
import com.erp.config.ApiVersionConfig;
import com.erp.modules.crmflow.dto.CreateQuotationResponse;
import com.erp.modules.crmflow.dto.OpportunityTransitionRequest;
import com.erp.modules.crmflow.dto.OpportunityTransitionResponse;
import com.erp.modules.crmflow.service.OpportunityService;
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
 * Opportunity pipeline workflow and quotation conversion endpoints.
 */
@RestController
@RequestMapping(ApiVersionConfig.API_BASE + "/opportunities")
public class OpportunityController {

  private static final Logger log = LoggerFactory.getLogger(OpportunityController.class);

  private final OpportunityService opportunityService;

  public OpportunityController(OpportunityService opportunityService) {
    this.opportunityService = opportunityService;
  }

  @PostMapping("/{id}/transition")
  public ResponseEntity<ApiResponse<OpportunityTransitionResponse>> transition(
      @PathVariable UUID id,
      @RequestBody OpportunityTransitionRequest request) {
    RuntimeContext ctx = RuntimeContextHolder.get();
    if (ctx == null || ctx.getTenantId() == null) {
      return unauthorized();
    }
    try {
      String newStage = opportunityService.transition(id, request.getTargetStage(), ctx.getTenantId());
      return ResponseEntity.ok(ApiResponse.success(
          new OpportunityTransitionResponse(true, newStage, "Opportunity moved to " + newStage),
          "Opportunity transitioned successfully."));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(new ApiResponse<>(false, null, e.getMessage(), "NOT_FOUND", Collections.emptyList()));
    } catch (IllegalStateException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(new ApiResponse<>(false, null, e.getMessage(), "BAD_REQUEST", Collections.emptyList()));
    }
  }

  @PostMapping("/{id}/create-quotation")
  public ResponseEntity<ApiResponse<CreateQuotationResponse>> createQuotation(@PathVariable UUID id) {
    RuntimeContext ctx = RuntimeContextHolder.get();
    if (ctx == null || ctx.getTenantId() == null) {
      return unauthorizedQuotation();
    }
    try {
      Map<String, String> result = opportunityService.createQuotation(id, ctx.getTenantId());
      return ResponseEntity.ok(ApiResponse.success(
          new CreateQuotationResponse(result.get("quotationId"), result.get("quoteNumber")),
          "Quotation created from opportunity."));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(new ApiResponse<>(false, null, e.getMessage(), "NOT_FOUND", Collections.emptyList()));
    } catch (IllegalStateException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(new ApiResponse<>(false, null, e.getMessage(), "BAD_REQUEST", Collections.emptyList()));
    }
  }

  private ResponseEntity<ApiResponse<OpportunityTransitionResponse>> unauthorized() {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(new ApiResponse<>(false, null, "Authentication required.", "UNAUTHORIZED", Collections.emptyList()));
  }

  private ResponseEntity<ApiResponse<CreateQuotationResponse>> unauthorizedQuotation() {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(new ApiResponse<>(false, null, "Authentication required.", "UNAUTHORIZED", Collections.emptyList()));
  }
}
