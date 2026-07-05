package com.erp.core.metadata.entity;

import com.erp.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.Map;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "sys_metadata_views")
public class MetadataView extends BaseEntity {

  @Column(name = "name", nullable = false, unique = true, length = 100)
  private String name;

  @Column(name = "model_name", nullable = false, length = 100)
  private String modelName;

  @Column(name = "type", nullable = false, length = 50)
  private String type; // list, form, search

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "definition", columnDefinition = "jsonb", nullable = false)
  private Map<String, Object> definition;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getModelName() {
    return modelName;
  }

  public void setModelName(String modelName) {
    this.modelName = modelName;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Map<String, Object> getDefinition() {
    return definition;
  }

  public void setDefinition(Map<String, Object> definition) {
    this.definition = definition;
  }
}
