package com.erp.core.metadata.dto;

public class FormCreateRequest {

  private String name;
  private String label;
  private String modelName;
  private String scope;
  private String description;
  private String whereClauseField;
  private String whereClauseOperator;
  private String whereClauseValue;

  public FormCreateRequest() {}

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getModelName() {
    return modelName;
  }

  public void setModelName(String modelName) {
    this.modelName = modelName;
  }

  public String getScope() {
    return scope;
  }

  public void setScope(String scope) {
    this.scope = scope;
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
