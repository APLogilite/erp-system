package com.erp.modules.analytics.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class QueryBuilderService {

  private final JdbcTemplate jdbcTemplate;

  public QueryBuilderService(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public Map<String, Object> execute(QueryRequest request) {
    Map<String, Object> result = new HashMap<>();

    try {
      String sql = buildQuery(request);
      List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);

      result.put("data", rows);
      result.put("totalRows", rows.size());
      result.put("columns", rows.isEmpty() ? List.of() : new ArrayList<>(rows.get(0).keySet()));
      result.put("sql", sql);
      result.put("success", true);
    } catch (Exception e) {
      result.put("success", false);
      result.put("error", e.getMessage());
      result.put("data", List.of());
      result.put("totalRows", 0);
    }

    return result;
  }

  private String buildQuery(QueryRequest request) {
    StringBuilder sql = new StringBuilder("SELECT ");

    if (request.getAggregations() != null && !request.getAggregations().isEmpty()) {
      List<String> aggParts = new ArrayList<>();
      for (Aggregation agg : request.getAggregations()) {
        aggParts.add(agg.getFunction() + "(" + agg.getField() + ") AS " + agg.getAlias());
      }
      sql.append(String.join(", ", aggParts));
    } else {
      sql.append("*");
    }

    sql.append(" FROM ").append(request.getTableName());

    if (request.getFilters() != null && !request.getFilters().isEmpty()) {
      sql.append(" WHERE ");
      sql.append(String.join(" AND ", request.getFilters()));
    }

    if (request.getGroupBy() != null && !request.getGroupBy().isEmpty()) {
      sql.append(" GROUP BY ").append(String.join(", ", request.getGroupBy()));
    }

    if (request.getOrderBy() != null && !request.getOrderBy().isEmpty()) {
      sql.append(" ORDER BY ").append(String.join(", ", request.getOrderBy()));
    }

    if (request.getLimit() > 0) {
      sql.append(" LIMIT ").append(request.getLimit());
    }

    return sql.toString();
  }

  public static class QueryRequest {
    private String tableName;
    private List<String> fields;
    private List<String> filters;
    private List<String> groupBy;
    private List<String> orderBy;
    private List<Aggregation> aggregations;
    private int limit = 100;

    public String getTableName() { return tableName; }
    public void setTableName(String tableName) { this.tableName = tableName; }
    public List<String> getFields() { return fields; }
    public void setFields(List<String> fields) { this.fields = fields; }
    public List<String> getFilters() { return filters; }
    public void setFilters(List<String> filters) { this.filters = filters; }
    public List<String> getGroupBy() { return groupBy; }
    public void setGroupBy(List<String> groupBy) { this.groupBy = groupBy; }
    public List<String> getOrderBy() { return orderBy; }
    public void setOrderBy(List<String> orderBy) { this.orderBy = orderBy; }
    public List<Aggregation> getAggregations() { return aggregations; }
    public void setAggregations(List<Aggregation> aggregations) { this.aggregations = aggregations; }
    public int getLimit() { return limit; }
    public void setLimit(int limit) { this.limit = limit; }
  }

  public static class Aggregation {
    private String field;
    private String function;
    private String alias;

    public String getField() { return field; }
    public void setField(String field) { this.field = field; }
    public String getFunction() { return function; }
    public void setFunction(String function) { this.function = function; }
    public String getAlias() { return alias; }
    public void setAlias(String alias) { this.alias = alias; }
  }
}
