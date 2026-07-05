package com.erp.modules.analytics.controller;

import com.erp.common.api.ApiResponse;
import com.erp.config.ApiVersionConfig;
import com.erp.modules.analytics.service.DrillDownEngine;
import com.erp.modules.analytics.service.DrillDownEngine.DrillRequest;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiVersionConfig.API_V1 + "/drill-down")
public class DrillDownController {

  private final DrillDownEngine drillDownEngine;

  public DrillDownController(DrillDownEngine drillDownEngine) {
    this.drillDownEngine = drillDownEngine;
  }

  @PostMapping("/drill")
  public ResponseEntity<ApiResponse<Map<String, Object>>> drill(@RequestBody DrillRequest request) {
    Map<String, Object> result = drillDownEngine.drill(request);
    return ResponseEntity.ok(ApiResponse.success(result, "Drill-down executed"));
  }
}
