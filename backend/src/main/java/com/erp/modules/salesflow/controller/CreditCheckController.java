package com.erp.modules.salesflow.controller;

import com.erp.common.api.ApiResponse;
import com.erp.config.ApiVersionConfig;
import com.erp.modules.salesflow.dto.CreditCheckResponse;
import com.erp.modules.salesflow.service.CreditCheckService;
import com.erp.platform.identity.dto.RuntimeContext;
import com.erp.platform.identity.dto.RuntimeContextHolder;
import java.util.Collections;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Credit check REST endpoint.
 *
 * POST /api/v1/orders/{id}/check-credit — evaluate customer credit for the order
 */
@RestController
@RequestMapping(ApiVersionConfig.API_BASE + "/orders")
public class CreditCheckController {

  private static final Logger log = LoggerFactory.getLogger(CreditCheckController.class);

  private final CreditCheckService service;

  public CreditCheckController(CreditCheckService service) {
    this.service = service;
  }

  @PostMapping("/{id}/check-credit")
  public ResponseEntity<ApiResponse<CreditCheckResponse>> checkCredit(@PathVariable UUID id) {
    RuntimeContext ctx = RuntimeContextHolder.get();
    if (ctx == null || ctx.getTenantId() == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(new ApiResponse<>(false, null, "Authentication required.", "UNAUTHORIZED", Collections.emptyList()));
    }
    try {
      var result = service.checkCredit(id, ctx.getTenantId());
      CreditCheckResponse resp = new CreditCheckResponse();
      resp.setApproved(result.approved);
      resp.setReason(result.reason);
      resp.setCreditLimit(result.creditLimit);
      resp.setOutstanding(result.outstanding);
      resp.setAvailable(result.available);
      resp.setOrderAmount(result.orderAmount);
      return ResponseEntity.ok(ApiResponse.success(resp, "Credit check completed."));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(new ApiResponse<>(false, null, e.getMessage(), "NOT_FOUND", Collections.emptyList()));
    }
  }
}
