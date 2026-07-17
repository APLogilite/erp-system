package com.erp.core.runtime.dto.window;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a tab within a window definition response,
 * including its fields and associated table info.
 */
public class TabDefinitionResponse {

  private UUID id;
  private String name;
  private Integer seqNo;
  private Boolean isSingleRow;
  private String whereClause;
  private String parentColumn;
  private List<UUID> childTabIds = new ArrayList<>();
  private TableInfo table;
  private List<FieldDefinitionResponse> fields;

  public TabDefinitionResponse() {}

  public UUID getId() { return id; }
  public void setId(UUID id) { this.id = id; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public Integer getSeqNo() { return seqNo; }
  public void setSeqNo(Integer seqNo) { this.seqNo = seqNo; }
  public Boolean getIsSingleRow() { return isSingleRow; }
  public void setIsSingleRow(Boolean isSingleRow) { this.isSingleRow = isSingleRow; }
  public String getWhereClause() { return whereClause; }
  public void setWhereClause(String whereClause) { this.whereClause = whereClause; }
  public String getParentColumn() { return parentColumn; }
  public void setParentColumn(String parentColumn) { this.parentColumn = parentColumn; }
  public List<UUID> getChildTabIds() { return childTabIds; }
  public void setChildTabIds(List<UUID> childTabIds) { this.childTabIds = childTabIds; }
  public TableInfo getTable() { return table; }
  public void setTable(TableInfo table) { this.table = table; }
  public List<FieldDefinitionResponse> getFields() { return fields; }
  public void setFields(List<FieldDefinitionResponse> fields) { this.fields = fields; }

  /**
   * Inner DTO for table metadata within a tab.
   */
  public static class TableInfo {
    private UUID id;
    private String name;
    private String label;

    public TableInfo() {}

    public TableInfo(UUID id, String name, String label) {
      this.id = id;
      this.name = name;
      this.label = label;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
  }
}
