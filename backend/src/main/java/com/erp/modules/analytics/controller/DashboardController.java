package com.erp.modules.analytics.controller;

import com.erp.common.api.ApiResponse;
import com.erp.config.ApiVersionConfig;
import com.erp.modules.analytics.dto.DashboardRequest;
import com.erp.modules.analytics.dto.DashboardResponse;
import com.erp.modules.analytics.service.DashboardService;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiVersionConfig.API_V1 + "/dashboards")
public class DashboardController {

  private final DashboardService dashboardService;

  public DashboardController(DashboardService dashboardService) {
    this.dashboardService = dashboardService;
  }

  @PostMapping
  public ResponseEntity<ApiResponse<UUID>> create(@RequestBody DashboardRequest request) {
    UUID id = dashboardService.createWithWidgets(request);
    return ResponseEntity.ok(ApiResponse.success(id, "Dashboard created"));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<DashboardResponse>>> getAll() {
    List<DashboardResponse> list = dashboardService.getAllWithWidgets();
    return ResponseEntity.ok(ApiResponse.success(list, "Dashboards retrieved"));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<DashboardResponse>> getById(@PathVariable UUID id) {
    DashboardResponse response = dashboardService.getDashboardWithWidgets(id);
    return ResponseEntity.ok(ApiResponse.success(response, "Dashboard retrieved"));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<DashboardResponse>> update(@PathVariable UUID id, @RequestBody DashboardRequest request) {
    var existing = dashboardService.findByIdOrThrow(id);
    existing.setName(request.getName());
    existing.setDescription(request.getDescription());
    existing.setLayout(request.getLayout());
    existing.setIsDefault(request.getIsDefault());
    existing.setRoles(request.getRoles());
    var updated = dashboardService.update(existing);
    return ResponseEntity.ok(ApiResponse.success(dashboardService.getDashboardWithWidgets(updated.getId()), "Dashboard updated"));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
    dashboardService.delete(id);
    return ResponseEntity.ok(ApiResponse.successMessage("Dashboard deleted"));
  }
}
