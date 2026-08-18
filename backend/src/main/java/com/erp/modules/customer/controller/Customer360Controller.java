package com.erp.modules.customer.controller;

import com.erp.common.api.ApiResponse;
import com.erp.config.ApiVersionConfig;
import com.erp.modules.customer.dto.Customer360Response;
import com.erp.modules.customer.service.Customer360Service;
import com.erp.platform.identity.dto.RuntimeContext;
import com.erp.platform.identity.dto.RuntimeContextHolder;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Customer 360 dashboard REST endpoint.
 *
 * GET /api/v1/customers/{partnerId}/360
 */
@RestController
@RequestMapping(ApiVersionConfig.API_BASE + "/customers")
public class Customer360Controller {

  private static final Logger log = LoggerFactory.getLogger(Customer360Controller.class);

  private final Customer360Service service;

  public Customer360Controller(Customer360Service service) {
    this.service = service;
  }

  @GetMapping("/{partnerId}/360")
  public ResponseEntity<ApiResponse<Customer360Response>> getCustomer360(@PathVariable UUID partnerId) {
    RuntimeContext ctx = RuntimeContextHolder.get();
    if (ctx == null || ctx.getTenantId() == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(new ApiResponse<>(false, null, "Authentication required.", "UNAUTHORIZED", Collections.emptyList()));
    }

    Map<String, Object> partner = service.getPartnerProfile(partnerId, ctx.getTenantId());
    if (partner == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(new ApiResponse<>(false, null, "Partner not found: " + partnerId, "NOT_FOUND", Collections.emptyList()));
    }

    BigDecimal outstanding = service.getOutstanding(partnerId, ctx.getTenantId());

    Customer360Response resp = new Customer360Response();
    resp.setPartner(partner);
    resp.setOutstanding(outstanding);
    resp.setRecentOrders(service.getRecentOrders(partnerId, ctx.getTenantId()));
    resp.setRecentInvoices(service.getRecentInvoices(partnerId, ctx.getTenantId()));
    resp.setRecentPayments(service.getRecentPayments(partnerId, ctx.getTenantId()));
    resp.setOpenQuotations(service.getOpenQuotations(partnerId, ctx.getTenantId()));

    return ResponseEntity.ok(ApiResponse.success(resp, "Customer 360 retrieved."));
  }
}
