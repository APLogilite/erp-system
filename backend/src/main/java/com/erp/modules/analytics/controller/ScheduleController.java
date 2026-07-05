package com.erp.modules.analytics.controller;

import com.erp.common.api.ApiResponse;
import com.erp.config.ApiVersionConfig;
import com.erp.modules.analytics.dto.ScheduledReportResponse;
import com.erp.modules.analytics.entity.ScheduledReport;
import com.erp.modules.analytics.repository.ScheduledReportRepository;
import com.erp.modules.analytics.service.ReportScheduler;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiVersionConfig.API_V1 + "/schedules")
public class ScheduleController {

  private final ScheduledReportRepository scheduleRepository;
  private final ReportScheduler reportScheduler;

  public ScheduleController(ScheduledReportRepository scheduleRepository,
                            ReportScheduler reportScheduler) {
    this.scheduleRepository = scheduleRepository;
    this.reportScheduler = reportScheduler;
  }

  @PostMapping
  public ResponseEntity<ApiResponse<ScheduledReportResponse>> create(@RequestBody ScheduledReport schedule) {
    if (schedule.getScheduleCode() == null) {
      schedule.setScheduleCode("SCH-" + System.currentTimeMillis());
    }
    if (schedule.getStatus() == null) {
      schedule.setStatus("ACTIVE");
    }
    if (schedule.getTotalRuns() == null) {
      schedule.setTotalRuns(0);
    }
    schedule.setNextRun(LocalDateTime.now());
    ScheduledReport saved = scheduleRepository.save(schedule);
    return ResponseEntity.ok(ApiResponse.success(toResponse(saved), "Schedule created"));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<ScheduledReportResponse>>> getAll() {
    List<ScheduledReportResponse> list = scheduleRepository.findAll().stream()
        .map(this::toResponse).collect(Collectors.toList());
    return ResponseEntity.ok(ApiResponse.success(list, "Schedules retrieved"));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<ScheduledReportResponse>> getById(@PathVariable UUID id) {
    ScheduledReport schedule = scheduleRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Schedule not found: " + id));
    return ResponseEntity.ok(ApiResponse.success(toResponse(schedule), "Schedule retrieved"));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<ScheduledReportResponse>> update(@PathVariable UUID id, @RequestBody ScheduledReport request) {
    ScheduledReport existing = scheduleRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Schedule not found: " + id));
    existing.setCronExpression(request.getCronExpression());
    existing.setOutputFormat(request.getOutputFormat());
    existing.setRecipientEmails(request.getRecipientEmails());
    existing.setParams(request.getParams());
    existing.setStatus(request.getStatus());
    ScheduledReport saved = scheduleRepository.save(existing);
    return ResponseEntity.ok(ApiResponse.success(toResponse(saved), "Schedule updated"));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
    scheduleRepository.deleteById(id);
    return ResponseEntity.ok(ApiResponse.successMessage("Schedule deleted"));
  }

  @PostMapping("/{id}/execute")
  public ResponseEntity<ApiResponse<Void>> execute(@PathVariable UUID id) {
    reportScheduler.executeScheduled(id);
    return ResponseEntity.ok(ApiResponse.successMessage("Schedule executed"));
  }

  @GetMapping("/due")
  public ResponseEntity<ApiResponse<List<ScheduledReportResponse>>> getDue() {
    List<ScheduledReportResponse> list = reportScheduler.getDueSchedules().stream()
        .map(this::toResponse).collect(Collectors.toList());
    return ResponseEntity.ok(ApiResponse.success(list, "Due schedules retrieved"));
  }

  private ScheduledReportResponse toResponse(ScheduledReport s) {
    ScheduledReportResponse r = new ScheduledReportResponse();
    r.setId(s.getId());
    r.setScheduleCode(s.getScheduleCode());
    r.setReportId(s.getReportId());
    r.setName(s.getName());
    r.setCronExpression(s.getCronExpression());
    r.setStatus(s.getStatus());
    r.setOutputFormat(s.getOutputFormat());
    r.setRecipientEmails(s.getRecipientEmails());
    r.setLastRun(s.getLastRun());
    r.setNextRun(s.getNextRun());
    r.setTotalRuns(s.getTotalRuns());
    r.setParams(s.getParams());
    r.setIsActive(s.getIsActive());
    r.setCreatedAt(s.getCreatedAt());
    r.setUpdatedAt(s.getUpdatedAt());
    return r;
  }
}
