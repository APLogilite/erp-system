package com.erp.core.metadata.dto;

import java.util.List;
import java.util.UUID;

public class SubFormReorderRequest {
  private List<UUID> subFormIds;

  public SubFormReorderRequest() {}

  public List<UUID> getSubFormIds() { return subFormIds; }
  public void setSubFormIds(List<UUID> subFormIds) { this.subFormIds = subFormIds; }
}
