package com.erp.modules.metadata.entity;

import com.erp.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;

/**
 * sys_window_access — Role-based window access control.
 * Controls which roles can access which windows.
 */
@Entity
@Table(name = "sys_window_access")
public class SysWindowAccess extends BaseEntity {

  @Column(name = "window_id", nullable = false)
  private UUID windowId;

  @Column(name = "tenant_id")
  private UUID tenantId;

  @Column(name = "role_id", nullable = false)
  private UUID roleId;

  // --- Getters and Setters ---

  public UUID getWindowId() { return windowId; }
  public void setWindowId(UUID windowId) { this.windowId = windowId; }

  public UUID getTenantId() { return tenantId; }
  public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }

  public UUID getRoleId() { return roleId; }
  public void setRoleId(UUID roleId) { this.roleId = roleId; }
}
