package com.erp.core.layout.entity;

import com.erp.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;

/**
 * sys_window_field — Field definitions.
 * Maps a column to a position within a tab, with display and read-only settings.
 */
@Entity
@Table(name = "sys_window_field")
public class SysWindowField extends BaseEntity {

  @Column(name = "tab_id", nullable = false)
  private UUID tabId;

  @Column(name = "column_id", nullable = false)
  private UUID columnId;

  @Column(name = "seq_no", nullable = false)
  private Integer seqNo = 10;

  @Column(name = "is_same_line")
  private Boolean isSameLine = false;

  @Column(name = "num_lines")
  private Integer numLines = 1;

  @Column(name = "column_width")
  private Integer columnWidth = 12;

  @Column(name = "is_displayed")
  private Boolean isDisplayed = true;

  @Column(name = "is_readonly")
  private Boolean isReadonly = false;

  @Column(name = "is_mandatory")
  private Boolean isMandatory = false;

  @Column(name = "display_logic", columnDefinition = "TEXT")
  private String displayLogic;

  @Column(name = "readonly_logic", columnDefinition = "TEXT")
  private String readonlyLogic;

  @Column(name = "default_value", columnDefinition = "TEXT")
  private String defaultValue;

  @Column(name = "label_override", length = 200)
  private String labelOverride;

  @Column(name = "filter_where_clause", columnDefinition = "TEXT")
  private String filterWhereClause;

  // --- Getters and Setters ---

  public UUID getTabId() { return tabId; }
  public void setTabId(UUID tabId) { this.tabId = tabId; }

  public UUID getColumnId() { return columnId; }
  public void setColumnId(UUID columnId) { this.columnId = columnId; }

  public Integer getSeqNo() { return seqNo; }
  public void setSeqNo(Integer seqNo) { this.seqNo = seqNo; }

  public Boolean getIsSameLine() { return isSameLine; }
  public void setIsSameLine(Boolean isSameLine) { this.isSameLine = isSameLine; }

  public Integer getNumLines() { return numLines; }
  public void setNumLines(Integer numLines) { this.numLines = numLines; }

  public Integer getColumnWidth() { return columnWidth; }
  public void setColumnWidth(Integer columnWidth) { this.columnWidth = columnWidth; }

  public Boolean getIsDisplayed() { return isDisplayed; }
  public void setIsDisplayed(Boolean isDisplayed) { this.isDisplayed = isDisplayed; }

  public Boolean getIsReadonly() { return isReadonly; }
  public void setIsReadonly(Boolean isReadonly) { this.isReadonly = isReadonly; }

  public Boolean getIsMandatory() { return isMandatory; }
  public void setIsMandatory(Boolean isMandatory) { this.isMandatory = isMandatory; }

  public String getDisplayLogic() { return displayLogic; }
  public void setDisplayLogic(String displayLogic) { this.displayLogic = displayLogic; }

  public String getReadonlyLogic() { return readonlyLogic; }
  public void setReadonlyLogic(String readonlyLogic) { this.readonlyLogic = readonlyLogic; }

  public String getDefaultValue() { return defaultValue; }
  public void setDefaultValue(String defaultValue) { this.defaultValue = defaultValue; }

  public String getLabelOverride() { return labelOverride; }
  public void setLabelOverride(String labelOverride) { this.labelOverride = labelOverride; }

  public String getFilterWhereClause() { return filterWhereClause; }
  public void setFilterWhereClause(String filterWhereClause) { this.filterWhereClause = filterWhereClause; }
}
