package com.erp.core.relation.dto;

import java.util.UUID;

public class LookupResultDto {

  private UUID id;
  private String value;
  private String label;

  public LookupResultDto() {}

  public LookupResultDto(UUID id, String value, String label) {
    this.id = id;
    this.value = value;
    this.label = label;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }
}
