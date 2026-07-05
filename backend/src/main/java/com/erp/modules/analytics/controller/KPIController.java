package com.erp.modules.analytics.controller;

import com.erp.common.api.ApiResponse;
import com.erp.config.ApiVersionConfig;
import com.erp.modules.analytics.dto.KPIResponse;
import com.erp.modules.analytics.entity.KPIDefinition;
import com.erp.modules.analytics.repository.KPIDefinitionRepository;
import com.erp.modules.analytics.service.KPIEngine;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiVersionConfig.API_V1 + "/kpi")
public class KPIController {

  private final KPIEngine kpiEngine;
  private final KPIDefinitionRepository kpiRepository;

  public KPIController(KPIEngine kpiEngine, KPIDefinitionRepository kpiRepository) {
    this.kpiEngine = kpiEngine;
    this.kpiRepository = kpiRepository;
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<KPIResponse>>> getAll() {
    List<KPIResponse> results = kpiEngine.calculateAll();
    return ResponseEntity.ok(ApiResponse.success(results, "All KPIs calculated"));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<KPIResponse>> getById(@PathVariable UUID id) {
    KPIResponse result = kpiEngine.calculate(id);
    return ResponseEntity.ok(ApiResponse.success(result, "KPI calculated"));
  }

  @GetMapping("/category/{category}")
  public ResponseEntity<ApiResponse<List<KPIResponse>>> getByCategory(@PathVariable String category) {
    List<KPIResponse> results = kpiEngine.calculateByCategory(category);
    return ResponseEntity.ok(ApiResponse.success(results, "KPIs by category"));
  }

  @PostMapping("/definitions")
  public ResponseEntity<ApiResponse<KPIResponse>> createDefinition(@RequestBody KPIDefinition def) {
    def.setCreatedAt(LocalDateTime.now());
    def.setUpdatedAt(LocalDateTime.now());
    if (def.getIsActive() == null) def.setIsActive(true);
    if (def.getRefreshInterval() == null) def.setRefreshInterval(300);
    KPIDefinition saved = kpiRepository.save(def);
    return ResponseEntity.ok(ApiResponse.success(kpiEngine.calculate(saved), "KPI definition created"));
  }

  @PostMapping("/{id}/refresh")
  public ResponseEntity<ApiResponse<Void>> refresh(@PathVariable UUID id) {
    kpiEngine.invalidateCache(id);
    return ResponseEntity.ok(ApiResponse.successMessage("KPI cache refreshed"));
  }

  @PostMapping("/refresh-all")
  public ResponseEntity<ApiResponse<Void>> refreshAll() {
    kpiEngine.invalidateAll();
    return ResponseEntity.ok(ApiResponse.successMessage("All KPI caches refreshed"));
  }
}
