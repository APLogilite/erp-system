package com.erp.core.runtime.dto;

import java.util.UUID;

public class BreadcrumbEntry {
  private String formCode;
  private UUID recordId;
  private String label;

  public BreadcrumbEntry() {}
  public BreadcrumbEntry(String formCode, UUID recordId, String label) {
    this.formCode = formCode; this.recordId = recordId; this.label = label;
  }

  public String getFormCode() { return formCode; }
  public void setFormCode(String formCode) { this.formCode = formCode; }
  public UUID getRecordId() { return recordId; }
  public void setRecordId(UUID recordId) { this.recordId = recordId; }
  public String getLabel() { return label; }
  public void setLabel(String label) { this.label = label; }
}
