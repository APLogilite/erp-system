package com.erp.core.metadata.dto;

import java.util.UUID;

public class GlobalFormDto {
  private UUID formId;
  private String formCode;
  private String formLabel;
  private String modelName;
  private boolean hasConfiguredAccess;

  public GlobalFormDto() {}

  public UUID getFormId() { return formId; }
  public void setFormId(UUID formId) { this.formId = formId; }
  public String getFormCode() { return formCode; }
  public void setFormCode(String formCode) { this.formCode = formCode; }
  public String getFormLabel() { return formLabel; }
  public void setFormLabel(String formLabel) { this.formLabel = formLabel; }
  public String getModelName() { return modelName; }
  public void setModelName(String modelName) { this.modelName = modelName; }
  public boolean isHasConfiguredAccess() { return hasConfiguredAccess; }
  public void setHasConfiguredAccess(boolean hasConfiguredAccess) { this.hasConfiguredAccess = hasConfiguredAccess; }
}
