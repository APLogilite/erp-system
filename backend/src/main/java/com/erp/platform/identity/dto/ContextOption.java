package com.erp.platform.identity.dto;

import java.util.UUID;

public class ContextOption {

  private UUID id;
  private String type;
  private String code;
  private String name;
  private UUID parentId;

  public ContextOption() {}

  public ContextOption(UUID id, String type, String code, String name) {
    this.id = id;
    this.type = type;
    this.code = code;
    this.name = name;
  }

  public ContextOption(UUID id, String type, String code, String name, UUID parentId) {
    this.id = id;
    this.type = type;
    this.code = code;
    this.name = name;
    this.parentId = parentId;
  }

  public UUID getId() { return id; }
  public void setId(UUID id) { this.id = id; }
  public String getType() { return type; }
  public void setType(String type) { this.type = type; }
  public String getCode() { return code; }
  public void setCode(String code) { this.code = code; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public UUID getParentId() { return parentId; }
  public void setParentId(UUID parentId) { this.parentId = parentId; }
}
