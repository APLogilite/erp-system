package com.erp.modules.analytics.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class DrillDownEngine {

  private final JdbcTemplate jdbcTemplate;

  public DrillDownEngine(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public Map<String, Object> drill(DrillRequest request) {
    Map<String, Object> result = new HashMap<>();
    result.put("source", request.getSource());
    result.put("drillPath", request.getDrillPath());

    try {
      String sql = request.getQuery();
      if (request.getContext() != null) {
        for (Map.Entry<String, String> entry : request.getContext().entrySet()) {
          sql = sql.replace(":" + entry.getKey(), "'" + entry.getValue().replace("'", "''") + "'");
        }
      }
      List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
      result.put("data", rows);
      result.put("totalRows", rows.size());
      result.put("columns", rows.isEmpty() ? List.of() : new ArrayList<>(rows.get(0).keySet()));
      result.put("success", true);
    } catch (Exception e) {
      result.put("success", false);
      result.put("error", e.getMessage());
      result.put("data", List.of());
    }

    return result;
  }

  public static class DrillRequest {
    private String source;
    private String drillPath;
    private String query;
    private Map<String, String> context;

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getDrillPath() { return drillPath; }
    public void setDrillPath(String drillPath) { this.drillPath = drillPath; }
    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }
    public Map<String, String> getContext() { return context; }
    public void setContext(Map<String, String> context) { this.context = context; }
  }
}
