package com.erp.core.metadata.dto;

import java.util.UUID;

public class FormTenantRoleCreateRequest {

  private UUID formId;
  private UUID tenantId;
  private UUID roleId;

  public FormTenantRoleCreateRequest() {}

  public UUID getFormId() {
    return formId;
  }

  public void setFormId(UUID formId) {
    this.formId = formId;
  }

  public UUID getTenantId() {
    return tenantId;
  }

  public void setTenantId(UUID tenantId) {
    this.tenantId = tenantId;
  }

  public UUID getRoleId() {
    return roleId;
  }

  public void setRoleId(UUID roleId) {
    this.roleId = roleId;
  }
}
