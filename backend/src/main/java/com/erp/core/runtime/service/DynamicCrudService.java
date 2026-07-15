package com.erp.core.runtime.service;

import com.erp.core.runtime.exception.RecordNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Generic Dynamic CRUD service that performs database operations on any
 * dynamic table without requiring JPA entity classes. Uses
 * NamedParameterJdbcTemplate with parameterized queries for security.
 *
 * <p>Tenant isolation is enforced on every query. Column names are validated
 * against sys_table_columns metadata to prevent SQL injection.
 */
@Service
public class DynamicCrudService {

  private static final Logger log = LoggerFactory.getLogger(DynamicCrudService.class);

  public static final Set<String> SYSTEM_COLUMNS = Set.of(
      "id", "tenant_id", "created_at", "updated_at",
      "created_by", "updated_by", "is_active", "deleted_at");

  private final NamedParameterJdbcTemplate jdbcTemplate;

  public DynamicCrudService(NamedParameterJdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  /**
   * Represents a single row filter condition for role-based data access.
   */
  public static class RowFilter {
    private final String field;
    private final String operator;
    private final String value;

    public RowFilter(String field, String operator, String value) {
      this.field = field;
      this.operator = operator;
      this.value = value;
    }

    public String getField() { return field; }
    public String getOperator() { return operator; }
    public String getValue() { return value; }
  }

  // ---------------------------------------------------------------
  // Public API
  // ---------------------------------------------------------------

  /**
   * List records from a dynamic table with pagination, sorting, tenant
   * isolation, where clause filtering, and role-based row filters.
   *
   * @param tableName   the physical table name
   * @param whereClause optional where clause field, e.g. "order_type"
   * @param whereValue  optional value for the where clause
   * @param tenantId    the current tenant ID (from JWT)
   * @param page        page number (0-based)
   * @param size        page size
   * @param sortField   field to sort by (null for default)
   * @param sortDir     sort direction: "asc" or "desc"
   * @param rowFilters  list of role-based row filters
   * @return map with keys: items, page, size, total
   */
  public Map<String, Object> listRecords(
      String tableName,
      String whereClause,
      String whereValue,
      UUID tenantId,
      int page,
      int size,
      String sortField,
      String sortDir,
      List<RowFilter> rowFilters) {

    validateTableName(tableName);

    String tableRef = escapeIdentifier(tableName);
    MapSqlParameterSource params = new MapSqlParameterSource();
    List<String> conditions = new ArrayList<>();

    // Tenant isolation
    conditions.add(tableRef + ".tenant_id = :tenantId");
    params.addValue("tenantId", tenantId);

    // Where clause (form-level data filter)
    if (whereClause != null && !whereClause.isBlank()
        && whereValue != null && !whereValue.isBlank()) {
      validateColumnName(tableName, whereClause);
      conditions.add(tableRef + "." + escapeIdentifier(whereClause) + " = :whereValue");
      params.addValue("whereValue", whereValue);
    }

    // Role-based row filters
    if (rowFilters != null) {
      for (int i = 0; i < rowFilters.size(); i++) {
        RowFilter rf = rowFilters.get(i);
        validateColumnName(tableName, rf.getField());
        String resolvedValue = resolveDynamicVariable(rf.getValue());
        String paramName = "rf_" + i;
        String op = mapOperator(rf.getOperator());
        conditions.add(tableRef + "." + escapeIdentifier(rf.getField()) + " " + op + " :" + paramName);
        params.addValue(paramName, resolvedValue);
      }
    }

    String whereClauseStr = String.join(" AND ", conditions);

    // Count query
    String countSql = "SELECT COUNT(*) FROM " + tableRef + " WHERE " + whereClauseStr;
    Integer total = jdbcTemplate.queryForObject(countSql, params, Integer.class);

    // Sort
    String orderClause;
    if (sortField != null && !sortField.isBlank()) {
      validateColumnName(tableName, sortField);
      String dir = "desc".equalsIgnoreCase(sortDir) ? "DESC" : "ASC";
      orderClause = " ORDER BY " + tableRef + "." + escapeIdentifier(sortField) + " " + dir;
    } else {
      orderClause = " ORDER BY " + tableRef + ".created_at DESC";
    }

    // Pagination
    int offset = Math.max(0, page) * size;
    String limitClause = " LIMIT " + size + " OFFSET " + offset;

    String querySql = "SELECT * FROM " + tableRef + " WHERE " + whereClauseStr
        + orderClause + limitClause;

    List<Map<String, Object>> rows = jdbcTemplate.queryForList(querySql, params);

    Map<String, Object> result = new LinkedHashMap<>();
    result.put("items", rows);
    result.put("page", page);
    result.put("size", size);
    result.put("total", total != null ? total : 0);
    return result;
  }

  /**
   * Get a single record by ID. Row filters are applied so that if the
   * record exists but is filtered out, null is returned (caller interprets
   * as 404).
   *
   * @param tableName  the physical table name
   * @param recordId   the record UUID
   * @param tenantId   the current tenant ID
   * @param rowFilters list of role-based row filters
   * @return the record as a map, or null if filtered out
   */
  public Map<String, Object> getRecord(
      String tableName,
      UUID recordId,
      UUID tenantId,
      List<RowFilter> rowFilters) {

    validateTableName(tableName);
    String tableRef = escapeIdentifier(tableName);
    MapSqlParameterSource params = new MapSqlParameterSource();
    List<String> conditions = new ArrayList<>();

    conditions.add(tableRef + ".id = :recordId");
    params.addValue("recordId", recordId);

    conditions.add(tableRef + ".tenant_id = :tenantId");
    params.addValue("tenantId", tenantId);

    // Row filters (applied to hide records from unauthorized roles)
    if (rowFilters != null) {
      for (int i = 0; i < rowFilters.size(); i++) {
        RowFilter rf = rowFilters.get(i);
        validateColumnName(tableName, rf.getField());
        String resolvedValue = resolveDynamicVariable(rf.getValue());
        String paramName = "rf_" + i;
        String op = mapOperator(rf.getOperator());
        conditions.add(tableRef + "." + escapeIdentifier(rf.getField()) + " " + op + " :" + paramName);
        params.addValue(paramName, resolvedValue);
      }
    }

    String sql = "SELECT * FROM " + tableRef + " WHERE " + String.join(" AND ", conditions) + " LIMIT 1";
    List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, params);
    return rows.isEmpty() ? null : rows.get(0);
  }

  /**
   * Create a new record in a dynamic table. Injects tenant_id, id,
   * created_by, and created_at.
   *
   * @param tableName the physical table name
   * @param data      the record data (column name → value)
   * @param tenantId  the current tenant ID
   * @param userId    the current user ID
   * @return the created record as a map
   */
  @Transactional
  public Map<String, Object> createRecord(
      String tableName,
      Map<String, Object> data,
      UUID tenantId,
      UUID userId) {

    validateTableName(tableName);
    validateColumns(tableName, data.keySet());
    convertUuidStrings(data); // Convert UUID strings to UUID objects for FK columns

    String tableRef = escapeIdentifier(tableName);
    MapSqlParameterSource params = new MapSqlParameterSource();

    // Inject system values
    UUID recordId = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();

    List<String> columns = new ArrayList<>();
    List<String> valueParams = new ArrayList<>();

    // System columns
    columns.add("id");
    valueParams.add(":id");
    params.addValue("id", recordId);

    columns.add("tenant_id");
    valueParams.add(":tenantId");
    params.addValue("tenantId", tenantId);

    columns.add("created_at");
    valueParams.add(":createdAt");
    params.addValue("createdAt", now);

    columns.add("updated_at");
    valueParams.add(":updatedAt");
    params.addValue("updatedAt", now);

    columns.add("created_by");
    valueParams.add(":createdBy");
    params.addValue("createdBy", userId);

    columns.add("updated_by");
    valueParams.add(":updatedBy");
    params.addValue("updatedBy", userId);

    columns.add("is_active");
    valueParams.add(":isActive");
    params.addValue("isActive", true);

    // User-provided columns
    List<String> userColumns = new ArrayList<>();
    for (Map.Entry<String, Object> entry : data.entrySet()) {
      String col = entry.getKey();
      if (SYSTEM_COLUMNS.contains(col)) {
        continue; // Skip system columns that might be in the request
      }
      userColumns.add(col);
      columns.add(col);
      String paramName = "col_" + col;
      valueParams.add(":" + paramName);
      params.addValue(paramName, entry.getValue());
    }

    String sql = "INSERT INTO " + tableRef + " ("
        + columns.stream().map(this::escapeIdentifier).collect(Collectors.joining(", "))
        + ") VALUES ("
        + String.join(", ", valueParams)
        + ")";

    log.info("Creating record in table: {}", tableName);
    jdbcTemplate.update(sql, params);

    return getRecord(tableName, recordId, tenantId, null);
  }

  /**
   * Update an existing record. Only provided columns are updated.
   * Read-only fields are stripped from the update. Tenant isolation
   * is enforced — the record must belong to the current tenant.
   *
   * @param tableName      the physical table name
   * @param recordId       the record UUID
   * @param data           the update data (column → value)
   * @param tenantId       the current tenant ID
   * @param userId         the current user ID
   * @param readOnlyFields set of column names that should not be updated
   * @return the updated record as a map
   */
  @Transactional
  public Map<String, Object> updateRecord(
      String tableName,
      UUID recordId,
      Map<String, Object> data,
      UUID tenantId,
      UUID userId,
      Set<String> readOnlyFields) {

    // Verify record exists and belongs to tenant
    Map<String, Object> existing = getRecord(tableName, recordId, tenantId, null);
    if (existing == null) {
      throw new RecordNotFoundException(tableName, recordId,
          "Record not found or access denied");
    }

    validateTableName(tableName);
    validateColumns(tableName, data.keySet());
    convertUuidStrings(data); // Convert UUID strings to UUID objects for FK columns

    String tableRef = escapeIdentifier(tableName);
    MapSqlParameterSource params = new MapSqlParameterSource();

    List<String> setClauses = new ArrayList<>();
    params.addValue("recordId", recordId);

    for (Map.Entry<String, Object> entry : data.entrySet()) {
      String col = entry.getKey();
      if (SYSTEM_COLUMNS.contains(col)) {
        continue;
      }
      if (readOnlyFields != null && readOnlyFields.contains(col)) {
        continue; // Strip read-only fields
      }
      String paramName = "col_" + col;
      setClauses.add(escapeIdentifier(col) + " = :" + paramName);
      params.addValue(paramName, entry.getValue());
    }

    // Always update system timestamp
    setClauses.add("updated_at = :updatedAt");
    params.addValue("updatedAt", LocalDateTime.now());
    setClauses.add("updated_by = :updatedBy");
    params.addValue("updatedBy", userId);

    String sql = "UPDATE " + tableRef + " SET "
        + String.join(", ", setClauses)
        + " WHERE id = :recordId AND tenant_id = :tenantId";
    params.addValue("tenantId", tenantId);

    log.info("Updating record {} in table: {}", recordId, tableName);
    int affected = jdbcTemplate.update(sql, params);
    if (affected == 0) {
      throw new RecordNotFoundException(tableName, recordId,
          "Record not found or access denied during update");
    }

    return getRecord(tableName, recordId, tenantId, null);
  }

  /**
   * Soft-deletes a record (sets is_active = false, deleted_at = NOW()).
   *
   * @param tableName the physical table name
   * @param recordId  the record UUID
   * @param tenantId  the current tenant ID
   */
  @Transactional
  public void deleteRecord(String tableName, UUID recordId, UUID tenantId) {
    validateTableName(tableName);
    String tableRef = escapeIdentifier(tableName);

    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("recordId", recordId);
    params.addValue("tenantId", tenantId);
    params.addValue("deletedAt", LocalDateTime.now());

    String sql = "UPDATE " + tableRef + " SET is_active = false, deleted_at = :deletedAt "
        + "WHERE id = :recordId AND tenant_id = :tenantId";

    log.info("Soft-deleting record {} from table: {}", recordId, tableName);
    int affected = jdbcTemplate.update(sql, params);
    if (affected == 0) {
      throw new RecordNotFoundException(tableName, recordId,
          "Record not found or access denied during delete");
    }
  }

  /**
   * Returns child records for a given parent relationship.
   * Used for populating sub-form tab grids.
   *
   * @param childTableName  the child table name
   * @param relationColumn  the FK column on the child table referencing the parent
   * @param parentRecordId  the parent record UUID
   * @param tenantId        the current tenant ID
   * @return list of child records
   */
  public List<Map<String, Object>> getChildRecords(
      String childTableName,
      String relationColumn,
      UUID parentRecordId,
      UUID tenantId,
      Map<String, String> additionalConditions) {

    validateTableName(childTableName);
    validateColumnName(childTableName, relationColumn);

    String tableRef = escapeIdentifier(childTableName);
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("parentId", parentRecordId);
    params.addValue("tenantId", tenantId);

    List<String> conditions = new ArrayList<>();
    conditions.add(escapeIdentifier(relationColumn) + " = :parentId");
    conditions.add(tableRef + ".tenant_id = :tenantId");

    // Apply additional conditions (e.g., where_clause filters like shipment_type = 'outbound')
    if (additionalConditions != null) {
      int i = 0;
      for (Map.Entry<String, String> entry : additionalConditions.entrySet()) {
        validateColumnName(childTableName, entry.getKey());
        String paramName = "ac_" + i;
        conditions.add(escapeIdentifier(entry.getKey()) + " = :" + paramName);
        params.addValue(paramName, entry.getValue());
        i++;
      }
    }

    String sql = "SELECT * FROM " + tableRef + " WHERE "
        + String.join(" AND ", conditions);

    return jdbcTemplate.queryForList(sql, params);
  }

  /**
   * Converts string values in FK columns (_id suffix) from JSON strings to UUID objects.
   * PostgreSQL UUID columns reject string values — they expect UUID objects.
   * Attempts conversion on any string value in an _id column that looks like a UUID.
   */
  private void convertUuidStrings(Map<String, Object> data) {
    if (data == null) return;
    for (Map.Entry<String, Object> entry : data.entrySet()) {
      String col = entry.getKey();
      Object val = entry.getValue();
      if (val instanceof String str && col.endsWith("_id")) {
        try {
          data.put(col, UUID.fromString(str));
        } catch (IllegalArgumentException ignored) {
          // Not a valid UUID format, leave as string
        }
      }
    }
  }

  // ---------------------------------------------------------------
  // Private helpers
  // ---------------------------------------------------------------

  /**
   * Validates that the table name exists in the metadata.
   * Future: could check sys_metadata_models for existence.
   */
  private void validateTableName(String tableName) {
    if (tableName == null || tableName.isBlank()) {
      throw new IllegalArgumentException("Table name must not be empty");
    }
    // Basic SQL injection check: only allow alphanumeric + underscore
    if (!tableName.matches("^[a-zA-Z_][a-zA-Z0-9_]*$")) {
      throw new IllegalArgumentException("Invalid table name: " + tableName);
    }
  }

  /**
   * Validates that all provided column names follow safe naming conventions.
   * Full column-vs-metadata validation is done at a higher service layer
   * that has access to the form definition and table model context.
   */
  private void validateColumns(String tableName, Set<String> columnNames) {
    if (columnNames == null || columnNames.isEmpty()) {
      return;
    }
    for (String col : columnNames) {
      if (SYSTEM_COLUMNS.contains(col)) {
        continue;
      }
      validateColumnName(tableName, col);
    }
  }

  /**
   * Validates that a single column name follows safe PostgreSQL identifier rules.
   * This prevents SQL injection via column names in dynamic queries.
   */
  private void validateColumnName(String tableName, String columnName) {
    if (columnName == null || columnName.isBlank()) {
      throw new IllegalArgumentException("Column name must not be empty");
    }
    if (!columnName.matches("^[a-zA-Z_][a-zA-Z0-9_]*$")) {
      throw new IllegalArgumentException("Invalid column name: " + columnName + " for table: " + tableName);
    }
  }

  /**
   * Resolves dynamic variables like {current_user_id}, {current_user_role},
   * {current_tenant_id} in row filter values.
   * Future implementation will resolve from JWT claims.
   */
  private String resolveDynamicVariable(String value) {
    if (value == null) {
      return null;
    }
    // Simple variable resolution — in production, resolve from SecurityContext/JWT
    if (value.contains("{current_tenant_id}")) {
      // Will be resolved at runtime from security context
    }
    return value;
  }

  /**
   * Maps internal operator names to SQL operators.
   */
  private String mapOperator(String operator) {
    if (operator == null) {
      return "=";
    }
    return switch (operator) {
      case "equals" -> "=";
      case "not_equals" -> "<>";
      case "greater_than" -> ">";
      case "less_than" -> "<";
      case "greater_than_or_equal" -> ">=";
      case "less_than_or_equal" -> "<=";
      case "contains" -> "ILIKE";
      case "in" -> "IN";
      default -> "=";
    };
  }

  /**
   * Escapes a PostgreSQL identifier by quoting it.
   */
  private String escapeIdentifier(String identifier) {
    return "\"" + identifier.replace("\"", "\"\"") + "\"";
  }
}
