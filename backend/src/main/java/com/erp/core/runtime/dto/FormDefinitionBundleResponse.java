package com.erp.core.runtime.dto;

import java.util.List;
import java.util.UUID;

public class FormDefinitionBundleResponse {

  private UUID formId;
  private String formCode;
  private String formLabel;
  private String modelName;
  private String modelLabel;
  private String tableName;
  private String whereClauseField;
  private String whereClauseOperator;
  private String whereClauseValue;
  private List<FieldDefinitionResponse> fields;
  private List<LayoutDefinitionResponse> sections;
  private List<SubFormDefinitionResponse> subForms;

  public FormDefinitionBundleResponse() {}

  public UUID getFormId() { return formId; }
  public void setFormId(UUID formId) { this.formId = formId; }
  public String getFormCode() { return formCode; }
  public void setFormCode(String formCode) { this.formCode = formCode; }
  public String getFormLabel() { return formLabel; }
  public void setFormLabel(String formLabel) { this.formLabel = formLabel; }
  public String getModelName() { return modelName; }
  public void setModelName(String modelName) { this.modelName = modelName; }
  public String getModelLabel() { return modelLabel; }
  public void setModelLabel(String modelLabel) { this.modelLabel = modelLabel; }
  public String getTableName() { return tableName; }
  public void setTableName(String tableName) { this.tableName = tableName; }
  public String getWhereClauseField() { return whereClauseField; }
  public void setWhereClauseField(String whereClauseField) { this.whereClauseField = whereClauseField; }
  public String getWhereClauseOperator() { return whereClauseOperator; }
  public void setWhereClauseOperator(String whereClauseOperator) { this.whereClauseOperator = whereClauseOperator; }
  public String getWhereClauseValue() { return whereClauseValue; }
  public void setWhereClauseValue(String whereClauseValue) { this.whereClauseValue = whereClauseValue; }
  public List<FieldDefinitionResponse> getFields() { return fields; }
  public void setFields(List<FieldDefinitionResponse> fields) { this.fields = fields; }
  public List<LayoutDefinitionResponse> getSections() { return sections; }
  public void setSections(List<LayoutDefinitionResponse> sections) { this.sections = sections; }
  public List<SubFormDefinitionResponse> getSubForms() { return subForms; }
  public void setSubForms(List<SubFormDefinitionResponse> subForms) { this.subForms = subForms; }
}
