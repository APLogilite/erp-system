package com.erp.modules.analytics.controller;

import com.erp.common.api.ApiResponse;
import com.erp.config.ApiVersionConfig;
import com.erp.modules.analytics.dto.ReportDefinitionRequest;
import com.erp.modules.analytics.dto.ReportDefinitionResponse;
import com.erp.modules.analytics.entity.ReportDefinition;
import com.erp.modules.analytics.repository.ReportDefinitionRepository;
import com.erp.modules.analytics.service.ReportEngine;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiVersionConfig.API_V1 + "/reports")
public class ReportController {

  private final ReportEngine reportEngine;
  private final ReportDefinitionRepository reportRepository;

  public ReportController(ReportEngine reportEngine, ReportDefinitionRepository reportRepository) {
    this.reportEngine = reportEngine;
    this.reportRepository = reportRepository;
  }

  @PostMapping("/definitions")
  public ResponseEntity<ApiResponse<ReportDefinitionResponse>> createDefinition(@RequestBody ReportDefinitionRequest request) {
    ReportDefinition def = new ReportDefinition();
    def.setReportCode(request.getReportCode());
    def.setName(request.getName());
    def.setDescription(request.getDescription());
    def.setReportType(request.getReportType());
    def.setModelCode(request.getModelCode());
    def.setQueryConfig(request.getQueryConfig());
    def.setColumnConfig(request.getColumnConfig());
    def.setGroupConfig(request.getGroupConfig());
    def.setOutputFormat(request.getOutputFormat());
    def.setIsSystem(false);
    ReportDefinition saved = reportRepository.save(def);
    return ResponseEntity.ok(ApiResponse.success(toResponse(saved), "Report definition created"));
  }

  @GetMapping("/definitions")
  public ResponseEntity<ApiResponse<List<ReportDefinitionResponse>>> getDefinitions() {
    List<ReportDefinitionResponse> list = reportRepository.findAll().stream()
        .map(this::toResponse).collect(Collectors.toList());
    return ResponseEntity.ok(ApiResponse.success(list, "Report definitions retrieved"));
  }

  @GetMapping("/definitions/{id}")
  public ResponseEntity<ApiResponse<ReportDefinitionResponse>> getDefinition(@PathVariable UUID id) {
    ReportDefinition def = reportRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Report not found: " + id));
    return ResponseEntity.ok(ApiResponse.success(toResponse(def), "Report definition retrieved"));
  }

  @PostMapping("/{id}/generate")
  public ResponseEntity<ApiResponse<Map<String, Object>>> generate(
      @PathVariable UUID id, @RequestBody(required = false) Map<String, String> params) {
    Map<String, Object> result = reportEngine.generate(id, params);
    return ResponseEntity.ok(ApiResponse.success(result, "Report generated"));
  }

  @PostMapping("/{id}/export")
  public ResponseEntity<ApiResponse<Map<String, Object>>> export(
      @PathVariable UUID id, @RequestParam(defaultValue = "CSV") String format,
      @RequestBody(required = false) Map<String, String> params) {
    Map<String, Object> result = reportEngine.export(id, format, params);
    return ResponseEntity.ok(ApiResponse.success(result, "Report exported as " + format));
  }

  @PostMapping("/by-code/{code}/generate")
  public ResponseEntity<ApiResponse<Map<String, Object>>> generateByCode(
      @PathVariable String code, @RequestBody(required = false) Map<String, String> params) {
    Map<String, Object> result = reportEngine.generate(code, params);
    return ResponseEntity.ok(ApiResponse.success(result, "Report generated"));
  }

  private ReportDefinitionResponse toResponse(ReportDefinition def) {
    ReportDefinitionResponse r = new ReportDefinitionResponse();
    r.setId(def.getId());
    r.setReportCode(def.getReportCode());
    r.setName(def.getName());
    r.setDescription(def.getDescription());
    r.setReportType(def.getReportType());
    r.setModelCode(def.getModelCode());
    r.setQueryConfig(def.getQueryConfig());
    r.setColumnConfig(def.getColumnConfig());
    r.setGroupConfig(def.getGroupConfig());
    r.setOutputFormat(def.getOutputFormat());
    r.setIsSystem(def.getIsSystem());
    r.setIsActive(def.getIsActive());
    r.setCreatedAt(def.getCreatedAt());
    r.setUpdatedAt(def.getUpdatedAt());
    return r;
  }
}
