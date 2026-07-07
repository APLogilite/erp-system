package com.erp.core.metadata.service;

import com.erp.core.metadata.entity.MetadataModel;
import com.erp.core.metadata.entity.TableColumnEntity;
import com.erp.core.metadata.exception.DdlExecutionException;
import com.erp.core.metadata.repository.MetadataModelRepository;
import com.erp.core.metadata.repository.TableColumnRepository;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service that dynamically creates and alters PostgreSQL tables based on metadata
 * stored in sys_table_columns. Uses JdbcTemplate for all DDL operations since
 * there are no JPA entity classes for dynamically-created tables.
 */
@Service
public class DdlExecutorService {

  private static final Logger log = LoggerFactory.getLogger(DdlExecutorService.class);

  /**
   * Maps internal column type names to PostgreSQL column types.
   * "string"   → VARCHAR(n)         where n is max_length
   * "text"     → TEXT
   * "integer"  → INTEGER
   * "decimal"  → NUMERIC(p, s)      where p is precision, s is scale
   * "boolean"  → BOOLEAN
   * "date"     → DATE
   * "datetime" → TIMESTAMP
   * "many2one" → UUID               (FK to another table)
   * "enum"     → VARCHAR(100)
   */
  private static final Map<String, String> TYPE_MAP = Map.ofEntries(
      Map.entry("string", "VARCHAR(%d)"),
      Map.entry("text", "TEXT"),
      Map.entry("integer", "INTEGER"),
      Map.entry("decimal", "NUMERIC(%d, %d)"),
      Map.entry("boolean", "BOOLEAN"),
      Map.entry("date", "DATE"),
      Map.entry("datetime", "TIMESTAMP"),
      Map.entry("many2one", "UUID"),
      Map.entry("enum", "VARCHAR(100)")
  );

  private static final String BASE_COLUMNS_SQL = ""
      + "    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),\n"
      + "    tenant_id UUID NOT NULL,\n"
      + "    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,\n"
      + "    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,\n"
      + "    created_by UUID,\n"
      + "    updated_by UUID,\n"
      + "    is_active BOOLEAN NOT NULL DEFAULT TRUE,\n"
      + "    deleted_at TIMESTAMP";

  private final JdbcTemplate jdbcTemplate;
  private final MetadataModelRepository metadataModelRepository;
  private final TableColumnRepository tableColumnRepository;

  public DdlExecutorService(
      JdbcTemplate jdbcTemplate,
      MetadataModelRepository metadataModelRepository,
      TableColumnRepository tableColumnRepository) {
    this.jdbcTemplate = jdbcTemplate;
    this.metadataModelRepository = metadataModelRepository;
    this.tableColumnRepository = tableColumnRepository;
  }

  /**
   * Creates a physical PostgreSQL table from the metadata definition.
   * Loads the model and columns, generates a CREATE TABLE statement,
   * and executes it. Called within a @Transactional context so that
   * if DDL fails, metadata changes are also rolled back.
   *
   * @param tableId the UUID of the table definition in sys_metadata_models
   * @throws DdlExecutionException if DDL execution fails
   */
  @Transactional(rollbackFor = Exception.class)
  public void createTable(UUID tableId) {
    MetadataModel model = metadataModelRepository.findById(tableId)
        .orElseThrow(() -> new IllegalArgumentException("Table definition not found: " + tableId));

    String tableName = model.getTableName();
    if (tableName == null || tableName.isBlank()) {
      throw new IllegalArgumentException("Table name is not set for model: " + model.getName());
    }

    List<TableColumnEntity> columns = tableColumnRepository.findByTableIdAndIsActiveTrueOrderByPosition(tableId);

    String sql = buildCreateTableSql(tableName, columns);

    log.info("Executing DDL: CREATE TABLE {}", tableName);
    try {
      jdbcTemplate.execute(sql);
      log.info("Successfully created table: {}", tableName);
    } catch (Exception e) {
      log.error("Failed to create table {}: {}", tableName, e.getMessage());
      throw new DdlExecutionException(
          "Failed to create table: " + tableName,
          sql,
          tableName,
          e);
    }
  }

  /**
   * Adds a new column to an existing physical table.
   *
   * @param tableId  the UUID of the table definition
   * @param columnId the UUID of the column definition
   * @throws DdlExecutionException if DDL execution fails
   */
  @Transactional(rollbackFor = Exception.class)
  public void addColumn(UUID tableId, UUID columnId) {
    MetadataModel model = metadataModelRepository.findById(tableId)
        .orElseThrow(() -> new IllegalArgumentException("Table definition not found: " + tableId));

    TableColumnEntity column = tableColumnRepository.findById(columnId)
        .orElseThrow(() -> new IllegalArgumentException("Column definition not found: " + columnId));

    String tableName = model.getTableName();
    String sql = buildAddColumnSql(tableName, column);

    log.info("Executing DDL: ALTER TABLE {} ADD COLUMN {}", tableName, column.getCode());
    try {
      jdbcTemplate.execute(sql);
      log.info("Successfully added column {} to table {}", column.getCode(), tableName);
    } catch (Exception e) {
      log.error("Failed to add column {} to table {}: {}", column.getCode(), tableName, e.getMessage());
      throw new DdlExecutionException(
          "Failed to add column: " + column.getCode() + " to table: " + tableName,
          sql,
          tableName,
          e);
    }
  }

  /**
   * Drops a column from an existing physical table.
   *
   * @param tableId  the UUID of the table definition
   * @param columnId the UUID of the column definition
   * @throws DdlExecutionException if DDL execution fails
   */
  @Transactional(rollbackFor = Exception.class)
  public void dropColumn(UUID tableId, UUID columnId) {
    MetadataModel model = metadataModelRepository.findById(tableId)
        .orElseThrow(() -> new IllegalArgumentException("Table definition not found: " + tableId));

    TableColumnEntity column = tableColumnRepository.findById(columnId)
        .orElseThrow(() -> new IllegalArgumentException("Column definition not found: " + columnId));

    String tableName = model.getTableName();
    String columnName = column.getCode();

    log.warn("Dropping column {} from table {} — data will be lost", columnName, tableName);

    String sql = "ALTER TABLE " + escapeIdentifier(tableName)
        + " DROP COLUMN IF EXISTS " + escapeIdentifier(columnName);

    try {
      jdbcTemplate.execute(sql);
      log.info("Successfully dropped column {} from table {}", columnName, tableName);
    } catch (Exception e) {
      log.error("Failed to drop column {} from table {}: {}", columnName, tableName, e.getMessage());
      throw new DdlExecutionException(
          "Failed to drop column: " + columnName + " from table: " + tableName,
          sql,
          tableName,
          e);
    }
  }

  /**
   * Modifies an existing column's data type or constraints.
   * Some type changes (e.g., VARCHAR to INTEGER with non-numeric data)
   * will fail with a database error, which is propagated as an exception.
   *
   * @param tableId  the UUID of the table definition
   * @param columnId the UUID of the column definition
   * @throws DdlExecutionException if DDL execution fails or the type change is incompatible
   */
  @Transactional(rollbackFor = Exception.class)
  public void modifyColumn(UUID tableId, UUID columnId) {
    MetadataModel model = metadataModelRepository.findById(tableId)
        .orElseThrow(() -> new IllegalArgumentException("Table definition not found: " + tableId));

    TableColumnEntity column = tableColumnRepository.findById(columnId)
        .orElseThrow(() -> new IllegalArgumentException("Column definition not found: " + columnId));

    String tableName = model.getTableName();
    String columnName = column.getCode();
    String pgType = resolveType(column);

    // Build ALTER COLUMN type change SQL
    String sql = "ALTER TABLE " + escapeIdentifier(tableName)
        + " ALTER COLUMN " + escapeIdentifier(columnName)
        + " TYPE " + pgType;

    // Handle NOT NULL / NULL constraint
    if (Boolean.TRUE.equals(column.getRequired())) {
      sql += ", ALTER COLUMN " + escapeIdentifier(columnName) + " SET NOT NULL";
    } else {
      sql += ", ALTER COLUMN " + escapeIdentifier(columnName) + " DROP NOT NULL";
    }

    // Handle default value
    String defaultExpr = resolveDefaultValue(column);
    if (defaultExpr != null) {
      sql += ", ALTER COLUMN " + escapeIdentifier(columnName) + " SET DEFAULT " + defaultExpr;
    } else {
      sql += ", ALTER COLUMN " + escapeIdentifier(columnName) + " DROP DEFAULT";
    }

    log.info("Executing DDL: ALTER TABLE {} ALTER COLUMN {}", tableName, columnName);
    try {
      jdbcTemplate.execute(sql);
      log.info("Successfully modified column {} on table {}", columnName, tableName);
    } catch (Exception e) {
      log.error("Failed to modify column {} on table {}: {}", columnName, tableName, e.getMessage());
      throw new DdlExecutionException(
          "Failed to modify column: " + columnName + " on table: " + tableName,
          sql,
          tableName,
          e);
    }
  }

  /**
   * Checks whether a physical table exists in the database.
   *
   * @param tableName the physical table name
   * @return true if the table exists, false otherwise
   */
  public boolean tableExists(String tableName) {
    String sql = "SELECT COUNT(*) FROM information_schema.tables "
        + "WHERE table_schema = 'public' AND table_name = ?";
    Integer count = jdbcTemplate.queryForObject(sql, Integer.class, tableName.toLowerCase());
    return count != null && count > 0;
  }

  /**
   * Returns the current column metadata for a physical table from information_schema.
   *
   * @param tableName the physical table name
   * @return list of maps containing column_name, data_type, is_nullable, etc.
   */
  public List<Map<String, Object>> getTableColumns(String tableName) {
    String sql = "SELECT column_name, data_type, character_maximum_length, "
        + "numeric_precision, numeric_scale, is_nullable, column_default "
        + "FROM information_schema.columns "
        + "WHERE table_schema = 'public' AND table_name = ? "
        + "ORDER BY ordinal_position";
    return jdbcTemplate.queryForList(sql, tableName.toLowerCase());
  }

  // ---------------------------------------------------------------
  // Private helpers
  // ---------------------------------------------------------------

  /**
   * Builds the full CREATE TABLE SQL statement including system columns
   * and all user-defined columns from the metadata.
   */
  private String buildCreateTableSql(String tableName, List<TableColumnEntity> columns) {
    StringBuilder sb = new StringBuilder();
    sb.append("CREATE TABLE ").append(escapeIdentifier(tableName)).append(" (\n");
    sb.append(BASE_COLUMNS_SQL);

    for (TableColumnEntity column : columns) {
      sb.append(",\n");
      sb.append("    ").append(escapeIdentifier(column.getCode())).append(" ");
      sb.append(resolveType(column));

      if (Boolean.TRUE.equals(column.getRequired())) {
        sb.append(" NOT NULL");
      }

      String defaultExpr = resolveDefaultValue(column);
      if (defaultExpr != null) {
        sb.append(" DEFAULT ").append(defaultExpr);
      }
    }

    sb.append("\n);");
    return sb.toString();
  }

  /**
   * Builds ALTER TABLE ... ADD COLUMN ... SQL statement.
   */
  private String buildAddColumnSql(String tableName, TableColumnEntity column) {
    StringBuilder sb = new StringBuilder();
    sb.append("ALTER TABLE ").append(escapeIdentifier(tableName));
    sb.append(" ADD COLUMN IF NOT EXISTS ").append(escapeIdentifier(column.getCode()));
    sb.append(" ").append(resolveType(column));

    if (Boolean.TRUE.equals(column.getRequired())) {
      sb.append(" NOT NULL");
    }

    String defaultExpr = resolveDefaultValue(column);
    if (defaultExpr != null) {
      sb.append(" DEFAULT ").append(defaultExpr);
    }

    return sb.toString();
  }

  /**
   * Resolves the PostgreSQL column type from the internal type name.
   * For "decimal", uses precision/scale. For "string", uses max_length.
   */
  private String resolveType(TableColumnEntity column) {
    String typeTemplate = TYPE_MAP.get(column.getType());
    if (typeTemplate == null) {
      throw new IllegalArgumentException("Unsupported column type: " + column.getType());
    }

    switch (column.getType()) {
      case "string":
        int maxLen = (column.getMaxLength() != null && column.getMaxLength() > 0)
            ? column.getMaxLength() : 255;
        return String.format(typeTemplate, maxLen);
      case "decimal":
        int precision = (column.getPrecision() != null && column.getPrecision() > 0)
            ? column.getPrecision() : 15;
        int scale = (column.getScale() != null && column.getScale() >= 0)
            ? column.getScale() : 2;
        return String.format(typeTemplate, precision, scale);
      default:
        return typeTemplate;
    }
  }

  /**
   * Resolves the SQL DEFAULT expression for a column based on its type and defaultValue.
   * Returns null if no default value is set.
   */
  private String resolveDefaultValue(TableColumnEntity column) {
    if (column.getDefaultValue() == null || column.getDefaultValue().isBlank()) {
      return null;
    }

    String dv = column.getDefaultValue().trim();
    switch (column.getType()) {
      case "string":
      case "text":
      case "enum":
        return "'" + dv.replace("'", "''") + "'";
      case "integer":
      case "decimal":
        return dv;
      case "boolean":
        if ("true".equalsIgnoreCase(dv) || "1".equals(dv) || "yes".equalsIgnoreCase(dv)) {
          return "TRUE";
        }
        return "FALSE";
      case "date":
        return "'" + dv + "'::DATE";
      case "datetime":
        return "'" + dv + "'::TIMESTAMP";
      default:
        return "'" + dv.replace("'", "''") + "'";
    }
  }

  /**
   * Escapes a PostgreSQL identifier (table name, column name) by quoting it.
   * This prevents issues with reserved words or special characters.
   */
  private String escapeIdentifier(String identifier) {
    return "\"" + identifier.replace("\"", "\"\"") + "\"";
  }
}
