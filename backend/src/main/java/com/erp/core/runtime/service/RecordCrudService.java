package com.erp.core.runtime.service;

import com.erp.core.runtime.dto.FieldDefinitionResponse;
import com.erp.core.runtime.dto.FormDefinitionBundleResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Orchestrates CRUD operations on dynamic form records.
 * Wires form definitions, tenant isolation, where clause enforcement,
 * row filters, sub-form child records, and breadcrumb context.
 */
@Service
public class RecordCrudService {

  private static final Logger log = LoggerFactory.getLogger(RecordCrudService.class);

  private final DynamicCrudService dynamicCrudService;
  private final FormDefinitionAssemblyService assemblyService;
  private final RecordValidationService validationService;

  public RecordCrudService(
      DynamicCrudService dynamicCrudService,
      FormDefinitionAssemblyService assemblyService,
      RecordValidationService validationService) {
    this.dynamicCrudService = dynamicCrudService;
    this.assemblyService = assemblyService;
    this.validationService = validationService;
  }

  /**
   * List records for a form with tenant isolation, where clause, and row filters.
   */
  public Map<String, Object> listRecords(
      String formCode,
      UUID tenantId,
      List<UUID> roleIds,
      int page,
      int size,
      String sortField,
      String sortDir) {

    FormDefinitionBundleResponse def = assemblyService.assembleDefinition(formCode);
    if (def == null) {
      throw new IllegalArgumentException("Form not found: " + formCode);
    }

    String tableName = def.getTableName();
    if (tableName == null) {
      throw new IllegalArgumentException("Form has no associated table: " + formCode);
    }

    List<DynamicCrudService.RowFilter> rowFilters = buildRowFilters(def.getFormId(), roleIds);

    return dynamicCrudService.listRecords(
        tableName,
        def.getWhereClauseField(),
        def.getWhereClauseValue(),
        tenantId,
        page,
        size,
        sortField,
        sortDir,
        rowFilters);
  }

  /**
   * Get a single record with sub-form children and breadcrumb context.
   */
  public Map<String, Object> getRecordWithContext(
      String formCode,
      UUID recordId,
      UUID tenantId,
      List<UUID> roleIds) {

    FormDefinitionBundleResponse def = assemblyService.assembleDefinition(formCode);
    if (def == null) {
      throw new IllegalArgumentException("Form not found: " + formCode);
    }

    String tableName = def.getTableName();
    List<DynamicCrudService.RowFilter> rowFilters = buildRowFilters(def.getFormId(), roleIds);

    // Get the main record
    Map<String, Object> record = dynamicCrudService.getRecord(tableName, recordId, tenantId, rowFilters);
    if (record == null) {
      return null;
    }

    // Load sub-form child records
    Map<String, Object> subFormRecords = new LinkedHashMap<>();
    if (def.getSubForms() != null) {
      for (var subForm : def.getSubForms()) {
        FormDefinitionBundleResponse childDef = assemblyService.assembleDefinition(subForm.getChildFormCode());
        if (childDef != null && childDef.getTableName() != null) {
          List<DynamicCrudService.RowFilter> childFilters = buildRowFilters(childDef.getFormId(), roleIds);
          List<Map<String, Object>> children = dynamicCrudService.getChildRecords(
              childDef.getTableName(),
              subForm.getRelationCode(),
              recordId,
              tenantId);
          // Filter children with row filters
          List<Map<String, Object>> filtered = new ArrayList<>();
          for (Map<String, Object> child : children) {
            Map<String, Object> filteredChild = dynamicCrudService.getRecord(
                childDef.getTableName(),
                (UUID) child.get("id"),
                tenantId,
                childFilters);
            if (filteredChild != null) {
              filtered.add(filteredChild);
            }
          }
          subFormRecords.put(subForm.getLabel(), filtered);
        }
      }
    }

    Map<String, Object> result = new LinkedHashMap<>();
    result.put("record", record);
    result.put("subFormRecords", subFormRecords);
    result.put("breadcrumb", buildBreadcrumb(formCode, def.getFormLabel(), recordId, record));

    return result;
  }

  /**
   * Create a record with validation, where clause enforcement, and auto-set fields.
   */
  public Map<String, Object> createRecord(
      String formCode,
      Map<String, Object> data,
      UUID tenantId,
      UUID userId,
      List<UUID> roleIds) {

    FormDefinitionBundleResponse def = assemblyService.assembleDefinition(formCode);
    if (def == null) {
      throw new IllegalArgumentException("Form not found: " + formCode);
    }

    // Validate
    List<String> errors = validationService.validate(data, def.getFields(), true);
    if (!errors.isEmpty()) {
      throw new IllegalArgumentException("Validation failed: " + String.join("; ", errors));
    }

    // Auto-set where clause field value
    if (def.getWhereClauseField() != null && def.getWhereClauseValue() != null
        && !data.containsKey(def.getWhereClauseField())) {
      data.put(def.getWhereClauseField(), def.getWhereClauseValue());
    }

    return dynamicCrudService.createRecord(def.getTableName(), data, tenantId, userId);
  }

  /**
   * Update a record with validation and read-only enforcement.
   */
  public Map<String, Object> updateRecord(
      String formCode,
      UUID recordId,
      Map<String, Object> data,
      UUID tenantId,
      UUID userId,
      List<UUID> roleIds) {

    FormDefinitionBundleResponse def = assemblyService.assembleDefinition(formCode);
    if (def == null) {
      throw new IllegalArgumentException("Form not found: " + formCode);
    }

    // Strip read-only fields
    Map<String, Object> cleanedData = validationService.stripReadOnly(data, def.getFields());

    // Validate
    List<String> errors = validationService.validate(cleanedData, def.getFields(), false);
    if (!errors.isEmpty()) {
      throw new IllegalArgumentException("Validation failed: " + String.join("; ", errors));
    }

    Set<String> readOnlyFieldNames = def.getFields().stream()
        .filter(f -> Boolean.TRUE.equals(f.getReadOnly()))
        .map(FieldDefinitionResponse::getColumnCode)
        .collect(Collectors.toSet());

    return dynamicCrudService.updateRecord(
        def.getTableName(), recordId, cleanedData, tenantId, userId, readOnlyFieldNames);
  }

  /**
   * Soft-delete a record.
   */
  public void deleteRecord(String formCode, UUID recordId, UUID tenantId) {
    FormDefinitionBundleResponse def = assemblyService.assembleDefinition(formCode);
    if (def == null) {
      throw new IllegalArgumentException("Form not found: " + formCode);
    }
    dynamicCrudService.deleteRecord(def.getTableName(), recordId, tenantId);
  }

  // ---------------------------------------------------------------
  // Private helpers
  // ---------------------------------------------------------------

  private List<DynamicCrudService.RowFilter> buildRowFilters(UUID formId, List<UUID> roleIds) {
    // Row filters would be loaded from sys_form_role_filters.
    // For now, return empty list. Full implementation requires
    // FormRoleFilterRepository and JWT variable resolution.
    return Collections.emptyList();
  }

  private List<Map<String, Object>> buildBreadcrumb(
      String formCode, String formLabel, UUID recordId, Map<String, Object> record) {

    List<Map<String, Object>> breadcrumb = new ArrayList<>();
    Map<String, Object> crumb = new LinkedHashMap<>();
    crumb.put("formCode", formCode);
    crumb.put("label", formLabel);
    crumb.put("recordId", recordId.toString());
    breadcrumb.add(crumb);
    return breadcrumb;
  }
}
