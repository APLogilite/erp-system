package com.erp.core.metadata.dto;

import java.util.List;
import java.util.UUID;

public class SectionFieldAssignmentRequest {

  private List<UUID> fieldIds;

  public SectionFieldAssignmentRequest() {}

  public List<UUID> getFieldIds() {
    return fieldIds;
  }

  public void setFieldIds(List<UUID> fieldIds) {
    this.fieldIds = fieldIds;
  }
}
