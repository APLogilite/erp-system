package com.erp.modules.metadata.entity;

import com.erp.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;

/**
 * sys_menu — Hierarchical menu entries.
 * Organizes windows into collapsible groups for user navigation.
 */
@Entity
@Table(name = "sys_menu")
public class SysMenu extends BaseEntity {

  @Column(nullable = false, length = 100)
  private String name;

  @Column(nullable = false, length = 20)
  private String type;

  @Column(name = "parent_id")
  private UUID parentId;

  @Column(name = "window_id")
  private UUID windowId;

  @Column(name = "seq_no", nullable = false)
  private Integer seqNo = 10;

  @Column(length = 100)
  private String icon;

  // --- Getters and Setters ---

  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public String getType() { return type; }
  public void setType(String type) { this.type = type; }

  public UUID getParentId() { return parentId; }
  public void setParentId(UUID parentId) { this.parentId = parentId; }

  public UUID getWindowId() { return windowId; }
  public void setWindowId(UUID windowId) { this.windowId = windowId; }

  public Integer getSeqNo() { return seqNo; }
  public void setSeqNo(Integer seqNo) { this.seqNo = seqNo; }

  public String getIcon() { return icon; }
  public void setIcon(String icon) { this.icon = icon; }
}
