package com.erp.core.layout.entity;

import com.erp.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;

/**
 * sys_tab — Tab definitions.
 * A tab represents a sub-section within a window, like "Header" and "Lines" tabs.
 */
@Entity
@Table(name = "sys_tab")
public class SysTab extends BaseEntity {

  @Column(name = "window_id", nullable = false)
  private UUID windowId;

  @Column(nullable = false, length = 100)
  private String name;

  @Column(name = "table_id", nullable = false)
  private UUID tableId;

  @Column(name = "seq_no", nullable = false)
  private Integer seqNo = 10;

  @Column(name = "is_single_row")
  private Boolean isSingleRow = false;

  @Column(name = "where_clause", columnDefinition = "TEXT")
  private String whereClause;

  @Column(name = "parent_link_column_id")
  private UUID parentLinkColumn_ID;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_link_column_id", insertable = false, updatable = false)
  private SysColumn parentLinkColumn;

  // --- Getters and Setters ---

  public UUID getWindowId() { return windowId; }
  public void setWindowId(UUID windowId) { this.windowId = windowId; }

  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public UUID getTableId() { return tableId; }
  public void setTableId(UUID tableId) { this.tableId = tableId; }

  public Integer getSeqNo() { return seqNo; }
  public void setSeqNo(Integer seqNo) { this.seqNo = seqNo; }

  public Boolean getIsSingleRow() { return isSingleRow; }
  public void setIsSingleRow(Boolean isSingleRow) { this.isSingleRow = isSingleRow; }

  public String getWhereClause() { return whereClause; }
  public void setWhereClause(String whereClause) { this.whereClause = whereClause; }

  public UUID getParentLinkColumn_ID() { return parentLinkColumn_ID; }
  public void setParentLinkColumn_ID(UUID parentLinkColumn_ID) { this.parentLinkColumn_ID = parentLinkColumn_ID; }

  public SysColumn getParentLinkColumn() { return parentLinkColumn; }
  public void setParentLinkColumn(SysColumn parentLinkColumn) { this.parentLinkColumn = parentLinkColumn; }
}
