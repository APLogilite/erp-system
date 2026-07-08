package com.erp.core.metadata.dto;

public class UpdateTableRequest {
  private String label;
  private String pluralLabel;
  private String description;

  public UpdateTableRequest() {}

  public String getLabel() { return label; }
  public void setLabel(String label) { this.label = label; }
  public String getPluralLabel() { return pluralLabel; }
  public void setPluralLabel(String pluralLabel) { this.pluralLabel = pluralLabel; }
  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }
}
