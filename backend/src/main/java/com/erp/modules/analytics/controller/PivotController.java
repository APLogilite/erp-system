package com.erp.modules.analytics.controller;

import com.erp.common.api.ApiResponse;
import com.erp.config.ApiVersionConfig;
import com.erp.modules.analytics.service.PivotEngine;
import com.erp.modules.analytics.service.PivotEngine.PivotRequest;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiVersionConfig.API_V1 + "/pivot")
public class PivotController {

  private final PivotEngine pivotEngine;

  public PivotController(PivotEngine pivotEngine) {
    this.pivotEngine = pivotEngine;
  }

  @PostMapping("/build")
  public ResponseEntity<ApiResponse<Map<String, Object>>> build(@RequestBody PivotRequest request) {
    Map<String, Object> result = pivotEngine.build(request);
    return ResponseEntity.ok(ApiResponse.success(result, "Pivot built"));
  }
}
