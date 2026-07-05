package com.erp.platform.identity.dto;

import java.util.List;
import java.util.UUID;

/**
 * RuntimeContext represents the active ERP session context.
 *
 * It is NOT a database entity — it is attached to every authenticated
 * request via a request-scoped bean or security context holder.
 *
 * Separation of concerns:
 *   Authentication  — Who are you?      (user, tenant)
 *   Authorization   — What can you do?   (roles, permissions)
 *   Context         — Where are you?     (org, company, branch, preferences)
 */
public class RuntimeContext {

  private UUID userId;
  private String username;
  private String email;
  private String displayName;

  private UUID tenantId;
  private String tenantCode;
  private String tenantName;

  private UUID organizationId;
  private String organizationCode;
  private String organizationName;

  private UUID companyId;
  private String companyCode;
  private String companyName;

  private UUID branchId;
  private String branchCode;
  private String branchName;

  private UUID departmentId;
  private String departmentCode;
  private String departmentName;

  private List<String> roles;
  private List<String> permissions;

  private String language;
  private String timezone;
  private String currency;
  private String dateFormat;
  private String numberFormat;
  private String theme;

  private String ipAddress;
  private String sessionId;

  public UUID getUserId() { return userId; }
  public void setUserId(UUID userId) { this.userId = userId; }
  public String getUsername() { return username; }
  public void setUsername(String username) { this.username = username; }
  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }
  public String getDisplayName() { return displayName; }
  public void setDisplayName(String displayName) { this.displayName = displayName; }

  public UUID getTenantId() { return tenantId; }
  public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
  public String getTenantCode() { return tenantCode; }
  public void setTenantCode(String tenantCode) { this.tenantCode = tenantCode; }
  public String getTenantName() { return tenantName; }
  public void setTenantName(String tenantName) { this.tenantName = tenantName; }

  public UUID getOrganizationId() { return organizationId; }
  public void setOrganizationId(UUID organizationId) { this.organizationId = organizationId; }
  public String getOrganizationCode() { return organizationCode; }
  public void setOrganizationCode(String organizationCode) { this.organizationCode = organizationCode; }
  public String getOrganizationName() { return organizationName; }
  public void setOrganizationName(String organizationName) { this.organizationName = organizationName; }

  public UUID getCompanyId() { return companyId; }
  public void setCompanyId(UUID companyId) { this.companyId = companyId; }
  public String getCompanyCode() { return companyCode; }
  public void setCompanyCode(String companyCode) { this.companyCode = companyCode; }
  public String getCompanyName() { return companyName; }
  public void setCompanyName(String companyName) { this.companyName = companyName; }

  public UUID getBranchId() { return branchId; }
  public void setBranchId(UUID branchId) { this.branchId = branchId; }
  public String getBranchCode() { return branchCode; }
  public void setBranchCode(String branchCode) { this.branchCode = branchCode; }
  public String getBranchName() { return branchName; }
  public void setBranchName(String branchName) { this.branchName = branchName; }

  public UUID getDepartmentId() { return departmentId; }
  public void setDepartmentId(UUID departmentId) { this.departmentId = departmentId; }
  public String getDepartmentCode() { return departmentCode; }
  public void setDepartmentCode(String departmentCode) { this.departmentCode = departmentCode; }
  public String getDepartmentName() { return departmentName; }
  public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

  public List<String> getRoles() { return roles; }
  public void setRoles(List<String> roles) { this.roles = roles; }
  public List<String> getPermissions() { return permissions; }
  public void setPermissions(List<String> permissions) { this.permissions = permissions; }

  public String getLanguage() { return language; }
  public void setLanguage(String language) { this.language = language; }
  public String getTimezone() { return timezone; }
  public void setTimezone(String timezone) { this.timezone = timezone; }
  public String getCurrency() { return currency; }
  public void setCurrency(String currency) { this.currency = currency; }
  public String getDateFormat() { return dateFormat; }
  public void setDateFormat(String dateFormat) { this.dateFormat = dateFormat; }
  public String getNumberFormat() { return numberFormat; }
  public void setNumberFormat(String numberFormat) { this.numberFormat = numberFormat; }
  public String getTheme() { return theme; }
  public void setTheme(String theme) { this.theme = theme; }

  public String getIpAddress() { return ipAddress; }
  public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
  public String getSessionId() { return sessionId; }
  public void setSessionId(String sessionId) { this.sessionId = sessionId; }
}
