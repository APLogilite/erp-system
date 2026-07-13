package com.erp.core.metadata.controller;

import com.erp.common.api.ApiResponse;
import com.erp.config.ApiVersionConfig;
import com.erp.core.metadata.dto.ExpressionEvaluateRequest;
import com.erp.core.metadata.dto.ExpressionResultResponse;
import com.erp.core.metadata.dto.ExpressionValidateRequest;
import com.erp.core.metadata.service.ExpressionValidationService;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiVersionConfig.API_BASE + "/metadata/expressions")
@PreAuthorize("hasAuthority('sys_admin')")
public class ExpressionController {

  private final ExpressionValidationService expressionService;

  public ExpressionController(ExpressionValidationService expressionService) {
    this.expressionService = expressionService;
  }

  @PostMapping("/validate")
  public ResponseEntity<ApiResponse<ExpressionResultResponse>> validateExpression(
      @RequestBody ExpressionValidateRequest request) {
    ExpressionResultResponse result = expressionService.validateExpression(
        request.getExpression());
    return ResponseEntity.ok(ApiResponse.success(result, result.getMessage()));
  }

  @PostMapping("/validate-action")
  public ResponseEntity<ApiResponse<ExpressionResultResponse>> validateAction(
      @RequestBody ExpressionValidateRequest request) {
    ExpressionResultResponse result = expressionService.validateAction(
        request.getExpression());
    return ResponseEntity.ok(ApiResponse.success(result, result.getMessage()));
  }

  @PostMapping("/validate-pattern")
  public ResponseEntity<ApiResponse<ExpressionResultResponse>> validatePattern(
      @RequestBody ExpressionValidateRequest request) {
    ExpressionResultResponse result = expressionService.validatePattern(
        request.getExpression());
    return ResponseEntity.ok(ApiResponse.success(result, result.getMessage()));
  }

  @PostMapping("/evaluate")
  public ResponseEntity<ApiResponse<ExpressionResultResponse>> evaluateExpression(
      @RequestBody ExpressionEvaluateRequest request) {
    Map<String, Object> sampleData = request.getSampleData();
    ExpressionResultResponse result = expressionService.evaluateExpression(
        request.getExpression(), sampleData);
    return ResponseEntity.ok(ApiResponse.success(result, result.getMessage()));
  }
}
