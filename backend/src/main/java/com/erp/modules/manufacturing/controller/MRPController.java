package com.erp.modules.manufacturing.controller;

import com.erp.common.api.ApiResponse;
import com.erp.config.ApiVersionConfig;
import com.erp.modules.manufacturing.service.MRPService;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiVersionConfig.API_V1 + "/mrp")
public class MRPController {

  private final MRPService mrpService;

  public MRPController(MRPService mrpService) {
    this.mrpService = mrpService;
  }

  @GetMapping("/run")
  public ResponseEntity<ApiResponse<Map<String, Object>>> runMRP() {
    Map<String, Object> result = mrpService.runMRP();
    return ResponseEntity.ok(ApiResponse.success(result, "MRP run completed"));
  }
}
