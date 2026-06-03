package com.erp.core.metadata.controller;

import com.erp.common.api.ApiResponse;
import com.erp.config.ApiVersionConfig;
import com.erp.core.metadata.entity.MetadataModel;
import com.erp.core.metadata.entity.MetadataPermission;
import com.erp.core.metadata.entity.MetadataView;
import com.erp.core.metadata.entity.MetadataWorkflow;
import com.erp.core.metadata.service.MetadataRegistryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(ApiVersionConfig.API_BASE + "/metadata")
public class MetadataController {

  private final MetadataRegistryService metadataRegistryService;

  public MetadataController(MetadataRegistryService metadataRegistryService) {
    this.metadataRegistryService = metadataRegistryService;
  }

  @GetMapping
  public ResponseEntity<ApiResponse<Map<String, Object>>> getMetadataPackage() {
    Map<String, Object> payload = metadataRegistryService.getFullMetadataPackage();
    return ResponseEntity.ok(ApiResponse.success(payload, "Metadata package loaded."));
  }

  @GetMapping("/models")
  public ResponseEntity<ApiResponse<List<MetadataModel>>> getModels() {
    List<MetadataModel> models = metadataRegistryService.getAllModels();
    return ResponseEntity.ok(ApiResponse.success(models, "Metadata models loaded."));
  }

  @GetMapping("/views")
  public ResponseEntity<ApiResponse<List<MetadataView>>> getViews() {
    List<MetadataView> views = metadataRegistryService.getAllViews();
    return ResponseEntity.ok(ApiResponse.success(views, "Metadata views loaded."));
  }

  @GetMapping("/workflows")
  public ResponseEntity<ApiResponse<List<MetadataWorkflow>>> getWorkflows() {
    List<MetadataWorkflow> workflows = metadataRegistryService.getAllWorkflows();
    return ResponseEntity.ok(ApiResponse.success(workflows, "Metadata workflows loaded."));
  }

  @GetMapping("/permissions")
  public ResponseEntity<ApiResponse<List<MetadataPermission>>> getPermissions() {
    List<MetadataPermission> permissions = metadataRegistryService.getAllPermissions();
    return ResponseEntity.ok(ApiResponse.success(permissions, "Metadata permissions loaded."));
  }
}
