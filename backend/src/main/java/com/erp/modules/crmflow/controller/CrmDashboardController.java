package com.erp.modules.crmflow.controller;

import com.erp.common.api.ApiResponse;
import com.erp.config.ApiVersionConfig;
import com.erp.modules.crmflow.dto.CrmDashboardResponse;
import com.erp.modules.crmflow.service.CrmDashboardService;
import com.erp.platform.identity.dto.RuntimeContext;
import com.erp.platform.identity.dto.RuntimeContextHolder;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiVersionConfig.API_BASE + "/crm")
public class CrmDashboardController {

  private static final Logger log = LoggerFactory.getLogger(CrmDashboardController.class);

  private final CrmDashboardService dashboardService;

  public CrmDashboardController(CrmDashboardService dashboardService) {
    this.dashboardService = dashboardService;
  }

  @GetMapping("/dashboard")
  public ResponseEntity<ApiResponse<CrmDashboardResponse>> dashboard() {
    RuntimeContext ctx = RuntimeContextHolder.get();
    if (ctx == null || ctx.getTenantId() == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(new ApiResponse<>(false, null, "Authentication required.", "UNAUTHORIZED", Collections.emptyList()));
    }
    return ResponseEntity.ok(ApiResponse.success(
        dashboardService.buildDashboard(ctx.getTenantId()),
        "CRM dashboard retrieved."));
  }
}
