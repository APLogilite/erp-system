package com.erp.modules.salesflow.controller;

import com.erp.common.api.ApiResponse;
import com.erp.config.ApiVersionConfig;
import com.erp.modules.salesflow.dto.OrderTotalsResponse;
import com.erp.modules.salesflow.service.OrderCalculationService;
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
 * Order calculation REST endpoint.
 *
 * POST /api/v1/orders/{id}/calculate — recalculate header totals from lines
 */
@RestController
@RequestMapping(ApiVersionConfig.API_BASE + "/orders")
public class OrderCalculationController {

  private static final Logger log = LoggerFactory.getLogger(OrderCalculationController.class);

  private final OrderCalculationService service;

  public OrderCalculationController(OrderCalculationService service) {
    this.service = service;
  }

  @PostMapping("/{id}/calculate")
  public ResponseEntity<ApiResponse<OrderTotalsResponse>> calculate(@PathVariable UUID id) {
    RuntimeContext ctx = RuntimeContextHolder.get();
    if (ctx == null || ctx.getTenantId() == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(new ApiResponse<>(false, null, "Authentication required.", "UNAUTHORIZED", Collections.emptyList()));
    }
    try {
      var totals = service.calculateAndPersist(id, ctx.getTenantId());
      OrderTotalsResponse resp = new OrderTotalsResponse();
      resp.setSubtotal(totals.subtotal());
      resp.setDiscountAmount(totals.discountAmount());
      resp.setTaxAmount(totals.taxAmount());
      resp.setGrandTotal(totals.grandTotal());
      return ResponseEntity.ok(ApiResponse.success(resp, "Totals calculated."));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(new ApiResponse<>(false, null, e.getMessage(), "NOT_FOUND", Collections.emptyList()));
    }
  }
}
