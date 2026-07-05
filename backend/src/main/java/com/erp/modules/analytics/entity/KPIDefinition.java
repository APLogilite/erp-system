package com.erp.modules.analytics.entity;

import com.erp.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table(name = "kpi_definitions")
public class KPIDefinition extends BaseEntity {

  @Column(name = "kpi_code", nullable = false, unique = true)
  private String kpiCode;

  @Column(nullable = false)
  private String name;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(nullable = false)
  private String category;

  @Column(name = "calculation_type", nullable = false)
  private String calculationType;

  @Lob
  @Column(name = "query_expression", columnDefinition = "TEXT")
  private String queryExpression;

  @Column(name = "comparison_period")
  private String comparisonPeriod;

  @Column(name = "unit")
  private String unit;

  @Column(name = "is_percentage")
  private Boolean isPercentage = false;

  @Column(name = "target_value")
  private Double targetValue;

  @Column(name = "threshold_warning")
  private Double thresholdWarning;

  @Column(name = "threshold_critical")
  private Double thresholdCritical;

  @Column(name = "refresh_interval")
  private Integer refreshInterval = 300;

  @Column(name = "is_active")
  private Boolean isActive = true;

  public String getKpiCode() { return kpiCode; }
  public void setKpiCode(String kpiCode) { this.kpiCode = kpiCode; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }
  public String getCategory() { return category; }
  public void setCategory(String category) { this.category = category; }
  public String getCalculationType() { return calculationType; }
  public void setCalculationType(String calculationType) { this.calculationType = calculationType; }
  public String getQueryExpression() { return queryExpression; }
  public void setQueryExpression(String queryExpression) { this.queryExpression = queryExpression; }
  public String getComparisonPeriod() { return comparisonPeriod; }
  public void setComparisonPeriod(String comparisonPeriod) { this.comparisonPeriod = comparisonPeriod; }
  public String getUnit() { return unit; }
  public void setUnit(String unit) { this.unit = unit; }
  public Boolean getIsPercentage() { return isPercentage; }
  public void setIsPercentage(Boolean isPercentage) { this.isPercentage = isPercentage; }
  public Double getTargetValue() { return targetValue; }
  public void setTargetValue(Double targetValue) { this.targetValue = targetValue; }
  public Double getThresholdWarning() { return thresholdWarning; }
  public void setThresholdWarning(Double thresholdWarning) { this.thresholdWarning = thresholdWarning; }
  public Double getThresholdCritical() { return thresholdCritical; }
  public void setThresholdCritical(Double thresholdCritical) { this.thresholdCritical = thresholdCritical; }
  public Integer getRefreshInterval() { return refreshInterval; }
  public void setRefreshInterval(Integer refreshInterval) { this.refreshInterval = refreshInterval; }
  public Boolean getIsActive() { return isActive; }
  public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
