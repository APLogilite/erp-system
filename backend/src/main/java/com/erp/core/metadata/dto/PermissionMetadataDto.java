package com.erp.core.metadata.dto;

public class PermissionMetadataDto {

  public enum PermissionType {
    READ, CREATE, UPDATE, DELETE, EXECUTE, ADMIN
  }

  private String code;
  private String resource;
  private PermissionType permissionType;

  public PermissionMetadataDto() {}

  public PermissionMetadataDto(String code, String resource, PermissionType permissionType) {
    this.code = code;
    this.resource = resource;
    this.permissionType = permissionType;
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

  public PermissionType getPermissionType() {
    return permissionType;
  }

  public void setPermissionType(PermissionType permissionType) {
    this.permissionType = permissionType;
  }
}
