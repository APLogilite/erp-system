package com.erp.core.runtime.dto;

import java.util.UUID;

public class SubFormDefinitionResponse {

  private UUID id;
  private String relationCode;
  private String childFormCode;
  private String label;
  private String displayAs;
  private Integer position;

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
}
