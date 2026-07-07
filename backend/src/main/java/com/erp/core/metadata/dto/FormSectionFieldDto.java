package com.erp.core.metadata.dto;

import java.util.UUID;

public class FormSectionFieldDto {

  private UUID id;
  private UUID sectionId;
  private UUID fieldId;
  private Integer position;

  public FormSectionFieldDto() {}

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

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
