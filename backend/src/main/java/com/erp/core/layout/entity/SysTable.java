package com.erp.core.layout.entity;

import com.erp.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * sys_table — Table definitions.
 * Represents a physical PostgreSQL table registered in the metadata layer.
 */
@Entity
@Table(name = "sys_table")
public class SysTable extends BaseEntity {

  @Column(nullable = false, unique = true, length = 100)
  private String name;

  @Column(nullable = false, length = 100)
  private String label;

  @Column(name = "plural_label", length = 100)
  private String pluralLabel;

  @Column(name = "table_type", nullable = false, length = 20)
  private String tableType = "dynamic";

  @Column(name = "table_name", nullable = false, length = 100)
  private String tableName;

  @Column(columnDefinition = "TEXT")
  private String description;

  // --- Getters and Setters ---

  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public String getLabel() { return label; }
  public void setLabel(String label) { this.label = label; }

  public String getPluralLabel() { return pluralLabel; }
  public void setPluralLabel(String pluralLabel) { this.pluralLabel = pluralLabel; }

  public String getTableType() { return tableType; }
  public void setTableType(String tableType) { this.tableType = tableType; }

  public String getTableName() { return tableName; }
  public void setTableName(String tableName) { this.tableName = tableName; }

  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }
}
