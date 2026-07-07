package com.erp.core.runtime.dto;

import java.util.List;
import java.util.UUID;

public class FieldDefinitionResponse {

  private UUID fieldId;
  private String columnCode;
  private String label;
  private String type;
  private Boolean visible;
  private Boolean readOnly;
  private Boolean required;
  private Integer position;
  private String defaultValue;
  private String placeholder;
  private String relationTable;
  private List<String> enumOptions;
  private List<RuleDef> rules;
  private List<ValidationDef> validations;

  public FieldDefinitionResponse() {}

  public UUID getFieldId() { return fieldId; }
  public void setFieldId(UUID fieldId) { this.fieldId = fieldId; }
  public String getColumnCode() { return columnCode; }
  public void setColumnCode(String columnCode) { this.columnCode = columnCode; }
  public String getLabel() { return label; }
  public void setLabel(String label) { this.label = label; }
  public String getType() { return type; }
  public void setType(String type) { this.type = type; }
  public Boolean getVisible() { return visible; }
  public void setVisible(Boolean visible) { this.visible = visible; }
  public Boolean getReadOnly() { return readOnly; }
  public void setReadOnly(Boolean readOnly) { this.readOnly = readOnly; }
  public Boolean getRequired() { return required; }
  public void setRequired(Boolean required) { this.required = required; }
  public Integer getPosition() { return position; }
  public void setPosition(Integer position) { this.position = position; }
  public String getDefaultValue() { return defaultValue; }
  public void setDefaultValue(String defaultValue) { this.defaultValue = defaultValue; }
  public String getPlaceholder() { return placeholder; }
  public void setPlaceholder(String placeholder) { this.placeholder = placeholder; }
  public String getRelationTable() { return relationTable; }
  public void setRelationTable(String relationTable) { this.relationTable = relationTable; }
  public List<String> getEnumOptions() { return enumOptions; }
  public void setEnumOptions(List<String> enumOptions) { this.enumOptions = enumOptions; }
  public List<RuleDef> getRules() { return rules; }
  public void setRules(List<RuleDef> rules) { this.rules = rules; }
  public List<ValidationDef> getValidations() { return validations; }
  public void setValidations(List<ValidationDef> validations) { this.validations = validations; }

  public static class RuleDef {
    private UUID ruleId;
    private String conditionField;
    private String conditionOperator;
    private String conditionValue;
    private String action;
    private Integer logicGroup;

    public RuleDef() {}
    public UUID getRuleId() { return ruleId; }
    public void setRuleId(UUID ruleId) { this.ruleId = ruleId; }
    public String getConditionField() { return conditionField; }
    public void setConditionField(String conditionField) { this.conditionField = conditionField; }
    public String getConditionOperator() { return conditionOperator; }
    public void setConditionOperator(String conditionOperator) { this.conditionOperator = conditionOperator; }
    public String getConditionValue() { return conditionValue; }
    public void setConditionValue(String conditionValue) { this.conditionValue = conditionValue; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public Integer getLogicGroup() { return logicGroup; }
    public void setLogicGroup(Integer logicGroup) { this.logicGroup = logicGroup; }
  }

  public static class ValidationDef {
    private UUID validationId;
    private String type;
    private String value;
    private String message;

    public ValidationDef() {}
    public UUID getValidationId() { return validationId; }
    public void setValidationId(UUID validationId) { this.validationId = validationId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
  }
}
