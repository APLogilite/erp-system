package com.erp.core.metadata.dto;

public class FormUpdateRequest {

  private String label;
  private String description;
  private String whereClauseField;
  private String whereClauseOperator;
  private String whereClauseValue;

  public FormUpdateRequest() {}

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getWhereClauseField() {
    return whereClauseField;
  }

  public void setWhereClauseField(String whereClauseField) {
    this.whereClauseField = whereClauseField;
  }

  public String getWhereClauseOperator() {
    return whereClauseOperator;
  }

  public void setWhereClauseOperator(String whereClauseOperator) {
    this.whereClauseOperator = whereClauseOperator;
  }

  public String getWhereClauseValue() {
    return whereClauseValue;
  }

  public void setWhereClauseValue(String whereClauseValue) {
    this.whereClauseValue = whereClauseValue;
  }
}
