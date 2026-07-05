package com.erp.modules.analytics.service;

import com.erp.modules.analytics.entity.ReportDefinition;
import com.erp.modules.analytics.repository.ReportDefinitionRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class ReportEngine {

  private final ReportDefinitionRepository reportRepository;
  private final JdbcTemplate jdbcTemplate;

  public ReportEngine(ReportDefinitionRepository reportRepository, JdbcTemplate jdbcTemplate) {
    this.reportRepository = reportRepository;
    this.jdbcTemplate = jdbcTemplate;
  }

  public Map<String, Object> generate(UUID reportId, Map<String, String> params) {
    ReportDefinition report = reportRepository.findById(reportId)
        .orElseThrow(() -> new IllegalArgumentException("Report not found: " + reportId));
    return generate(report, params);
  }

  public Map<String, Object> generate(String reportCode, Map<String, String> params) {
    ReportDefinition report = reportRepository.findByReportCode(reportCode)
        .orElseThrow(() -> new IllegalArgumentException("Report not found: " + reportCode));
    return generate(report, params);
  }

  public Map<String, Object> generate(ReportDefinition report, Map<String, String> params) {
    Map<String, Object> result = new HashMap<>();
    result.put("reportCode", report.getReportCode());
    result.put("reportName", report.getName());
    result.put("reportType", report.getReportType());
    result.put("outputFormat", report.getOutputFormat());

    try {
      String query = report.getQueryConfig();
      if (query != null && !query.trim().isEmpty()) {
        if (params != null) {
          for (Map.Entry<String, String> entry : params.entrySet()) {
            query = query.replace(":" + entry.getKey(), "'" + entry.getValue().replace("'", "''") + "'");
          }
        }
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(query);
        result.put("data", rows);
        result.put("totalRows", rows.size());
        result.put("columns", rows.isEmpty() ? List.of() : new ArrayList<>(rows.get(0).keySet()));
      } else {
        result.put("data", List.of());
        result.put("totalRows", 0);
        result.put("columns", List.of());
      }
      result.put("success", true);
    } catch (Exception e) {
      result.put("success", false);
      result.put("error", e.getMessage());
      result.put("data", List.of());
      result.put("totalRows", 0);
    }

    return result;
  }

  public Map<String, Object> export(UUID reportId, String format, Map<String, String> params) {
    Map<String, Object> data = generate(reportId, params);
    data.put("exportFormat", format);
    data.put("exportedAt", java.time.LocalDateTime.now().toString());
    return data;
  }
}
