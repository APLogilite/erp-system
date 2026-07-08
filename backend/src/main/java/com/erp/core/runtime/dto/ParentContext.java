package com.erp.core.runtime.dto;

import java.util.UUID;

public class ParentContext {
  private String formCode;
  private UUID recordId;
  private String label;
  private String relationColumn;

  public ParentContext() {}

  public String getFormCode() { return formCode; }
  public void setFormCode(String formCode) { this.formCode = formCode; }
  public UUID getRecordId() { return recordId; }
  public void setRecordId(UUID recordId) { this.recordId = recordId; }
  public String getLabel() { return label; }
  public void setLabel(String label) { this.label = label; }
  public String getRelationColumn() { return relationColumn; }
  public void setRelationColumn(String relationColumn) { this.relationColumn = relationColumn; }
}
