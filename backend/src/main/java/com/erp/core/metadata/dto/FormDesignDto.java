package com.erp.core.metadata.dto;

import java.util.List;
import java.util.UUID;

/**
 * Full form design response containing the form header, fields,
 * field rules, field validations, layout sections, and sub-forms.
 */
public class FormDesignDto {

  private UUID id;
  private String name;
  private String label;
  private String modelName;
  private String type;
  private String scope;
  private UUID tenantId;
  private String description;
  private String whereClauseField;
  private String whereClauseOperator;
  private String whereClauseValue;
  private Boolean isActive;
  private List<FormFieldDto> fields;
  private List<FormLayoutSectionDto> sections;
  private List<FormSubFormDto> subForms;

  public FormDesignDto() {}

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

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

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getScope() {
    return scope;
  }

  public void setScope(String scope) {
    this.scope = scope;
  }

  public UUID getTenantId() {
    return tenantId;
  }

  public void setTenantId(UUID tenantId) {
    this.tenantId = tenantId;
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

  public Boolean getIsActive() {
    return isActive;
  }

  public void setIsActive(Boolean isActive) {
    this.isActive = isActive;
  }

  public List<FormFieldDto> getFields() {
    return fields;
  }

  public void setFields(List<FormFieldDto> fields) {
    this.fields = fields;
  }

  public List<FormLayoutSectionDto> getSections() {
    return sections;
  }

  public void setSections(List<FormLayoutSectionDto> sections) {
    this.sections = sections;
  }

  public List<FormSubFormDto> getSubForms() {
    return subForms;
  }

  public void setSubForms(List<FormSubFormDto> subForms) {
    this.subForms = subForms;
  }
}
