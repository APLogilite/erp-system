package com.erp.core.metadata.entity;

import com.erp.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "sys_metadata_versions")
public class MetadataVersion extends BaseEntity {

  @Column(name = "version", nullable = false, unique = true)
  private Integer version;

  @Column(name = "description", length = 255)
  private String description;

  @Column(name = "is_active", nullable = false)
  private Boolean isActive = false;

  public Integer getVersion() {
    return version;
  }

  public void setVersion(Integer version) {
    this.version = version;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Boolean getActive() {
    return isActive;
  }

  public void setActive(Boolean active) {
    isActive = active;
  }
}
