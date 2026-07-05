package com.erp.core.metadata.service;

import com.erp.core.metadata.dto.ActionMetadataDto;
import com.erp.core.metadata.dto.MetadataBundleDto;
import com.erp.core.metadata.dto.ModelMetadataDto;
import com.erp.core.metadata.dto.PermissionMetadataDto;
import com.erp.core.metadata.dto.ViewMetadataDto;
import com.erp.core.metadata.dto.WorkflowMetadataDto;
import com.erp.core.metadata.registry.MetadataRegistry;
import com.erp.core.metadata.validator.MetadataValidator;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service layer for metadata operations.
 * Handles metadata lookup, aggregation, validation, and caching.
 */
@Service
public class MetadataService {

  private final MetadataRegistry metadataRegistry;
  private final MetadataValidator metadataValidator;

  public MetadataService(MetadataRegistry metadataRegistry, MetadataValidator metadataValidator) {
    this.metadataRegistry = metadataRegistry;
    this.metadataValidator = metadataValidator;
  }

  /**
   * Get model metadata by code.
   */
  public ModelMetadataDto getModel(String code) {
    return metadataRegistry.findModel(code);
  }

  /**
   * Get view metadata by code.
   */
  public ViewMetadataDto getView(String code) {
    return metadataRegistry.findView(code);
  }

  /**
   * Get workflow metadata by code.
   */
  public WorkflowMetadataDto getWorkflow(String code) {
    return metadataRegistry.findWorkflow(code);
  }

  /**
   * Get action metadata by code.
   */
  public ActionMetadataDto getAction(String code) {
    return metadataRegistry.findAction(code);
  }

  /**
   * Get permission metadata by code.
   */
  public PermissionMetadataDto getPermission(String code) {
    return metadataRegistry.findPermission(code);
  }

  /**
   * Get complete metadata bundle for a model.
   * Aggregates model, views, workflow, actions, and permissions.
   */
  public MetadataBundleDto getMetadataBundle(String modelCode) {
    ModelMetadataDto model = getModel(modelCode);

    MetadataBundleDto bundle = new MetadataBundleDto();
    bundle.setModel(model);
    bundle.setViews(metadataRegistry.getViewsByModel(modelCode));

    // Workflow is optional
    try {
      String workflowCode = modelCode + "_workflow";
      bundle.setWorkflow(metadataRegistry.findWorkflow(workflowCode));
    } catch (Exception e) {
      // Workflow not found, leave as null
    }

    return bundle;
  }

  /**
   * Register model metadata.
   */
  public void registerModel(ModelMetadataDto model) {
    metadataValidator.validateModel(model);
    metadataRegistry.registerModel(model);
  }

  /**
   * Register view metadata.
   */
  public void registerView(ViewMetadataDto view) {
    metadataRegistry.registerView(view);
  }

  /**
   * Register workflow metadata.
   */
  public void registerWorkflow(WorkflowMetadataDto workflow) {
    metadataValidator.validateWorkflow(workflow);
    metadataRegistry.registerWorkflow(workflow);
  }

  /**
   * Register action metadata.
   */
  public void registerAction(ActionMetadataDto action) {
    metadataRegistry.registerAction(action);
  }

  /**
   * Register permission metadata.
   */
  public void registerPermission(PermissionMetadataDto permission) {
    metadataRegistry.registerPermission(permission);
  }

  /**
   * Get all models.
   */
  public List<ModelMetadataDto> getAllModels() {
    return metadataRegistry.getAllModels();
  }

  /**
   * Get all views.
   */
  public List<ViewMetadataDto> getAllViews() {
    return metadataRegistry.getAllViews();
  }

  /**
   * Get all workflows.
   */
  public List<WorkflowMetadataDto> getAllWorkflows() {
    return metadataRegistry.getAllWorkflows();
  }

  /**
   * Get all actions.
   */
  public List<ActionMetadataDto> getAllActions() {
    return metadataRegistry.getAllActions();
  }

  /**
   * Get all permissions.
   */
  public List<PermissionMetadataDto> getAllPermissions() {
    return metadataRegistry.getAllPermissions();
  }
}
