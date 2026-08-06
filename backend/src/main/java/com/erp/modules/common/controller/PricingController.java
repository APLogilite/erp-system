package com.erp.modules.common.controller;

import com.erp.common.api.ApiResponse;
import com.erp.config.ApiVersionConfig;
import com.erp.modules.common.dto.PriceResolveRequest;
import com.erp.modules.common.dto.PriceResolveResponse;
import com.erp.modules.common.service.PriceResolutionService;
import com.erp.platform.identity.dto.RuntimeContext;
import com.erp.platform.identity.dto.RuntimeContextHolder;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Pricing endpoints.
 *
 * POST /api/v1/pricing/resolve — resolve the unit price for a product/customer/quantity
 */
@RestController
@RequestMapping(ApiVersionConfig.API_BASE + "/pricing")
public class PricingController {

  private static final Logger log = LoggerFactory.getLogger(PricingController.class);
  private final PriceResolutionService service;

  public PricingController(PriceResolutionService service) {
    this.service = service;
  }

  @PostMapping("/resolve")
  public ResponseEntity<ApiResponse<PriceResolveResponse>> resolve(@RequestBody PriceResolveRequest request) {
    RuntimeContext ctx = RuntimeContextHolder.get();
    if (ctx == null || ctx.getTenantId() == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(new ApiResponse<>(false, null, "Authentication required.", "UNAUTHORIZED", Collections.emptyList()));
    }

    try {
      PriceResolveResponse response = service.resolve(request);
      return ResponseEntity.ok(ApiResponse.success(response, "Price resolved."));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(new ApiResponse<>(false, null, e.getMessage(), "NOT_FOUND", Collections.emptyList()));
    }
  }
}
