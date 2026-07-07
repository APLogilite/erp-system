package com.erp.core.metadata.dto;

import java.util.UUID;

public class FormSectionFieldCreateRequest {

  private UUID sectionId;
  private UUID fieldId;
  private Integer position;

  public FormSectionFieldCreateRequest() {}

  public UUID getSectionId() {
    return sectionId;
  }

  public void setSectionId(UUID sectionId) {
    this.sectionId = sectionId;
  }

  public UUID getFieldId() {
    return fieldId;
  }

  public void setFieldId(UUID fieldId) {
    this.fieldId = fieldId;
  }

  public Integer getPosition() {
    return position;
  }

  public void setPosition(Integer position) {
    this.position = position;
  }
}
