package com.erp.core.security.dto;

import com.erp.core.security.enums.PermissionLevel;

public class PermissionMetadataDto {

  private String code;
  private String resource;
  private PermissionLevel permissionLevel;
  private String expression;

  public PermissionMetadataDto() {}

  public PermissionMetadataDto(String code, String resource, PermissionLevel permissionLevel, String expression) {
    this.code = code;
    this.resource = resource;
    this.permissionLevel = permissionLevel;
    this.expression = expression;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getResource() {
    return resource;
  }

  public void setResource(String resource) {
    this.resource = resource;
  }

  public PermissionLevel getPermissionLevel() {
    return permissionLevel;
  }

  public void setPermissionLevel(PermissionLevel permissionLevel) {
    this.permissionLevel = permissionLevel;
  }

  public String getExpression() {
    return expression;
  }

  public void setExpression(String expression) {
    this.expression = expression;
  }
}
