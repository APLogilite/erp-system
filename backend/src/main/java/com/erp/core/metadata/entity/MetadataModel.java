package com.erp.core.metadata.entity;

import com.erp.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.Map;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "sys_metadata_models")
public class MetadataModel extends BaseEntity {

  @Column(name = "name", nullable = false, unique = true, length = 100)
  private String name;

  @Column(name = "label", nullable = false, length = 100)
  private String label;

  @Column(name = "plural_label", nullable = false, length = 100)
  private String pluralLabel;

  @Column(name = "table_type", length = 20)
  private String tableType;

  @Column(name = "table_name", length = 100)
  private String tableName;

  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "definition", columnDefinition = "jsonb", nullable = false)
  private Map<String, Object> definition;

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

  public String getPluralLabel() {
    return pluralLabel;
  }

  public void setPluralLabel(String pluralLabel) {
    this.pluralLabel = pluralLabel;
  }

  public String getTableType() {
    return tableType;
  }

  public void setTableType(String tableType) {
    this.tableType = tableType;
  }

  public String getTableName() {
    return tableName;
  }

  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Map<String, Object> getDefinition() {
    return definition;
  }

  public void setDefinition(Map<String, Object> definition) {
    this.definition = definition;
  }
}
