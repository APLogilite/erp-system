package com.erp.core.metadata.dto;

import java.util.List;

public class ModelMetadataDto {

  private String code;
  private String name;
  private String description;
  private List<FieldMetadataDto> fields;
  private boolean auditable;
  private boolean workflowEnabled;
  private boolean active;

  public ModelMetadataDto() {}

  public ModelMetadataDto(String code, String name, String description) {
    this.code = code;
    this.name = name;
    this.description = description;
    this.auditable = true;
    this.workflowEnabled = false;
    this.active = true;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public List<FieldMetadataDto> getFields() {
    return fields;
  }

  public void setFields(List<FieldMetadataDto> fields) {
    this.fields = fields;
  }

  public boolean isAuditable() {
    return auditable;
  }

  public void setAuditable(boolean auditable) {
    this.auditable = auditable;
  }

  public boolean isWorkflowEnabled() {
    return workflowEnabled;
  }

  public void setWorkflowEnabled(boolean workflowEnabled) {
    this.workflowEnabled = workflowEnabled;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }
}
