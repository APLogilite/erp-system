package com.erp.modules.platform.controller;

import com.erp.common.api.ApiResponse;
import com.erp.config.ApiVersionConfig;
import com.erp.modules.platform.dto.ActivityEventResponse;
import com.erp.modules.platform.service.ActivityTimelineService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiVersionConfig.API_V1 + "/activities")
public class ActivityController {

  private final ActivityTimelineService activityTimelineService;

  public ActivityController(ActivityTimelineService activityTimelineService) {
    this.activityTimelineService = activityTimelineService;
  }

  @PostMapping
  public ResponseEntity<ApiResponse<ActivityEventResponse>> record(
      @RequestParam String eventType,
      @RequestParam(required = false) String module,
      @RequestParam(required = false) String recordId,
      @RequestParam(required = false) String actor,
      @RequestParam String summary,
      @RequestParam(required = false) String details) {
    return ResponseEntity.ok(ApiResponse.success(
        activityTimelineService.record(eventType, module, recordId, actor, summary, details),
        "Activity recorded"));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<ActivityEventResponse>>> getAll() {
    return ResponseEntity.ok(ApiResponse.success(activityTimelineService.getAllTimeline(), "Activities retrieved"));
  }

  @GetMapping("/module/{module}")
  public ResponseEntity<ApiResponse<List<ActivityEventResponse>>> getByModule(@PathVariable String module) {
    return ResponseEntity.ok(ApiResponse.success(activityTimelineService.getModuleTimeline(module), "Activities retrieved"));
  }

  @GetMapping("/by-relation")
  public ResponseEntity<ApiResponse<List<ActivityEventResponse>>> getByRelation(
      @RequestParam String module, @RequestParam String recordId) {
    return ResponseEntity.ok(ApiResponse.success(
        activityTimelineService.getTimeline(module, recordId), "Activities retrieved"));
  }
}
