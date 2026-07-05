package com.erp.modules.analytics.controller;

import com.erp.common.api.ApiResponse;
import com.erp.config.ApiVersionConfig;
import com.erp.modules.analytics.service.QueryBuilderService;
import com.erp.modules.analytics.service.QueryBuilderService.QueryRequest;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiVersionConfig.API_V1 + "/query")
public class QueryController {

  private final QueryBuilderService queryBuilderService;

  public QueryController(QueryBuilderService queryBuilderService) {
    this.queryBuilderService = queryBuilderService;
  }

  @PostMapping("/execute")
  public ResponseEntity<ApiResponse<Map<String, Object>>> execute(@RequestBody QueryRequest request) {
    Map<String, Object> result = queryBuilderService.execute(request);
    return ResponseEntity.ok(ApiResponse.success(result, "Query executed"));
  }
}
