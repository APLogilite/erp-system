package com.erp.modules.metadata.entity;

import com.erp.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;

/**
 * sys_column — Column definitions.
 * Maps a column to its parent sys_table with type metadata for UI rendering.
 */
@Entity
@Table(name = "sys_column")
public class SysColumn extends BaseEntity {

  @Column(name = "table_id", nullable = false)
  private UUID tableId;

  @Column(nullable = false, length = 100)
  private String code;

  @Column(nullable = false, length = 100)
  private String label;

  @Column(nullable = false, length = 50)
  private String type;

  @Column(nullable = false)
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

  @Column(name = "enum_options", columnDefinition = "jsonb")
  private String enumOptions;

  @Column
  private Integer position;

  @Column(name = "filter_where_clause", columnDefinition = "TEXT")
  private String filterWhereClause;

  // --- Getters and Setters ---

  public UUID getTableId() { return tableId; }
  public void setTableId(UUID tableId) { this.tableId = tableId; }

  public String getCode() { return code; }
  public void setCode(String code) { this.code = code; }

  public String getLabel() { return label; }
  public void setLabel(String label) { this.label = label; }

  public String getType() { return type; }
  public void setType(String type) { this.type = type; }

  public Boolean getRequired() { return required; }
  public void setRequired(Boolean required) { this.required = required; }

  public String getDefaultValue() { return defaultValue; }
  public void setDefaultValue(String defaultValue) { this.defaultValue = defaultValue; }

  public Integer getMaxLength() { return maxLength; }
  public void setMaxLength(Integer maxLength) { this.maxLength = maxLength; }

  public Integer getPrecision() { return precision; }
  public void setPrecision(Integer precision) { this.precision = precision; }

  public Integer getScale() { return scale; }
  public void setScale(Integer scale) { this.scale = scale; }

  public String getRelationTable() { return relationTable; }
  public void setRelationTable(String relationTable) { this.relationTable = relationTable; }

  public String getEnumOptions() { return enumOptions; }
  public void setEnumOptions(String enumOptions) { this.enumOptions = enumOptions; }

  public Integer getPosition() { return position; }
  public void setPosition(Integer position) { this.position = position; }

  public String getFilterWhereClause() { return filterWhereClause; }
  public void setFilterWhereClause(String filterWhereClause) { this.filterWhereClause = filterWhereClause; }
}
