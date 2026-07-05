package com.erp.modules.analytics.entity;

import com.erp.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table(name = "report_definitions")
public class ReportDefinition extends BaseEntity {

  @Column(name = "report_code", nullable = false, unique = true)
  private String reportCode;

  @Column(nullable = false)
  private String name;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(name = "report_type", nullable = false)
  private String reportType;

  @Column(name = "model_code")
  private String modelCode;

  @Lob
  @Column(name = "query_config", columnDefinition = "TEXT")
  private String queryConfig;

  @Lob
  @Column(name = "column_config", columnDefinition = "TEXT")
  private String columnConfig;

  @Lob
  @Column(name = "group_config", columnDefinition = "TEXT")
  private String groupConfig;

  @Column(name = "output_format")
  private String outputFormat = "GRID";

  @Column(name = "is_system")
  private Boolean isSystem = false;

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
}
