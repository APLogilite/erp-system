package com.erp.modules.analytics.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class KPIResponse {
  private UUID id;
  private String kpiCode;
  private String name;
  private String description;
  private String category;
  private String calculationType;
  private String queryExpression;
  private String comparisonPeriod;
  private String unit;
  private Boolean isPercentage;
  private Double targetValue;
  private Double thresholdWarning;
  private Double thresholdCritical;
  private Integer refreshInterval;
  private Boolean isActive;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  private Double currentValue;
  private Double previousValue;
  private Double changePercent;
  private String trend;

  public UUID getId() { return id; }
  public void setId(UUID id) { this.id = id; }
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
  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
  public LocalDateTime getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
  public Double getCurrentValue() { return currentValue; }
  public void setCurrentValue(Double currentValue) { this.currentValue = currentValue; }
  public Double getPreviousValue() { return previousValue; }
  public void setPreviousValue(Double previousValue) { this.previousValue = previousValue; }
  public Double getChangePercent() { return changePercent; }
  public void setChangePercent(Double changePercent) { this.changePercent = changePercent; }
  public String getTrend() { return trend; }
  public void setTrend(String trend) { this.trend = trend; }
}
