package com.erp.core.metadata.dto;

import java.util.List;
import java.util.UUID;

public class TenantRoleRequest {
  private List<UUID> roleIds;

  public TenantRoleRequest() {}

  public List<UUID> getRoleIds() { return roleIds; }
  public void setRoleIds(List<UUID> roleIds) { this.roleIds = roleIds; }
}
