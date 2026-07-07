package com.erp.core.runtime.exception;

/**
 * Exception thrown when a requested record is not found in a dynamic table.
 * Caller should interpret this as a 404 response.
 */
public class RecordNotFoundException extends RuntimeException {

  private final String tableName;
  private final Object recordId;

  public RecordNotFoundException(String tableName, Object recordId) {
    super("Record not found in table '" + tableName + "' with id: " + recordId);
    this.tableName = tableName;
    this.recordId = recordId;
  }

  public RecordNotFoundException(String tableName, Object recordId, String message) {
    super(message);
    this.tableName = tableName;
    this.recordId = recordId;
  }

  public String getTableName() {
    return tableName;
  }

  public Object getRecordId() {
    return recordId;
  }
}
