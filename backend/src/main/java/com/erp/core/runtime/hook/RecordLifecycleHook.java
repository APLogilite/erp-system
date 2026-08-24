package com.erp.core.runtime.hook;

import java.util.Map;
import java.util.UUID;

/**
 * Extension point for table-specific logic during dynamic CRUD operations.
 * Hooks are invoked by DynamicCrudService before create/update.
 */
public interface RecordLifecycleHook {

  /**
   * Returns true if this hook handles the given table.
   */
  boolean supports(String tableName);

  /**
   * Called before a new record is inserted. Implementations may mutate the data map.
   */
  default void beforeCreate(String tableName, Map<String, Object> data, UUID tenantId, UUID userId) {
  }

  /**
   * Called before an existing record is updated. Implementations may mutate the data map.
   */
  default void beforeUpdate(String tableName, UUID recordId, Map<String, Object> data, UUID tenantId, UUID userId) {
  }
}
