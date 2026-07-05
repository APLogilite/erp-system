package com.erp.core.runtime.controller;

import com.erp.common.api.ApiResponse;
import com.erp.config.ApiVersionConfig;
import com.erp.core.runtime.dto.RuntimeActionRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(ApiVersionConfig.API_BASE + "/runtime")
public class RuntimeController {

  @PostMapping("/action/execute")
  public ResponseEntity<ApiResponse<Map<String, Object>>> executeAction(
      @RequestBody RuntimeActionRequest request) {

    Map<String, Object> response = new HashMap<>();
    response.put("model", request.getModel());
    response.put("actionCode", request.getActionCode());
    response.put("status", "accepted");
    response.put("message", "Runtime action endpoint is available. Runtime execution engine will process the action.");

    return ResponseEntity.ok(ApiResponse.success(response, "Runtime action request accepted."));
  }
}
