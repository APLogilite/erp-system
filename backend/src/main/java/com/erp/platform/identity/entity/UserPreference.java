package com.erp.platform.identity.entity;

import com.erp.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "identity_user_preferences")
public class UserPreference extends BaseEntity {

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false, unique = true)
  private UserAccount user;

  @Column(name = "language", length = 10)
  private String language = "en";

  @Column(name = "timezone", length = 50)
  private String timezone = "UTC";

  @Column(name = "date_format", length = 20)
  private String dateFormat = "YYYY-MM-DD";

  @Column(name = "time_format", length = 20)
  private String timeFormat = "HH:mm";

  @Column(name = "number_format", length = 20)
  private String numberFormat = "#,##0.00";

  @Column(name = "currency", length = 3)
  private String currency = "USD";

  @Column(name = "theme", length = 20)
  private String theme = "light";

  @Column(name = "notifications_enabled")
  private Boolean notificationsEnabled = true;

  @Column(name = "items_per_page")
  private Integer itemsPerPage = 25;

  @Column(name = "active_tenant_id")
  private java.util.UUID activeTenantId;

  @Column(name = "active_organization_id")
  private java.util.UUID activeOrganizationId;

  @Column(name = "active_company_id")
  private java.util.UUID activeCompanyId;

  @Column(name = "active_branch_id")
  private java.util.UUID activeBranchId;

  @Column(name = "active_department_id")
  private java.util.UUID activeDepartmentId;

  @Column(name = "active_role_code", length = 50)
  private String activeRoleCode;

  public UserAccount getUser() { return user; }
  public void setUser(UserAccount user) { this.user = user; }
  public String getLanguage() { return language; }
  public void setLanguage(String language) { this.language = language; }
  public String getTimezone() { return timezone; }
  public void setTimezone(String timezone) { this.timezone = timezone; }
  public String getDateFormat() { return dateFormat; }
  public void setDateFormat(String dateFormat) { this.dateFormat = dateFormat; }
  public String getTimeFormat() { return timeFormat; }
  public void setTimeFormat(String timeFormat) { this.timeFormat = timeFormat; }
  public String getNumberFormat() { return numberFormat; }
  public void setNumberFormat(String numberFormat) { this.numberFormat = numberFormat; }
  public String getCurrency() { return currency; }
  public void setCurrency(String currency) { this.currency = currency; }
  public String getTheme() { return theme; }
  public void setTheme(String theme) { this.theme = theme; }
  public Boolean getNotificationsEnabled() { return notificationsEnabled; }
  public void setNotificationsEnabled(Boolean notificationsEnabled) { this.notificationsEnabled = notificationsEnabled; }
  public Integer getItemsPerPage() { return itemsPerPage; }
  public void setItemsPerPage(Integer itemsPerPage) { this.itemsPerPage = itemsPerPage; }
  public java.util.UUID getActiveTenantId() { return activeTenantId; }
  public void setActiveTenantId(java.util.UUID activeTenantId) { this.activeTenantId = activeTenantId; }
  public java.util.UUID getActiveOrganizationId() { return activeOrganizationId; }
  public void setActiveOrganizationId(java.util.UUID activeOrganizationId) { this.activeOrganizationId = activeOrganizationId; }
  public java.util.UUID getActiveCompanyId() { return activeCompanyId; }
  public void setActiveCompanyId(java.util.UUID activeCompanyId) { this.activeCompanyId = activeCompanyId; }
  public java.util.UUID getActiveBranchId() { return activeBranchId; }
  public void setActiveBranchId(java.util.UUID activeBranchId) { this.activeBranchId = activeBranchId; }
  public java.util.UUID getActiveDepartmentId() { return activeDepartmentId; }
  public void setActiveDepartmentId(java.util.UUID activeDepartmentId) { this.activeDepartmentId = activeDepartmentId; }
  public String getActiveRoleCode() { return activeRoleCode; }
  public void setActiveRoleCode(String activeRoleCode) { this.activeRoleCode = activeRoleCode; }
}
