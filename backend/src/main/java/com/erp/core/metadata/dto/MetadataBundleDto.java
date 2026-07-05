package com.erp.core.metadata.dto;

import java.util.List;

public class MetadataBundleDto {

  private ModelMetadataDto model;
  private List<ViewMetadataDto> views;
  private WorkflowMetadataDto workflow;
  private List<ActionMetadataDto> actions;
  private List<PermissionMetadataDto> permissions;

  public MetadataBundleDto() {}

  public ModelMetadataDto getModel() {
    return model;
  }

  public void setModel(ModelMetadataDto model) {
    this.model = model;
  }

  public List<ViewMetadataDto> getViews() {
    return views;
  }

  public void setViews(List<ViewMetadataDto> views) {
    this.views = views;
  }

  public WorkflowMetadataDto getWorkflow() {
    return workflow;
  }

  public void setWorkflow(WorkflowMetadataDto workflow) {
    this.workflow = workflow;
  }

  public List<ActionMetadataDto> getActions() {
    return actions;
  }

  public void setActions(List<ActionMetadataDto> actions) {
    this.actions = actions;
  }

  public List<PermissionMetadataDto> getPermissions() {
    return permissions;
  }

  public void setPermissions(List<PermissionMetadataDto> permissions) {
    this.permissions = permissions;
  }
}
