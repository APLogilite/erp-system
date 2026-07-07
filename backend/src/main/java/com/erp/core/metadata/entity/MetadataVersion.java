package com.erp.core.metadata.entity;

import com.erp.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.Map;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "sys_metadata_versions")
public class MetadataVersion extends BaseEntity {

  @Column(name = "version", nullable = false)
  private Integer version;

  @Column(name = "table_id")
  private java.util.UUID tableId;

  @Column(name = "description", length = 500)
  private String description;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "definition_snapshot", columnDefinition = "jsonb")
  private Map<String, Object> definitionSnapshot;

  @Column(name = "changed_by")
  private java.util.UUID changedBy;

  @Column(name = "is_active", nullable = false)
  private Boolean isActive = false;

  public Integer getVersion() {
    return version;
  }

  public void setVersion(Integer version) {
    this.version = version;
  }

  public java.util.UUID getTableId() {
    return tableId;
  }

  public void setTableId(java.util.UUID tableId) {
    this.tableId = tableId;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Map<String, Object> getDefinitionSnapshot() {
    return definitionSnapshot;
  }

  public void setDefinitionSnapshot(Map<String, Object> definitionSnapshot) {
    this.definitionSnapshot = definitionSnapshot;
  }

  public java.util.UUID getChangedBy() {
    return changedBy;
  }

  public void setChangedBy(java.util.UUID changedBy) {
    this.changedBy = changedBy;
  }

  public Boolean getActive() {
    return isActive;
  }

  public void setActive(Boolean active) {
    isActive = active;
  }
}
