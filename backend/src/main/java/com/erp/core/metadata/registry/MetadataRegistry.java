package com.erp.core.metadata.registry;

import com.erp.core.metadata.dto.ActionMetadataDto;
import com.erp.core.metadata.dto.ModelMetadataDto;
import com.erp.core.metadata.dto.PermissionMetadataDto;
import com.erp.core.metadata.dto.ViewMetadataDto;
import com.erp.core.metadata.dto.WorkflowMetadataDto;
import com.erp.core.metadata.exception.MetadataNotFoundException;
import com.erp.core.metadata.exception.MetadataValidationException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Central runtime metadata registry.
 * Thread-safe using ConcurrentHashMap.
 * Designed for plugin extension via registerMetadata().
 */
@Component
public class MetadataRegistry {

  private final ConcurrentHashMap<String, ModelMetadataDto> models = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<String, ViewMetadataDto> views = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<String, WorkflowMetadataDto> workflows = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<String, ActionMetadataDto> actions = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<String, PermissionMetadataDto> permissions = new ConcurrentHashMap<>();

  /**
   * Register a model metadata definition.
   */
  public void registerModel(ModelMetadataDto model) {
    if (models.containsKey(model.getCode())) {
      throw new MetadataValidationException(
          "Model already registered: " + model.getCode()
      );
    }
    models.put(model.getCode(), model);
  }

  /**
   * Register a view metadata definition.
   */
  public void registerView(ViewMetadataDto view) {
    if (views.containsKey(view.getCode())) {
      throw new MetadataValidationException(
          "View already registered: " + view.getCode()
      );
    }
    views.put(view.getCode(), view);
  }

  /**
   * Register a workflow metadata definition.
   */
  public void registerWorkflow(WorkflowMetadataDto workflow) {
    if (workflows.containsKey(workflow.getCode())) {
      throw new MetadataValidationException(
          "Workflow already registered: " + workflow.getCode()
      );
    }
    workflows.put(workflow.getCode(), workflow);
  }

  /**
   * Register an action metadata definition.
   */
  public void registerAction(ActionMetadataDto action) {
    if (actions.containsKey(action.getCode())) {
      throw new MetadataValidationException(
          "Action already registered: " + action.getCode()
      );
    }
    actions.put(action.getCode(), action);
  }

  /**
   * Register permission metadata definition.
   */
  public void registerPermission(PermissionMetadataDto permission) {
    if (permissions.containsKey(permission.getCode())) {
      throw new MetadataValidationException(
          "Permission already registered: " + permission.getCode()
      );
    }
    permissions.put(permission.getCode(), permission);
  }

  /**
   * Find model by code.
   */
  public ModelMetadataDto findModel(String code) {
    ModelMetadataDto model = models.get(code);
    if (model == null) {
      throw new MetadataNotFoundException("Model not found: " + code, code);
    }
    return model;
  }

  /**
   * Find view by code.
   */
  public ViewMetadataDto findView(String code) {
    ViewMetadataDto view = views.get(code);
    if (view == null) {
      throw new MetadataNotFoundException("View not found: " + code, code);
    }
    return view;
  }

  /**
   * Find workflow by code.
   */
  public WorkflowMetadataDto findWorkflow(String code) {
    WorkflowMetadataDto workflow = workflows.get(code);
    if (workflow == null) {
      throw new MetadataNotFoundException("Workflow not found: " + code, code);
    }
    return workflow;
  }

  /**
   * Find action by code.
   */
  public ActionMetadataDto findAction(String code) {
    ActionMetadataDto action = actions.get(code);
    if (action == null) {
      throw new MetadataNotFoundException("Action not found: " + code, code);
    }
    return action;
  }

  /**
   * Find permission by code.
   */
  public PermissionMetadataDto findPermission(String code) {
    PermissionMetadataDto permission = permissions.get(code);
    if (permission == null) {
      throw new MetadataNotFoundException("Permission not found: " + code, code);
    }
    return permission;
  }

  /**
   * Get all models.
   */
  public List<ModelMetadataDto> getAllModels() {
    return List.copyOf(models.values());
  }

  /**
   * Get all views.
   */
  public List<ViewMetadataDto> getAllViews() {
    return List.copyOf(views.values());
  }

  /**
   * Get all workflows.
   */
  public List<WorkflowMetadataDto> getAllWorkflows() {
    return List.copyOf(workflows.values());
  }

  /**
   * Get all actions.
   */
  public List<ActionMetadataDto> getAllActions() {
    return List.copyOf(actions.values());
  }

  /**
   * Get all permissions.
   */
  public List<PermissionMetadataDto> getAllPermissions() {
    return List.copyOf(permissions.values());
  }

  /**
   * Get views for a specific model.
   */
  public List<ViewMetadataDto> getViewsByModel(String modelCode) {
    return views.values().stream()
        .filter(v -> modelCode.equals(v.getModelCode()))
        .toList();
  }

  /**
   * Refresh registry (placeholder for plugin integration).
   */
  public void refresh() {
    // Placeholder for future refresh logic (e.g., from database)
  }

  /**
   * Clear all metadata (for testing).
   */
  public void clear() {
    models.clear();
    views.clear();
    workflows.clear();
    actions.clear();
    permissions.clear();
  }
}
