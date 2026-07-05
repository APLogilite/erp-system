package com.erp.modules.analytics.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class ScheduledReportResponse {
  private UUID id;
  private String scheduleCode;
  private String reportId;
  private String name;
  private String cronExpression;
  private String status;
  private String outputFormat;
  private String recipientEmails;
  private LocalDateTime lastRun;
  private LocalDateTime nextRun;
  private Integer totalRuns;
  private String params;
  private Boolean isActive;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public UUID getId() { return id; }
  public void setId(UUID id) { this.id = id; }
  public String getScheduleCode() { return scheduleCode; }
  public void setScheduleCode(String scheduleCode) { this.scheduleCode = scheduleCode; }
  public String getReportId() { return reportId; }
  public void setReportId(String reportId) { this.reportId = reportId; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public String getCronExpression() { return cronExpression; }
  public void setCronExpression(String cronExpression) { this.cronExpression = cronExpression; }
  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }
  public String getOutputFormat() { return outputFormat; }
  public void setOutputFormat(String outputFormat) { this.outputFormat = outputFormat; }
  public String getRecipientEmails() { return recipientEmails; }
  public void setRecipientEmails(String recipientEmails) { this.recipientEmails = recipientEmails; }
  public LocalDateTime getLastRun() { return lastRun; }
  public void setLastRun(LocalDateTime lastRun) { this.lastRun = lastRun; }
  public LocalDateTime getNextRun() { return nextRun; }
  public void setNextRun(LocalDateTime nextRun) { this.nextRun = nextRun; }
  public Integer getTotalRuns() { return totalRuns; }
  public void setTotalRuns(Integer totalRuns) { this.totalRuns = totalRuns; }
  public String getParams() { return params; }
  public void setParams(String params) { this.params = params; }
  public Boolean getIsActive() { return isActive; }
  public void setIsActive(Boolean isActive) { this.isActive = isActive; }
  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
  public LocalDateTime getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
