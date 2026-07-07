package com.erp.core.metadata.entity;

import com.erp.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.Map;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "sys_table_columns")
public class TableColumnEntity extends BaseEntity {

  @Column(name = "table_id", nullable = false)
  private java.util.UUID tableId;

  @Column(name = "code", nullable = false, length = 100)
  private String code;

  @Column(name = "label", nullable = false, length = 200)
  private String label;

  @Column(name = "type", nullable = false, length = 50)
  private String type;

  @Column(name = "required")
  private Boolean required = false;

  @Column(name = "default_value", columnDefinition = "TEXT")
  private String defaultValue;

  @Column(name = "max_length")
  private Integer maxLength;

  @Column(name = "precision")
  private Integer precision;

  @Column(name = "scale")
  private Integer scale;

  @Column(name = "relation_table", length = 100)
  private String relationTable;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "enum_options", columnDefinition = "jsonb")
  private Map<String, Object> enumOptions;

  @Column(name = "position", nullable = false)
  private Integer position;

  public java.util.UUID getTableId() {
    return tableId;
  }

  public void setTableId(java.util.UUID tableId) {
    this.tableId = tableId;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Boolean getRequired() {
    return required;
  }

  public void setRequired(Boolean required) {
    this.required = required;
  }

  public String getDefaultValue() {
    return defaultValue;
  }

  public void setDefaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
  }

  public Integer getMaxLength() {
    return maxLength;
  }

  public void setMaxLength(Integer maxLength) {
    this.maxLength = maxLength;
  }

  public Integer getPrecision() {
    return precision;
  }

  public void setPrecision(Integer precision) {
    this.precision = precision;
  }

  public Integer getScale() {
    return scale;
  }

  public void setScale(Integer scale) {
    this.scale = scale;
  }

  public String getRelationTable() {
    return relationTable;
  }

  public void setRelationTable(String relationTable) {
    this.relationTable = relationTable;
  }

  public Map<String, Object> getEnumOptions() {
    return enumOptions;
  }

  public void setEnumOptions(Map<String, Object> enumOptions) {
    this.enumOptions = enumOptions;
  }

  public Integer getPosition() {
    return position;
  }

  public void setPosition(Integer position) {
    this.position = position;
  }
}
