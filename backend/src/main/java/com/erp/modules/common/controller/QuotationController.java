package com.erp.modules.common.controller;

import com.erp.common.api.ApiResponse;
import com.erp.config.ApiVersionConfig;
import com.erp.modules.common.dto.LineCalcRequest;
import com.erp.modules.common.dto.QuotationTotalsResponse;
import com.erp.modules.common.service.QuotationService;
import com.erp.platform.identity.dto.RuntimeContext;
import com.erp.platform.identity.dto.RuntimeContextHolder;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiVersionConfig.API_BASE + "/quotations")
public class QuotationController {

  private final QuotationService service;

  public QuotationController(QuotationService service) { this.service = service; }

  @PostMapping("/{id}/calculate")
  public ResponseEntity<ApiResponse<QuotationTotalsResponse>> calculate(@PathVariable UUID id) {
    if (!checkAuth())
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(new ApiResponse<>(false, null, "Authentication required.", "UNAUTHORIZED", Collections.emptyList()));
    try { return ResponseEntity.ok(ApiResponse.success(service.calculateTotals(id), "Totals calculated.")); }
    catch (Exception e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(new ApiResponse<>(false, null, e.getMessage(), "NOT_FOUND", Collections.emptyList()));
    }
  }

  @PostMapping("/calculate-line")
  public ResponseEntity<ApiResponse<?>> calculateLine(@RequestBody LineCalcRequest req) {
    if (!checkAuth()) return unauthorized();
    return ResponseEntity.ok(ApiResponse.success(service.calculateLine(req), "Line calculated."));
  }

  @PostMapping("/{id}/convert")
  public ResponseEntity<ApiResponse<Map<String, String>>> convert(@PathVariable UUID id) {
    if (!checkAuth())
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(new ApiResponse<>(false, null, "Authentication required.", "UNAUTHORIZED", Collections.emptyList()));
    try {
      UUID orderId = service.convertToOrder(id, RuntimeContextHolder.get().getTenantId());
      return ResponseEntity.ok(ApiResponse.success(Map.of("orderId", orderId.toString()), "Quotation converted."));
    } catch (Exception e) {
      return ResponseEntity.status(e instanceof IllegalStateException ? HttpStatus.CONFLICT : HttpStatus.NOT_FOUND)
          .body(new ApiResponse<>(false, null, e.getMessage(),
              e instanceof IllegalStateException ? "CONFLICT" : "NOT_FOUND", Collections.emptyList()));
    }
  }

  private boolean checkAuth() {
    RuntimeContext ctx = RuntimeContextHolder.get();
    return ctx != null && ctx.getTenantId() != null;
  }

  private ResponseEntity<ApiResponse<?>> unauthorized() {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(new ApiResponse<>(false, null, "Authentication required.", "UNAUTHORIZED", Collections.emptyList()));
  }
}
