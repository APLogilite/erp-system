package com.erp.platform.identity.dto;

import java.util.UUID;

public class ContextSwitchRequest {

  private UUID tenantId;
  private UUID organizationId;
  private UUID companyId;
  private UUID branchId;
  private String roleCode;

  public UUID getTenantId() { return tenantId; }
  public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
  public UUID getOrganizationId() { return organizationId; }
  public void setOrganizationId(UUID organizationId) { this.organizationId = organizationId; }
  public UUID getCompanyId() { return companyId; }
  public void setCompanyId(UUID companyId) { this.companyId = companyId; }
  public UUID getBranchId() { return branchId; }
  public void setBranchId(UUID branchId) { this.branchId = branchId; }
  public String getRoleCode() { return roleCode; }
  public void setRoleCode(String roleCode) { this.roleCode = roleCode; }
}
