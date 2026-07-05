package com.erp.modules.analytics.dto;

import java.util.UUID;

public class WidgetResponse {
  private UUID id;
  private UUID dashboardId;
  private String widgetType;
  private String title;
  private String widgetConfig;
  private Integer positionX;
  private Integer positionY;
  private Integer width;
  private Integer height;
  private String dataSource;
  private Integer refreshInterval;

  public UUID getId() { return id; }
  public void setId(UUID id) { this.id = id; }
  public UUID getDashboardId() { return dashboardId; }
  public void setDashboardId(UUID dashboardId) { this.dashboardId = dashboardId; }
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
