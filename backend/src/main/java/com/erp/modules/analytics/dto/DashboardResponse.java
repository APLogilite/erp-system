package com.erp.modules.analytics.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class DashboardResponse {
  private UUID id;
  private String dashboardCode;
  private String name;
  private String description;
  private String layout;
  private Boolean isDefault;
  private String roles;
  private Boolean isActive;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private List<WidgetResponse> widgets;

  public UUID getId() { return id; }
  public void setId(UUID id) { this.id = id; }
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
  public Boolean getIsActive() { return isActive; }
  public void setIsActive(Boolean isActive) { this.isActive = isActive; }
  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
  public LocalDateTime getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
  public List<WidgetResponse> getWidgets() { return widgets; }
  public void setWidgets(List<WidgetResponse> widgets) { this.widgets = widgets; }
}
