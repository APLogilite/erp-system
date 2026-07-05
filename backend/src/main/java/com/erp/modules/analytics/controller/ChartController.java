package com.erp.modules.analytics.controller;

import com.erp.common.api.ApiResponse;
import com.erp.config.ApiVersionConfig;
import com.erp.modules.analytics.service.ChartEngine;
import com.erp.modules.analytics.service.ChartEngine.ChartRequest;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiVersionConfig.API_V1 + "/charts")
public class ChartController {

  private final ChartEngine chartEngine;

  public ChartController(ChartEngine chartEngine) {
    this.chartEngine = chartEngine;
  }

  @PostMapping("/generate")
  public ResponseEntity<ApiResponse<Map<String, Object>>> generate(@RequestBody ChartRequest request) {
    Map<String, Object> result = chartEngine.generate(request);
    return ResponseEntity.ok(ApiResponse.success(result, "Chart generated"));
  }
}
