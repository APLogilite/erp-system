package com.erp.modules.analytics.entity;

import com.erp.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "dashboard_widgets")
public class DashboardWidget extends BaseEntity {

  @Column(name = "dashboard_id", nullable = false)
  private UUID dashboardId;

  @Column(name = "widget_type", nullable = false)
  private String widgetType;

  @Column(nullable = false)
  private String title;

  @Column(name = "widget_config", columnDefinition = "TEXT")
  private String widgetConfig;

  @Column(name = "position_x")
  private Integer positionX = 0;

  @Column(name = "position_y")
  private Integer positionY = 0;

  @Column(name = "width")
  private Integer width = 6;

  @Column(name = "height")
  private Integer height = 4;

  @Column(name = "data_source")
  private String dataSource;

  @Column(name = "refresh_interval")
  private Integer refreshInterval = 0;

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
