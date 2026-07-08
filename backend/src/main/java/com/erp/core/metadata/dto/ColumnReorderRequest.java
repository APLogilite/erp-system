package com.erp.core.metadata.dto;

import java.util.List;
import java.util.UUID;

public class ColumnReorderRequest {

  private List<UUID> columnIds;

  public ColumnReorderRequest() {}

  public List<UUID> getColumnIds() { return columnIds; }
  public void setColumnIds(List<UUID> columnIds) { this.columnIds = columnIds; }
}
