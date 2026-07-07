package com.erp.core.metadata.entity;

import com.erp.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "sys_form_tenant_role")
public class FormTenantRoleEntity extends BaseEntity {

  @Column(name = "form_id", nullable = false)
  private java.util.UUID formId;

  @Column(name = "tenant_id", nullable = false)
  private java.util.UUID tenantId;

  @Column(name = "role_id", nullable = false)
  private java.util.UUID roleId;

  public java.util.UUID getFormId() {
    return formId;
  }

  public void setFormId(java.util.UUID formId) {
    this.formId = formId;
  }

  public java.util.UUID getTenantId() {
    return tenantId;
  }

  public void setTenantId(java.util.UUID tenantId) {
    this.tenantId = tenantId;
  }

  public java.util.UUID getRoleId() {
    return roleId;
  }

  public void setRoleId(java.util.UUID roleId) {
    this.roleId = roleId;
  }
}
