package com.erp.core.metadata.entity;

import com.erp.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "sys_form_section_fields")
public class FormSectionFieldEntity extends BaseEntity {

  @Column(name = "section_id", nullable = false)
  private java.util.UUID sectionId;

  @Column(name = "field_id", nullable = false)
  private java.util.UUID fieldId;

  @Column(name = "position")
  private Integer position;

  public java.util.UUID getSectionId() {
    return sectionId;
  }

  public void setSectionId(java.util.UUID sectionId) {
    this.sectionId = sectionId;
  }

  public java.util.UUID getFieldId() {
    return fieldId;
  }

  public void setFieldId(java.util.UUID fieldId) {
    this.fieldId = fieldId;
  }

  public Integer getPosition() {
    return position;
  }

  public void setPosition(Integer position) {
    this.position = position;
  }
}
