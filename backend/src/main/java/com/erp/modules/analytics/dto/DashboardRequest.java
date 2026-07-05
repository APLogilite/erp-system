package com.erp.modules.analytics.dto;

import java.util.List;
import java.util.Map;

public class DashboardRequest {
  private String dashboardCode;
  private String name;
  private String description;
  private String layout;
  private Boolean isDefault;
  private String roles;
  private List<WidgetRequest> widgets;

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
  public List<WidgetRequest> getWidgets() { return widgets; }
  public void setWidgets(List<WidgetRequest> widgets) { this.widgets = widgets; }

  public static class WidgetRequest {
    private String widgetType;
    private String title;
    private String widgetConfig;
    private Integer positionX;
    private Integer positionY;
    private Integer width;
    private Integer height;
    private String dataSource;
    private Integer refreshInterval;

    public String getWidgetType() { return widgetType; }
    public void setWidgetType(String widgetType) { this.widgetType = widgetType; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getWidgetConfig() { return widgetConfig; }
    public void setWidgetConfig(String widgetConfig) { this.widgetConfig = widgetConfig; }
    public Integer getPositionX() { return positionX; }
    public void setPositionX(Integer positionX) { this.positionX = positionX; }
    public Integer getPositionY() { return positionY; }
    public void setPositionY(Integer positionY) { this.positionY = positionY; }
    public Integer getWidth() { return width; }
    public void setWidth(Integer width) { this.width = width; }
    public Integer getHeight() { return height; }
    public void setHeight(Integer height) { this.height = height; }
    public String getDataSource() { return dataSource; }
    public void setDataSource(String dataSource) { this.dataSource = dataSource; }
    public Integer getRefreshInterval() { return refreshInterval; }
    public void setRefreshInterval(Integer refreshInterval) { this.refreshInterval = refreshInterval; }
  }
}
