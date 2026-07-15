package com.erp.core.runtime.service;

import com.erp.core.metadata.entity.FormRoleFilterEntity;
import com.erp.core.metadata.repository.FormRoleFilterRepository;
import com.erp.core.runtime.dto.FieldDefinitionResponse;
import com.erp.core.runtime.dto.FormDefinitionBundleResponse;
import com.erp.platform.identity.dto.RuntimeContext;
import com.erp.platform.identity.entity.Role;
import com.erp.platform.identity.repository.RoleRepository;
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
 * row filters (with dynamic variable resolution), sub-form child records,
 * and breadcrumb context.
 */
@Service
public class RecordCrudService {

  private static final Logger log = LoggerFactory.getLogger(RecordCrudService.class);

  private static final String VAR_CURRENT_USER_ID = "{current_user_id}";
  private static final String VAR_CURRENT_TENANT_ID = "{current_tenant_id}";

  private final DynamicCrudService dynamicCrudService;
  private final FormDefinitionAssemblyService assemblyService;
  private final RecordValidationService validationService;
  private final FormRoleFilterRepository formRoleFilterRepository;
  private final RoleRepository roleRepository;

  public RecordCrudService(
      DynamicCrudService dynamicCrudService,
      FormDefinitionAssemblyService assemblyService,
      RecordValidationService validationService,
      FormRoleFilterRepository formRoleFilterRepository,
      RoleRepository roleRepository) {
    this.dynamicCrudService = dynamicCrudService;
    this.assemblyService = assemblyService;
    this.validationService = validationService;
    this.formRoleFilterRepository = formRoleFilterRepository;
    this.roleRepository = roleRepository;
  }

  /**
   * List records for a form with tenant isolation, where clause, and row filters.
   */
  public Map<String, Object> listRecords(
      String formCode,
      UUID tenantId,
      List<String> roleCodes,
      UUID userId,
      int page,
      int size,
      String sortField,
      String sortDir) {

    FormDefinitionBundleResponse def = assemblyService.assembleDefinition(formCode, null, null);
    if (def == null) {
      throw new IllegalArgumentException("Form not found: " + formCode);
    }

    String tableName = def.getTableName();
    if (tableName == null) {
      throw new IllegalArgumentException("Form has no associated table: " + formCode);
    }

    List<DynamicCrudService.RowFilter> rowFilters = buildRowFilters(
        def.getFormId(), roleCodes, tenantId, userId);

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
      List<String> roleCodes,
      UUID userId) {

    FormDefinitionBundleResponse def = assemblyService.assembleDefinition(formCode, null, null);
    if (def == null) {
      throw new IllegalArgumentException("Form not found: " + formCode);
    }

    String tableName = def.getTableName();
    List<DynamicCrudService.RowFilter> rowFilters = buildRowFilters(
        def.getFormId(), roleCodes, tenantId, userId);

    // Get the main record
    Map<String, Object> record = dynamicCrudService.getRecord(tableName, recordId, tenantId, rowFilters);
    if (record == null) {
      return null;
    }

    // Load sub-form child records
    Map<String, Object> subFormRecords = new LinkedHashMap<>();
    if (def.getSubForms() != null) {
      for (var subForm : def.getSubForms()) {
        FormDefinitionBundleResponse childDef = assemblyService.assembleDefinition(subForm.getChildFormCode(), null, null);
        if (childDef != null && childDef.getTableName() != null) {
          List<DynamicCrudService.RowFilter> childFilters = buildRowFilters(
              childDef.getFormId(), roleCodes, tenantId, userId);
          List<Map<String, Object>> children = dynamicCrudService.getChildRecords(
              childDef.getTableName(),
              subForm.getRelationCode(),
              recordId,
              tenantId,
              null);
          // Apply row filters to children
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
      List<String> roleCodes) {

    FormDefinitionBundleResponse def = assemblyService.assembleDefinition(formCode, null, null);
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
      List<String> roleCodes) {

    FormDefinitionBundleResponse def = assemblyService.assembleDefinition(formCode, null, null);
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
    FormDefinitionBundleResponse def = assemblyService.assembleDefinition(formCode, null, null);
    if (def == null) {
      throw new IllegalArgumentException("Form not found: " + formCode);
    }
    dynamicCrudService.deleteRecord(def.getTableName(), recordId, tenantId);
  }

  // ---------------------------------------------------------------
  // Private helpers
  // ---------------------------------------------------------------

  /**
   * Builds row filter conditions by loading configured filters from
   * sys_form_role_filters and resolving dynamic variables against
   * the current user's context.
   */
  private List<DynamicCrudService.RowFilter> buildRowFilters(
      UUID formId,
      List<String> roleCodes,
      UUID tenantId,
      UUID userId) {

    if (roleCodes == null || roleCodes.isEmpty()) {
      return Collections.emptyList();
    }

    // Convert role codes to role UUIDs
    List<Role> userRoles = roleRepository.findByCodeIn(roleCodes);
    List<UUID> userRoleIds = userRoles.stream().map(Role::getId).collect(Collectors.toList());
    if (userRoleIds.isEmpty()) {
      return Collections.emptyList();
    }

    // Load row filters for this form + user's roles
    List<FormRoleFilterEntity> filters = formRoleFilterRepository
        .findByFormIdAndRoleIdIn(formId, userRoleIds);

    if (filters.isEmpty()) {
      return Collections.emptyList();
    }

    // Sort by position and resolve dynamic variables
    List<DynamicCrudService.RowFilter> rowFilters = filters.stream()
        .sorted((a, b) -> Integer.compare(
            a.getPosition() != null ? a.getPosition() : 0,
            b.getPosition() != null ? b.getPosition() : 0))
        .map(f -> {
          String resolvedValue = resolveDynamicVariables(
              f.getConditionValue(), tenantId, userId);
          return new DynamicCrudService.RowFilter(
              f.getConditionField(),
              f.getConditionOperator(),
              resolvedValue);
        })
        .collect(Collectors.toList());

    log.debug("Resolved {} row filters for formId={}, user roles={}",
        rowFilters.size(), formId, roleCodes);

    return rowFilters;
  }

  /**
   * Resolves dynamic variable placeholders in filter condition values.
   *
   * <p>Supported variables:
   * <ul>
   *   <li>{@code {current_user_id}} — replaced with the user's UUID</li>
   *   <li>{@code {current_tenant_id}} — replaced with the tenant UUID</li>
   * </ul>
   *
   * <p>Unsupported variables are left as-is (will not match any data).
   */
  private String resolveDynamicVariables(String value, UUID tenantId, UUID userId) {
    if (value == null) {
      return null;
    }
    String resolved = value;
    if (userId != null) {
      resolved = resolved.replace(VAR_CURRENT_USER_ID, userId.toString());
    }
    if (tenantId != null) {
      resolved = resolved.replace(VAR_CURRENT_TENANT_ID, tenantId.toString());
    }
    return resolved;
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
