package com.erp.modules.platform.controller;

import com.erp.common.api.ApiResponse;
import com.erp.config.ApiVersionConfig;
import com.erp.modules.platform.dto.PlatformEventResponse;
import com.erp.modules.platform.service.PlatformEventService;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiVersionConfig.API_V1 + "/events")
public class EventController {

  private final PlatformEventService platformEventService;

  public EventController(PlatformEventService platformEventService) {
    this.platformEventService = platformEventService;
  }

  @PostMapping
  public ResponseEntity<ApiResponse<PlatformEventResponse>> publish(
      @RequestParam String eventType,
      @RequestParam String sourceModule,
      @RequestParam(required = false) String sourceId,
      @RequestParam(required = false) String payload) {
    return ResponseEntity.ok(ApiResponse.success(
        platformEventService.publish(eventType, sourceModule, sourceId, payload),
        "Event published"));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<PlatformEventResponse>>> getAll() {
    return ResponseEntity.ok(ApiResponse.success(platformEventService.getAll(), "Events retrieved"));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<PlatformEventResponse>> getById(@PathVariable UUID id) {
    return ResponseEntity.ok(ApiResponse.success(platformEventService.getById(id), "Event retrieved"));
  }

  @GetMapping("/module/{sourceModule}")
  public ResponseEntity<ApiResponse<List<PlatformEventResponse>>> getByModule(@PathVariable String sourceModule) {
    return ResponseEntity.ok(ApiResponse.success(
        platformEventService.getBySourceModule(sourceModule), "Events retrieved"));
  }

  @GetMapping("/type/{eventType}")
  public ResponseEntity<ApiResponse<List<PlatformEventResponse>>> getByType(@PathVariable String eventType) {
    return ResponseEntity.ok(ApiResponse.success(
        platformEventService.getByEventType(eventType), "Events retrieved"));
  }
}
