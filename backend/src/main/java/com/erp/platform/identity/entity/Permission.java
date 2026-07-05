package com.erp.platform.identity.entity;

import com.erp.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "identity_permissions")
public class Permission extends BaseEntity {

  @Column(name = "code", nullable = false, unique = true, length = 100)
  private String code;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

  @Column(name = "resource_type", nullable = false, length = 50)
  private String resourceType;

  @Column(name = "resource", nullable = false, length = 100)
  private String resource;

  @Column(name = "action", nullable = false, length = 50)
  private String action;

  @Column(name = "is_system")
  private Boolean isSystem = false;

  @Column(name = "module", length = 50)
  private String module;

  public String getCode() { return code; }
  public void setCode(String code) { this.code = code; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }
  public String getResourceType() { return resourceType; }
  public void setResourceType(String resourceType) { this.resourceType = resourceType; }
  public String getResource() { return resource; }
  public void setResource(String resource) { this.resource = resource; }
  public String getAction() { return action; }
  public void setAction(String action) { this.action = action; }
  public Boolean getIsSystem() { return isSystem; }
  public void setIsSystem(Boolean isSystem) { this.isSystem = isSystem; }
  public String getModule() { return module; }
  public void setModule(String module) { this.module = module; }
}
