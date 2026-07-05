package com.erp.modules.analytics.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class PivotEngine {

  private final JdbcTemplate jdbcTemplate;

  public PivotEngine(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public Map<String, Object> build(PivotRequest request) {
    Map<String, Object> result = new HashMap<>();
    result.put("title", request.getTitle());

    try {
      String query = buildQuery(request);
      List<Map<String, Object>> rows = jdbcTemplate.queryForList(query);

      Map<String, Map<String, Double>> pivot = new LinkedHashMap<>();
      List<String> columnKeys = new ArrayList<>();

      for (Map<String, Object> row : rows) {
        String rowKey = extractKey(row, request.getRowFields());
        String colKey = extractKey(row, request.getColumnFields());
        Double value = extractValue(row, request.getMeasureField());

        if (!columnKeys.contains(colKey)) {
          columnKeys.add(colKey);
        }
        pivot.computeIfAbsent(rowKey, k -> new LinkedHashMap<>())
            .merge(colKey, value, Double::sum);
      }

      result.put("rows", new ArrayList<>(pivot.keySet()));
      result.put("columns", columnKeys);
      result.put("data", pivot);
      result.put("success", true);
    } catch (Exception e) {
      result.put("success", false);
      result.put("error", e.getMessage());
      result.put("rows", List.of());
      result.put("columns", List.of());
      result.put("data", Map.of());
    }

    return result;
  }

  private String buildQuery(PivotRequest request) {
    StringBuilder sql = new StringBuilder("SELECT ");
    for (String rf : request.getRowFields()) {
      sql.append(rf).append(", ");
    }
    for (String cf : request.getColumnFields()) {
      sql.append(cf).append(", ");
    }
    sql.append(request.getMeasureField());
    sql.append(" FROM ").append(request.getTableName());

    if (request.getFilters() != null && !request.getFilters().isEmpty()) {
      sql.append(" WHERE ");
      for (int i = 0; i < request.getFilters().size(); i++) {
        if (i > 0) sql.append(" AND ");
        sql.append(request.getFilters().get(i));
      }
    }

    sql.append(" GROUP BY ");
    for (int i = 0; i < request.getRowFields().size(); i++) {
      if (i > 0) sql.append(", ");
      sql.append(request.getRowFields().get(i));
    }
    for (String cf : request.getColumnFields()) {
      sql.append(", ").append(cf);
    }

    sql.append(" ORDER BY ");
    for (int i = 0; i < request.getRowFields().size(); i++) {
      if (i > 0) sql.append(", ");
      sql.append(request.getRowFields().get(i));
    }

    return sql.toString();
  }

  private String extractKey(Map<String, Object> row, List<String> fields) {
    StringBuilder key = new StringBuilder();
    for (String f : fields) {
      Object val = row.get(f);
      if (key.length() > 0) key.append("|");
      key.append(val != null ? val.toString() : "N/A");
    }
    return key.toString();
  }

  private Double extractValue(Map<String, Object> row, String measureField) {
    Object val = row.get(measureField);
    if (val instanceof Number) return ((Number) val).doubleValue();
    return 0.0;
  }

  public static class PivotRequest {
    private String title;
    private String tableName;
    private List<String> rowFields;
    private List<String> columnFields;
    private String measureField;
    private List<String> filters;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getTableName() { return tableName; }
    public void setTableName(String tableName) { this.tableName = tableName; }
    public List<String> getRowFields() { return rowFields; }
    public void setRowFields(List<String> rowFields) { this.rowFields = rowFields; }
    public List<String> getColumnFields() { return columnFields; }
    public void setColumnFields(List<String> columnFields) { this.columnFields = columnFields; }
    public String getMeasureField() { return measureField; }
    public void setMeasureField(String measureField) { this.measureField = measureField; }
    public List<String> getFilters() { return filters; }
    public void setFilters(List<String> filters) { this.filters = filters; }
  }
}
