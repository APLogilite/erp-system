package com.erp.core.runtime.dto;

import java.util.UUID;

public class SubFormDefinitionResponse {

  private UUID id;
  private String relationCode;
  private String childFormCode;
  private String label;
  private String displayAs;
  private Integer position;
  // One-level-deep child form definition info (FR-014)
  private UUID childFormId;
  private String childFormLabel;
  private String childFormModelName;
  private String childFormTableName;

  public SubFormDefinitionResponse() {}

  public UUID getId() { return id; }
  public void setId(UUID id) { this.id = id; }
  public String getRelationCode() { return relationCode; }
  public void setRelationCode(String relationCode) { this.relationCode = relationCode; }
  public String getChildFormCode() { return childFormCode; }
  public void setChildFormCode(String childFormCode) { this.childFormCode = childFormCode; }
  public String getLabel() { return label; }
  public void setLabel(String label) { this.label = label; }
  public String getDisplayAs() { return displayAs; }
  public void setDisplayAs(String displayAs) { this.displayAs = displayAs; }
  public Integer getPosition() { return position; }
  public void setPosition(Integer position) { this.position = position; }
  public UUID getChildFormId() { return childFormId; }
  public void setChildFormId(UUID childFormId) { this.childFormId = childFormId; }
  public String getChildFormLabel() { return childFormLabel; }
  public void setChildFormLabel(String childFormLabel) { this.childFormLabel = childFormLabel; }
  public String getChildFormModelName() { return childFormModelName; }
  public void setChildFormModelName(String childFormModelName) { this.childFormModelName = childFormModelName; }
  public String getChildFormTableName() { return childFormTableName; }
  public void setChildFormTableName(String childFormTableName) { this.childFormTableName = childFormTableName; }
}
