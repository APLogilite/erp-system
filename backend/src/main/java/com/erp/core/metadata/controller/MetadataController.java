package com.erp.core.metadata.controller;

import com.erp.common.api.ApiResponse;
import com.erp.config.ApiVersionConfig;
import com.erp.core.metadata.dto.ActionMetadataDto;
import com.erp.core.metadata.dto.MetadataBundleDto;
import com.erp.core.metadata.dto.ModelMetadataDto;
import com.erp.core.metadata.dto.PermissionMetadataDto;
import com.erp.core.metadata.dto.ViewMetadataDto;
import com.erp.core.metadata.dto.WorkflowMetadataDto;
import com.erp.core.metadata.service.MetadataService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST API for metadata queries.
 * Provides runtime metadata to frontend for dynamic rendering.
 */
@RestController
@RequestMapping(ApiVersionConfig.API_BASE + "/metadata")
public class MetadataController {

  private final MetadataService metadataService;

  public MetadataController(MetadataService metadataService) {
    this.metadataService = metadataService;
  }

  /**
   * Get model metadata by code.
   * GET /api/metadata/models/{code}
   */
  @GetMapping("/models/{code}")
  public ResponseEntity<ApiResponse<ModelMetadataDto>> getModel(@PathVariable String code) {
    ModelMetadataDto model = metadataService.getModel(code);
    return ResponseEntity.ok(ApiResponse.success(model, "Model metadata retrieved."));
  }

  /**
   * Get all models.
   * GET /api/metadata/models
   */
  @GetMapping("/models")
  public ResponseEntity<ApiResponse<List<ModelMetadataDto>>> getAllModels() {
    List<ModelMetadataDto> models = metadataService.getAllModels();
    return ResponseEntity.ok(ApiResponse.success(models, "Model list retrieved."));
  }

  /**
   * Get view metadata by code.
   * GET /api/metadata/views/{code}
   */
  @GetMapping("/views/{code}")
  public ResponseEntity<ApiResponse<ViewMetadataDto>> getView(@PathVariable String code) {
    ViewMetadataDto view = metadataService.getView(code);
    return ResponseEntity.ok(ApiResponse.success(view, "View metadata retrieved."));
  }

  /**
   * Get all views.
   * GET /api/metadata/views
   */
  @GetMapping("/views")
  public ResponseEntity<ApiResponse<List<ViewMetadataDto>>> getAllViews() {
    List<ViewMetadataDto> views = metadataService.getAllViews();
    return ResponseEntity.ok(ApiResponse.success(views, "View list retrieved."));
  }

  /**
   * Get workflow metadata by code.
   * GET /api/metadata/workflows/{code}
   */
  @GetMapping("/workflows/{code}")
  public ResponseEntity<ApiResponse<WorkflowMetadataDto>> getWorkflow(@PathVariable String code) {
    WorkflowMetadataDto workflow = metadataService.getWorkflow(code);
    return ResponseEntity.ok(ApiResponse.success(workflow, "Workflow metadata retrieved."));
  }

  /**
   * Get all workflows.
   * GET /api/metadata/workflows
   */
  @GetMapping("/workflows")
  public ResponseEntity<ApiResponse<List<WorkflowMetadataDto>>> getAllWorkflows() {
    List<WorkflowMetadataDto> workflows = metadataService.getAllWorkflows();
    return ResponseEntity.ok(ApiResponse.success(workflows, "Workflow list retrieved."));
  }

  /**
   * Get action metadata by code.
   * GET /api/metadata/actions/{code}
   */
  @GetMapping("/actions/{code}")
  public ResponseEntity<ApiResponse<ActionMetadataDto>> getAction(@PathVariable String code) {
    ActionMetadataDto action = metadataService.getAction(code);
    return ResponseEntity.ok(ApiResponse.success(action, "Action metadata retrieved."));
  }

  /**
   * Get all actions.
   * GET /api/metadata/actions
   */
  @GetMapping("/actions")
  public ResponseEntity<ApiResponse<List<ActionMetadataDto>>> getAllActions() {
    List<ActionMetadataDto> actions = metadataService.getAllActions();
    return ResponseEntity.ok(ApiResponse.success(actions, "Action list retrieved."));
  }

  /**
   * Get permission metadata by code.
   * GET /api/metadata/permissions/{code}
   */
  @GetMapping("/permissions/{code}")
  public ResponseEntity<ApiResponse<PermissionMetadataDto>> getPermission(@PathVariable String code) {
    PermissionMetadataDto permission = metadataService.getPermission(code);
    return ResponseEntity.ok(ApiResponse.success(permission, "Permission metadata retrieved."));
  }

  /**
   * Get all permissions.
   * GET /api/metadata/permissions
   */
  @GetMapping("/permissions")
  public ResponseEntity<ApiResponse<List<PermissionMetadataDto>>> getAllPermissions() {
    List<PermissionMetadataDto> permissions = metadataService.getAllPermissions();
    return ResponseEntity.ok(ApiResponse.success(permissions, "Permission list retrieved."));
  }

  /**
   * Get complete metadata bundle for a model.
   * Aggregates model, views, workflow, actions, permissions.
   * GET /api/metadata/bundle/{modelCode}
   */
  @GetMapping("/bundle/{modelCode}")
  public ResponseEntity<ApiResponse<MetadataBundleDto>> getMetadataBundle(@PathVariable String modelCode) {
    MetadataBundleDto bundle = metadataService.getMetadataBundle(modelCode);
    return ResponseEntity.ok(ApiResponse.success(bundle, "Metadata bundle retrieved."));
  }
}
