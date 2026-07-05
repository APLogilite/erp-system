package com.erp.modules.analytics.service;

import com.erp.modules.analytics.entity.ScheduledReport;
import com.erp.modules.analytics.repository.ScheduledReportRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReportScheduler {

  private final ScheduledReportRepository scheduleRepository;
  private final ReportEngine reportEngine;

  public ReportScheduler(ScheduledReportRepository scheduleRepository, ReportEngine reportEngine) {
    this.scheduleRepository = scheduleRepository;
    this.reportEngine = reportEngine;
  }

  public String cronToDescription(String cron) {
    if (cron == null) return "Manual";
    String[] parts = cron.split(" ");
    if (parts.length < 5) return "Custom";
    if ("0 0 * * * ?".equals(cron)) return "Hourly";
    if ("0 0 0 * * ?".equals(cron)) return "Daily";
    if ("0 0 0 * * 1".equals(cron)) return "Weekly (Monday)";
    if ("0 0 0 1 * ?".equals(cron)) return "Monthly (1st)";
    return "Custom Cron";
  }

  public void executeScheduled(UUID scheduleId) {
    ScheduledReport schedule = scheduleRepository.findById(scheduleId)
        .orElseThrow(() -> new IllegalArgumentException("Schedule not found: " + scheduleId));
    executeScheduled(schedule);
  }

  @Transactional
  public void executeScheduled(ScheduledReport schedule) {
    if (!"ACTIVE".equals(schedule.getStatus())) return;

    try {
      UUID reportId = UUID.fromString(schedule.getReportId());
      reportEngine.export(reportId, schedule.getOutputFormat(), null);
    } catch (Exception ignored) {}

    schedule.setLastRun(LocalDateTime.now());
    schedule.setNextRun(calculateNextRun(schedule.getCronExpression()));
    schedule.setTotalRuns(schedule.getTotalRuns() != null ? schedule.getTotalRuns() + 1 : 1);
    scheduleRepository.save(schedule);
  }

  public List<ScheduledReport> getDueSchedules() {
    return scheduleRepository.findByStatus("ACTIVE").stream()
        .filter(s -> s.getNextRun() == null || s.getNextRun().isBefore(LocalDateTime.now()))
        .toList();
  }

  private LocalDateTime calculateNextRun(String cronExpression) {
    if ("0 0 * * * ?".equals(cronExpression)) return LocalDateTime.now().plusHours(1);
    if ("0 0 0 * * ?".equals(cronExpression)) return LocalDateTime.now().plusDays(1);
    if ("0 0 0 * * 1".equals(cronExpression)) return LocalDateTime.now().plusWeeks(1);
    if ("0 0 0 1 * ?".equals(cronExpression)) return LocalDateTime.now().plusMonths(1);
    return LocalDateTime.now().plusDays(1);
  }
}
