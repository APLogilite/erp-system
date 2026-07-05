package com.erp.modules.analytics.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class ChartEngine {

  private final JdbcTemplate jdbcTemplate;

  public ChartEngine(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public Map<String, Object> generate(ChartRequest request) {
    Map<String, Object> result = new HashMap<>();
    result.put("chartType", request.getChartType());
    result.put("title", request.getTitle());

    try {
      List<Map<String, Object>> rows = jdbcTemplate.queryForList(request.getQuery());

      List<String> labels = new ArrayList<>();
      List<Double> values = new ArrayList<>();
      List<Map<String, Object>> series = new ArrayList<>();

      if ("PIE".equals(request.getChartType()) || "DONUT".equals(request.getChartType())) {
        for (Map<String, Object> row : rows) {
          labels.add(getString(row, request.getLabelField()));
          values.add(getDouble(row, request.getValueField()));
        }
        result.put("labels", labels);
        result.put("values", values);
      } else if ("SCATTER".equals(request.getChartType()) || "BUBBLE".equals(request.getChartType())) {
        for (Map<String, Object> row : rows) {
          Map<String, Object> point = new HashMap<>();
          point.put("x", getDouble(row, request.getLabelField()));
          point.put("y", getDouble(row, request.getValueField()));
          if (request.getSizeField() != null) {
            point.put("size", getDouble(row, request.getSizeField()));
          }
          series.add(point);
        }
        result.put("series", series);
      } else {
        Map<String, List<Double>> seriesMap = new LinkedHashMap<>();
        for (Map<String, Object> row : rows) {
          String label = getString(row, request.getLabelField());
          if (!labels.contains(label)) labels.add(label);
          String seriesName = request.getSeriesField() != null
              ? getString(row, request.getSeriesField()) : "default";
          Double val = getDouble(row, request.getValueField());
          seriesMap.computeIfAbsent(seriesName, k -> new ArrayList<>()).add(val);
        }
        result.put("labels", labels);
        for (Map.Entry<String, List<Double>> entry : seriesMap.entrySet()) {
          Map<String, Object> s = new HashMap<>();
          s.put("name", entry.getKey());
          s.put("data", entry.getValue());
          series.add(s);
        }
        result.put("series", series);
      }

      result.put("success", true);
    } catch (Exception e) {
      result.put("success", false);
      result.put("error", e.getMessage());
    }

    return result;
  }

  private String getString(Map<String, Object> row, String field) {
    Object val = row.get(field);
    return val != null ? val.toString() : "";
  }

  private Double getDouble(Map<String, Object> row, String field) {
    Object val = row.get(field);
    if (val instanceof Number) return ((Number) val).doubleValue();
    return 0.0;
  }

  public static class ChartRequest {
    private String chartType;
    private String title;
    private String query;
    private String labelField;
    private String valueField;
    private String seriesField;
    private String sizeField;

    public String getChartType() { return chartType; }
    public void setChartType(String chartType) { this.chartType = chartType; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }
    public String getLabelField() { return labelField; }
    public void setLabelField(String labelField) { this.labelField = labelField; }
    public String getValueField() { return valueField; }
    public void setValueField(String valueField) { this.valueField = valueField; }
    public String getSeriesField() { return seriesField; }
    public void setSeriesField(String seriesField) { this.seriesField = seriesField; }
    public String getSizeField() { return sizeField; }
    public void setSizeField(String sizeField) { this.sizeField = sizeField; }
  }
}
