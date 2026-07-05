package com.erp.modules.analytics.entity;

import com.erp.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table(name = "dashboards")
public class Dashboard extends BaseEntity {

  @Column(name = "dashboard_code", nullable = false, unique = true)
  private String dashboardCode;

  @Column(nullable = false)
  private String name;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(columnDefinition = "TEXT")
  private String layout;

  @Column(name = "is_default")
  private Boolean isDefault = false;

  @Column
  private String roles;

  @Lob
  @Column(name = "widget_config", columnDefinition = "TEXT")
  private String widgetConfig;

  public String getDashboardCode() { return dashboardCode; }
  public void setDashboardCode(String dashboardCode) { this.dashboardCode = dashboardCode; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }
  public String getLayout() { return layout; }
  public void setLayout(String layout) { this.layout = layout; }
  public Boolean getIsDefault() { return isDefault; }
  public void setIsDefault(Boolean isDefault) { this.isDefault = isDefault; }
  public String getRoles() { return roles; }
  public void setRoles(String roles) { this.roles = roles; }
  public String getWidgetConfig() { return widgetConfig; }
  public void setWidgetConfig(String widgetConfig) { this.widgetConfig = widgetConfig; }
}
