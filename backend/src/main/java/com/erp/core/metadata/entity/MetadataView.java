package com.erp.core.metadata.entity;

import com.erp.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.Map;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "sys_metadata_views")
public class MetadataView extends BaseEntity {

  @Column(name = "name", nullable = false, unique = true, length = 100)
  private String name;

  @Column(name = "model_name", nullable = false, length = 100)
  private String modelName;

  @Column(name = "type", nullable = false, length = 50)
  private String type; // list, form, search

  @Column(name = "scope", length = 20)
  private String scope;

  @Column(name = "tenant_id")
  private java.util.UUID tenantId;

  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

  @Column(name = "where_clause_field", length = 100)
  private String whereClauseField;

  @Column(name = "where_clause_operator", length = 50)
  private String whereClauseOperator;

  @Column(name = "where_clause_value", length = 255)
  private String whereClauseValue;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "definition", columnDefinition = "jsonb", nullable = false)
  private Map<String, Object> definition;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
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

  public java.util.UUID getTenantId() {
    return tenantId;
  }

  public void setTenantId(java.util.UUID tenantId) {
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

  public Map<String, Object> getDefinition() {
    return definition;
  }

  public void setDefinition(Map<String, Object> definition) {
    this.definition = definition;
  }
}
