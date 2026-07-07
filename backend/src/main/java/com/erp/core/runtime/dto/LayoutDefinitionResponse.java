package com.erp.core.runtime.dto;

import java.util.List;
import java.util.UUID;

public class LayoutDefinitionResponse {

  private UUID sectionId;
  private String code;
  private String label;
  private Boolean collapsible;
  private Integer columns;
  private Integer position;
  private List<UUID> fieldIds;

  public LayoutDefinitionResponse() {}

  public UUID getSectionId() { return sectionId; }
  public void setSectionId(UUID sectionId) { this.sectionId = sectionId; }
  public String getCode() { return code; }
  public void setCode(String code) { this.code = code; }
  public String getLabel() { return label; }
  public void setLabel(String label) { this.label = label; }
  public Boolean getCollapsible() { return collapsible; }
  public void setCollapsible(Boolean collapsible) { this.collapsible = collapsible; }
  public Integer getColumns() { return columns; }
  public void setColumns(Integer columns) { this.columns = columns; }
  public Integer getPosition() { return position; }
  public void setPosition(Integer position) { this.position = position; }
  public List<UUID> getFieldIds() { return fieldIds; }
  public void setFieldIds(List<UUID> fieldIds) { this.fieldIds = fieldIds; }
}
