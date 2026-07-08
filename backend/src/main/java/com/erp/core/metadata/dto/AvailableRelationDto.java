package com.erp.core.metadata.dto;

import java.util.List;

public class AvailableRelationDto {
  private String relationCode;
  private String childTableCode;
  private String childTableLabel;
  private String relationColumnLabel;
  private List<String> existingFormCodes;

  public AvailableRelationDto() {}

  public String getRelationCode() { return relationCode; }
  public void setRelationCode(String relationCode) { this.relationCode = relationCode; }
  public String getChildTableCode() { return childTableCode; }
  public void setChildTableCode(String childTableCode) { this.childTableCode = childTableCode; }
  public String getChildTableLabel() { return childTableLabel; }
  public void setChildTableLabel(String childTableLabel) { this.childTableLabel = childTableLabel; }
  public String getRelationColumnLabel() { return relationColumnLabel; }
  public void setRelationColumnLabel(String relationColumnLabel) { this.relationColumnLabel = relationColumnLabel; }
  public List<String> getExistingFormCodes() { return existingFormCodes; }
  public void setExistingFormCodes(List<String> existingFormCodes) { this.existingFormCodes = existingFormCodes; }
}
