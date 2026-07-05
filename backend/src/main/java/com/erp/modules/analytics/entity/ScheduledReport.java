package com.erp.modules.analytics.entity;

import com.erp.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "scheduled_reports")
public class ScheduledReport extends BaseEntity {

  @Column(name = "schedule_code", nullable = false, unique = true)
  private String scheduleCode;

  @Column(name = "report_id", nullable = false)
  private String reportId;

  @Column(nullable = false)
  private String name;

  @Column(name = "cron_expression", nullable = false)
  private String cronExpression;

  @Column(nullable = false)
  private String status = "ACTIVE";

  @Column(name = "output_format")
  private String outputFormat = "PDF";

  @Column(name = "recipient_emails")
  private String recipientEmails;

  @Column(name = "last_run")
  private LocalDateTime lastRun;

  @Column(name = "next_run")
  private LocalDateTime nextRun;

  @Column(name = "total_runs")
  private Integer totalRuns = 0;

  @Lob
  @Column(name = "params", columnDefinition = "TEXT")
  private String params;

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
}
