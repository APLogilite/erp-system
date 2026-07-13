package com.erp.core.runtime.service;

import com.erp.core.metadata.entity.FormFieldEntity;
import com.erp.core.metadata.entity.FormFieldRuleEntity;
import com.erp.core.metadata.entity.FormFieldValidationEntity;
import com.erp.core.metadata.entity.FormLayoutSectionEntity;
import com.erp.core.metadata.entity.FormSectionFieldEntity;
import com.erp.core.metadata.entity.FormSubFormEntity;
import com.erp.core.metadata.entity.FormTenantRoleEntity;
import com.erp.core.metadata.entity.MetadataModel;
import com.erp.core.metadata.entity.MetadataView;
import com.erp.core.metadata.entity.TableColumnEntity;
import com.erp.core.metadata.repository.FormFieldRepository;
import com.erp.core.metadata.repository.FormFieldRuleRepository;
import com.erp.core.metadata.repository.FormFieldValidationRepository;
import com.erp.core.metadata.repository.FormLayoutSectionRepository;
import com.erp.core.metadata.repository.FormSectionFieldRepository;
import com.erp.core.metadata.repository.FormSubFormRepository;
import com.erp.core.metadata.repository.FormTenantRoleRepository;
import com.erp.core.metadata.repository.MetadataModelRepository;
import com.erp.core.metadata.repository.MetadataViewRepository;
import com.erp.core.metadata.repository.TableColumnRepository;
import com.erp.core.runtime.dto.FieldDefinitionResponse;
import com.erp.core.runtime.dto.FormDefinitionBundleResponse;
import com.erp.core.runtime.dto.LayoutDefinitionResponse;
import com.erp.core.runtime.dto.SubFormDefinitionResponse;
import com.erp.platform.identity.dto.RuntimeContext;
import com.erp.platform.identity.dto.RuntimeContextHolder;
import com.erp.platform.identity.entity.Role;
import com.erp.platform.identity.repository.RoleRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

/**
 * Assembles a complete form definition bundle from normalized metadata tables.
 * Queries sys_metadata_views, sys_form_fields, sys_form_field_rules,
 * sys_form_field_validations, sys_form_layout_sections, sys_form_section_fields,
 * sys_form_sub_forms, sys_table_columns, and sys_metadata_models.
 *
 * <p>Includes authorization checks (tenant isolation, role-based form access)
 * and caching of assembled definitions.
 */
@Service
public class FormDefinitionAssemblyService {

  private static final Logger log = LoggerFactory.getLogger(FormDefinitionAssemblyService.class);

  private static final String ROLE_SYSTEM_ADMIN = "sys_admin";
  private static final String SCOPE_GLOBAL = "global";

  private final MetadataViewRepository metadataViewRepository;
  private final MetadataModelRepository metadataModelRepository;
  private final FormFieldRepository formFieldRepository;
  private final FormFieldRuleRepository formFieldRuleRepository;
  private final FormFieldValidationRepository formFieldValidationRepository;
  private final FormLayoutSectionRepository formLayoutSectionRepository;
  private final FormSectionFieldRepository formSectionFieldRepository;
  private final FormSubFormRepository formSubFormRepository;
  private final TableColumnRepository tableColumnRepository;
  private final FormTenantRoleRepository formTenantRoleRepository;
  private final RoleRepository roleRepository;

  public FormDefinitionAssemblyService(
      MetadataViewRepository metadataViewRepository,
      MetadataModelRepository metadataModelRepository,
      FormFieldRepository formFieldRepository,
      FormFieldRuleRepository formFieldRuleRepository,
      FormFieldValidationRepository formFieldValidationRepository,
      FormLayoutSectionRepository formLayoutSectionRepository,
      FormSectionFieldRepository formSectionFieldRepository,
      FormSubFormRepository formSubFormRepository,
      TableColumnRepository tableColumnRepository,
      FormTenantRoleRepository formTenantRoleRepository,
      RoleRepository roleRepository) {
    this.metadataViewRepository = metadataViewRepository;
    this.metadataModelRepository = metadataModelRepository;
    this.formFieldRepository = formFieldRepository;
    this.formFieldRuleRepository = formFieldRuleRepository;
    this.formFieldValidationRepository = formFieldValidationRepository;
    this.formLayoutSectionRepository = formLayoutSectionRepository;
    this.formSectionFieldRepository = formSectionFieldRepository;
    this.formSubFormRepository = formSubFormRepository;
    this.tableColumnRepository = tableColumnRepository;
    this.formTenantRoleRepository = formTenantRoleRepository;
    this.roleRepository = roleRepository;
  }

  /**
   * Assembles the full form definition bundle for a given form code.
   * Results are cached (invalidated on form definition changes via TASK-007).
   *
   * @param formCode the form code (name field in sys_metadata_views)
   * @param tenantId the current user's tenant ID (for authorization);
   *                 if null, authorization check is skipped (internal calls)
   * @param roleCodes the current user's role codes (for authorization);
   *                  if null/empty, authorization check is skipped
   * @return the assembled bundle, or null if form not found
   * @throws AccessDeniedException if the user is not authorized to access this form
   */
  @Cacheable(value = "formDefinitions", key = "#formCode")
  public FormDefinitionBundleResponse assembleDefinition(String formCode, UUID tenantId, List<String> roleCodes) {
    // 1. Form header
    MetadataView view = metadataViewRepository.findByName(formCode).orElse(null);
    if (view == null || !Boolean.TRUE.equals(view.getIsActive())) {
      return null;
    }

    // 2. Authorization check (skipped for internal calls with null context)
    if (tenantId != null && roleCodes != null) {
      verifyFormAccess(view, tenantId, roleCodes);
    }

    // 3. Model header
    MetadataModel model = metadataModelRepository.findByName(view.getModelName()).orElse(null);

    FormDefinitionBundleResponse bundle = new FormDefinitionBundleResponse();
    bundle.setFormId(view.getId());
    bundle.setFormCode(view.getName());
    // Use the definition JSONB for label if available, otherwise fall back to name
    bundle.setFormLabel(resolveFormLabel(view));
    bundle.setModelName(view.getModelName());
    bundle.setModelLabel(model != null ? model.getLabel() : null);
    bundle.setTableName(model != null ? model.getTableName() : null);
    bundle.setWhereClauseField(view.getWhereClauseField());
    bundle.setWhereClauseOperator(view.getWhereClauseOperator());
    bundle.setWhereClauseValue(view.getWhereClauseValue());

    // 4. Fields with type info from model columns
    List<FormFieldEntity> fields = formFieldRepository.findByFormIdAndIsActiveTrueOrderByPosition(view.getId());
    List<UUID> fieldIds = fields.stream().map(FormFieldEntity::getId).toList();

    // Batch-query rules and validations
    Map<UUID, List<FormFieldRuleEntity>> rulesByField = batchQueryRules(fieldIds);
    Map<UUID, List<FormFieldValidationEntity>> validationsByField = batchQueryValidations(fieldIds);

    // Build column metadata map (column_code → TableColumnEntity)
    Map<String, TableColumnEntity> columnMap = Collections.emptyMap();
    if (model != null) {
      List<TableColumnEntity> columns = tableColumnRepository
          .findByTableIdAndIsActiveTrueOrderByPosition(model.getId());
      columnMap = columns.stream()
          .collect(Collectors.toMap(TableColumnEntity::getCode, c -> c, (a, b) -> a));
    }

    // Assemble field definitions
    List<FieldDefinitionResponse> fieldDefs = new ArrayList<>();
    for (FormFieldEntity field : fields) {
      FieldDefinitionResponse fd = new FieldDefinitionResponse();
      fd.setFieldId(field.getId());
      fd.setColumnCode(field.getColumnCode());
      fd.setLabel(field.getLabelOverride() != null ? field.getLabelOverride()
          : (columnMap.containsKey(field.getColumnCode())
              ? columnMap.get(field.getColumnCode()).getLabel()
              : field.getColumnCode()));
      fd.setVisible(field.getVisible());
      fd.setReadOnly(field.getReadOnly());
      fd.setRequired(field.getRequired());
      fd.setPosition(field.getPosition());
      fd.setDefaultValue(field.getDefaultValue());
      fd.setPlaceholder(field.getPlaceholder());

      // Column type info
      if (columnMap.containsKey(field.getColumnCode())) {
        TableColumnEntity col = columnMap.get(field.getColumnCode());
        fd.setType(col.getType());
        fd.setRelationTable(col.getRelationTable());
        if (col.getEnumOptions() != null) {
          fd.setEnumOptions(col.getEnumOptions().values().stream()
              .map(Object::toString).toList());
        }
      }

      // Rules
      List<FormFieldRuleEntity> fieldRules = rulesByField.getOrDefault(field.getId(), Collections.emptyList());
      fd.setRules(fieldRules.stream().map(r -> {
        FieldDefinitionResponse.RuleDef rd = new FieldDefinitionResponse.RuleDef();
        rd.setRuleId(r.getId());
        rd.setConditionField(r.getConditionField());
        rd.setConditionOperator(r.getConditionOperator());
        rd.setConditionValue(r.getConditionValue());
        rd.setAction(r.getAction());
        rd.setLogicGroup(r.getLogicGroup());
        return rd;
      }).toList());

      // Validations
      List<FormFieldValidationEntity> fieldValidations = validationsByField.getOrDefault(field.getId(), Collections.emptyList());
      fd.setValidations(fieldValidations.stream().map(v -> {
        FieldDefinitionResponse.ValidationDef vd = new FieldDefinitionResponse.ValidationDef();
        vd.setValidationId(v.getId());
        vd.setType(v.getType());
        vd.setValue(v.getValue());
        vd.setMessage(v.getMessage());
        return vd;
      }).toList());

      fieldDefs.add(fd);
    }
    bundle.setFields(fieldDefs);

    // 5. Layout sections
    List<FormLayoutSectionEntity> sections = formLayoutSectionRepository.findByFormIdOrderByPosition(view.getId());
    List<UUID> sectionIds = sections.stream().map(FormLayoutSectionEntity::getId).toList();
    Map<UUID, List<FormSectionFieldEntity>> mappingsBySection = batchQuerySectionFields(sectionIds);

    List<LayoutDefinitionResponse> layoutDefs = new ArrayList<>();
    for (FormLayoutSectionEntity section : sections) {
      LayoutDefinitionResponse ld = new LayoutDefinitionResponse();
      ld.setSectionId(section.getId());
      ld.setCode(section.getCode());
      ld.setLabel(section.getLabel());
      ld.setCollapsible(section.getCollapsible());
      ld.setColumns(section.getColumns());
      ld.setPosition(section.getPosition());

      List<FormSectionFieldEntity> sfList = mappingsBySection.getOrDefault(section.getId(), Collections.emptyList());
      ld.setFieldIds(sfList.stream()
          .sorted((a, b) -> Integer.compare(
              a.getPosition() != null ? a.getPosition() : 0,
              b.getPosition() != null ? b.getPosition() : 0))
          .map(FormSectionFieldEntity::getFieldId)
          .toList());
      layoutDefs.add(ld);
    }
    bundle.setSections(layoutDefs);

    // 6. Sub-forms with one-level-deep child definition info
    List<FormSubFormEntity> subForms = formSubFormRepository.findByParentFormIdOrderByPosition(view.getId());
    bundle.setSubForms(subForms.stream().map(sf -> {
      SubFormDefinitionResponse sd = new SubFormDefinitionResponse();
      sd.setId(sf.getId());
      sd.setRelationCode(sf.getRelationCode());
      sd.setChildFormCode(sf.getChildFormCode());
      sd.setLabel(sf.getLabel());
      sd.setDisplayAs(sf.getDisplayAs());
      sd.setPosition(sf.getPosition());

      // One-level-deep child form definition lookup
      if (sf.getChildFormCode() != null) {
        metadataViewRepository.findByName(sf.getChildFormCode()).ifPresent(childView -> {
          sd.setChildFormId(childView.getId());
          sd.setChildFormLabel(resolveFormLabel(childView));
          sd.setChildFormModelName(childView.getModelName());
          metadataModelRepository.findByName(childView.getModelName()).ifPresent(childModel -> {
            sd.setChildFormTableName(childModel.getTableName());
          });
        });
      }
      return sd;
    }).toList());

    return bundle;
  }

  /**
   * Verifies that the current user is authorized to access this form.
   *
   * <p>Rules:
   * <ul>
   *   <li>sys_admin can access any form globally</li>
   *   <li>For global forms: the user's tenant must have at least one of the user's
   *       roles assigned to the form via sys_form_tenant_role</li>
   *   <li>For tenant-scoped forms: the form must belong to the user's tenant</li>
   * </ul>
   *
   * @param view the form definition
   * @param tenantId the current user's tenant ID
   * @param roleCodes the current user's role codes
   * @throws AccessDeniedException if access is denied
   */
  private void verifyFormAccess(MetadataView view, UUID tenantId, List<String> roleCodes) {
    // System admin bypasses all checks
    if (roleCodes != null && roleCodes.contains(ROLE_SYSTEM_ADMIN)) {
      return;
    }

    if (SCOPE_GLOBAL.equals(view.getScope())) {
      // Global form: verify the user's tenant has assigned at least one
      // of the user's roles to this form
      if (tenantId == null || roleCodes == null || roleCodes.isEmpty()) {
        throw new AccessDeniedException("Access denied to global form: " + view.getName());
      }

      List<FormTenantRoleEntity> tenantRoles = formTenantRoleRepository
          .findByFormIdAndTenantId(view.getId(), tenantId);

      // Convert user's role codes to UUIDs for comparison
      List<Role> userRoleEntities = roleRepository.findByCodeIn(roleCodes);
      Set<UUID> userRoleIds = userRoleEntities.stream()
          .map(Role::getId)
          .collect(Collectors.toSet());

      boolean hasAccess = tenantRoles.stream()
          .anyMatch(tr -> userRoleIds.contains(tr.getRoleId()));

      if (!hasAccess) {
        if (tenantRoles.isEmpty()) {
          throw new AccessDeniedException(
              "Access denied: no role assignments configured for this form in your tenant");
        }
        throw new AccessDeniedException("Access denied to global form: " + view.getName());
      }
    } else {
      // Tenant form: verify the form belongs to the user's tenant
      if (view.getTenantId() == null) {
        throw new AccessDeniedException(
            "Access denied: tenant-scoped form missing tenant assignment");
      }
      if (tenantId == null || !tenantId.equals(view.getTenantId())) {
        throw new AccessDeniedException(
            "Access denied: this form belongs to a different tenant");
      }
    }
  }

  /**
   * Resolves a human-readable label for a form.
   * Checks the definition JSONB for a "label" key first,
   * otherwise falls back to the name/code.
   */
  private String resolveFormLabel(MetadataView view) {
    if (view.getDefinition() != null && view.getDefinition().containsKey("label")) {
      Object labelObj = view.getDefinition().get("label");
      if (labelObj != null) {
        return labelObj.toString();
      }
    }
    return view.getName();
  }

  private Map<UUID, List<FormFieldRuleEntity>> batchQueryRules(List<UUID> fieldIds) {
    if (fieldIds.isEmpty()) return Collections.emptyMap();
    return formFieldRuleRepository.findByFieldIdIn(fieldIds).stream()
        .collect(Collectors.groupingBy(FormFieldRuleEntity::getFieldId));
  }

  private Map<UUID, List<FormFieldValidationEntity>> batchQueryValidations(List<UUID> fieldIds) {
    if (fieldIds.isEmpty()) return Collections.emptyMap();
    return formFieldValidationRepository.findByFieldIdIn(fieldIds).stream()
        .collect(Collectors.groupingBy(FormFieldValidationEntity::getFieldId));
  }

  private Map<UUID, List<FormSectionFieldEntity>> batchQuerySectionFields(List<UUID> sectionIds) {
    if (sectionIds.isEmpty()) return Collections.emptyMap();
    return formSectionFieldRepository.findBySectionIdIn(sectionIds).stream()
        .collect(Collectors.groupingBy(FormSectionFieldEntity::getSectionId));
  }
}
