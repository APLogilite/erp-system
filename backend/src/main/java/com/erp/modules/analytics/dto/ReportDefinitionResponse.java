package com.erp.modules.analytics.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class ReportDefinitionResponse {
  private UUID id;
  private String reportCode;
  private String name;
  private String description;
  private String reportType;
  private String modelCode;
  private String queryConfig;
  private String columnConfig;
  private String groupConfig;
  private String outputFormat;
  private Boolean isSystem;
  private Boolean isActive;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public UUID getId() { return id; }
  public void setId(UUID id) { this.id = id; }
  public String getReportCode() { return reportCode; }
  public void setReportCode(String reportCode) { this.reportCode = reportCode; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }
  public String getReportType() { return reportType; }
  public void setReportType(String reportType) { this.reportType = reportType; }
  public String getModelCode() { return modelCode; }
  public void setModelCode(String modelCode) { this.modelCode = modelCode; }
  public String getQueryConfig() { return queryConfig; }
  public void setQueryConfig(String queryConfig) { this.queryConfig = queryConfig; }
  public String getColumnConfig() { return columnConfig; }
  public void setColumnConfig(String columnConfig) { this.columnConfig = columnConfig; }
  public String getGroupConfig() { return groupConfig; }
  public void setGroupConfig(String groupConfig) { this.groupConfig = groupConfig; }
  public String getOutputFormat() { return outputFormat; }
  public void setOutputFormat(String outputFormat) { this.outputFormat = outputFormat; }
  public Boolean getIsSystem() { return isSystem; }
  public void setIsSystem(Boolean isSystem) { this.isSystem = isSystem; }
  public Boolean getIsActive() { return isActive; }
  public void setIsActive(Boolean isActive) { this.isActive = isActive; }
  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
  public LocalDateTime getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
