package com.erp.core.metadata.exception;

/**
 * Exception thrown when a DDL operation (CREATE TABLE, ALTER TABLE, etc.) fails.
 */
public class DdlExecutionException extends RuntimeException {

  private final String sql;
  private final String tableName;

  public DdlExecutionException(String message, String sql, String tableName) {
    super(message);
    this.sql = sql;
    this.tableName = tableName;
  }

  public DdlExecutionException(String message, String sql, String tableName, Throwable cause) {
    super(message, cause);
    this.sql = sql;
    this.tableName = tableName;
  }

  public String getSql() {
    return sql;
  }

  public String getTableName() {
    return tableName;
  }
}
