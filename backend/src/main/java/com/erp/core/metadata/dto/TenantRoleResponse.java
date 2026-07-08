package com.erp.core.metadata.dto;

import java.util.List;
import java.util.UUID;

public class TenantRoleResponse {
  private UUID formId;
  private UUID tenantId;
  private List<UUID> roleIds;

  public TenantRoleResponse() {}

  public UUID getFormId() { return formId; }
  public void setFormId(UUID formId) { this.formId = formId; }
  public UUID getTenantId() { return tenantId; }
  public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
  public List<UUID> getRoleIds() { return roleIds; }
  public void setRoleIds(List<UUID> roleIds) { this.roleIds = roleIds; }
}
