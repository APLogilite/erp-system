package com.erp.modules.purchase.controller;

import com.erp.common.api.ApiResponse;
import com.erp.config.ApiVersionConfig;
import com.erp.modules.purchase.dto.PriceResolutionRequest;
import com.erp.modules.purchase.dto.PriceResolutionResponse;
import com.erp.modules.purchase.service.PurchasePriceResolutionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiVersionConfig.API_V1 + "/purchase/price-lists")
public class PurchasePriceListController {

  private final PurchasePriceResolutionService resolutionService;

  public PurchasePriceListController(PurchasePriceResolutionService resolutionService) {
    this.resolutionService = resolutionService;
  }

  @PostMapping("/resolve")
  public ResponseEntity<ApiResponse<PriceResolutionResponse>> resolve(
      @RequestBody PriceResolutionRequest request) {
    PriceResolutionResponse result = resolutionService.resolve(request);
    return ResponseEntity.ok(ApiResponse.success(result, "Price resolved"));
  }
}
