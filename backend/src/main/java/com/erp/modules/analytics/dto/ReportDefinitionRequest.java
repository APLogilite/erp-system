package com.erp.modules.analytics.dto;

public class ReportDefinitionRequest {
  private String reportCode;
  private String name;
  private String description;
  private String reportType;
  private String modelCode;
  private String queryConfig;
  private String columnConfig;
  private String groupConfig;
  private String outputFormat;

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
}
