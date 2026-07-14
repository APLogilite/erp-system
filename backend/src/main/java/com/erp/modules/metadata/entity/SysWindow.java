package com.erp.modules.metadata.entity;

import com.erp.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;

/**
 * sys_window — Window definitions.
 * A window is the top-level form concept — what users see when they open a menu item.
 */
@Entity
@Table(name = "sys_window")
public class SysWindow extends BaseEntity {

  @Column(nullable = false, unique = true, length = 100)
  private String name;

  @Column(name = "table_id", nullable = false)
  private UUID tableId;

  @Column(columnDefinition = "TEXT")
  private String description;

  // --- Getters and Setters ---

  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public UUID getTableId() { return tableId; }
  public void setTableId(UUID tableId) { this.tableId = tableId; }

  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }
}
