package com.erp.core.metadata.service;

import com.erp.core.metadata.dto.FormCreateRequest;
import com.erp.core.metadata.dto.FormDesignDto;
import com.erp.core.metadata.dto.FormFieldDto;
import com.erp.core.metadata.dto.FormLayoutSectionDto;
import com.erp.core.metadata.dto.FormSubFormDto;
import com.erp.core.metadata.dto.FormUpdateRequest;
import com.erp.core.metadata.entity.FormFieldEntity;
import com.erp.core.metadata.entity.FormFieldRuleEntity;
import com.erp.core.metadata.entity.FormFieldValidationEntity;
import com.erp.core.metadata.entity.FormSubFormEntity;
import com.erp.core.metadata.entity.MetadataModel;
import com.erp.core.metadata.entity.MetadataView;
import com.erp.core.metadata.repository.FormFieldRepository;
import com.erp.core.metadata.repository.FormFieldRuleRepository;
import com.erp.core.metadata.repository.FormFieldValidationRepository;
import com.erp.core.metadata.repository.FormSubFormRepository;
import com.erp.core.metadata.repository.MetadataModelRepository;
import com.erp.core.metadata.repository.MetadataViewRepository;
import com.erp.platform.identity.dto.RuntimeContext;
import com.erp.platform.identity.dto.RuntimeContextHolder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for creating, reading, updating, deleting, and cloning
 * form definitions (MetadataView) with all their associated configuration
 * (fields, rules, validations, layout, sub-forms).
 */
@Service
public class FormDesignerService {

  private final MetadataViewRepository metadataViewRepository;
  private final MetadataModelRepository metadataModelRepository;
  private final FormFieldRepository formFieldRepository;
  private final FormFieldRuleRepository formFieldRuleRepository;
  private final FormFieldValidationRepository formFieldValidationRepository;
  private final FormSubFormRepository formSubFormRepository;
  private final FormFieldService formFieldService;
  private final FormLayoutService formLayoutService;

  public FormDesignerService(
      MetadataViewRepository metadataViewRepository,
      MetadataModelRepository metadataModelRepository,
      FormFieldRepository formFieldRepository,
      FormFieldRuleRepository formFieldRuleRepository,
      FormFieldValidationRepository formFieldValidationRepository,
      FormSubFormRepository formSubFormRepository,
      FormFieldService formFieldService,
      FormLayoutService formLayoutService) {
    this.metadataViewRepository = metadataViewRepository;
    this.metadataModelRepository = metadataModelRepository;
    this.formFieldRepository = formFieldRepository;
    this.formFieldRuleRepository = formFieldRuleRepository;
    this.formFieldValidationRepository = formFieldValidationRepository;
    this.formSubFormRepository = formSubFormRepository;
    this.formFieldService = formFieldService;
    this.formLayoutService = formLayoutService;
  }

  /**
   * List all forms, optionally filtered by scope and tenant.
   */
  public List<FormDesignDto> listForms(String scope, UUID tenantId) {
    List<MetadataView> views;
    UUID currentTenantId = getCurrentTenantId();
    List<String> currentRoles = getCurrentRoles();

    if (scope != null && tenantId != null) {
      views = metadataViewRepository.findAll().stream()
          .filter(v -> "form".equals(v.getType()))
          .filter(v -> scope.equals(v.getScope()))
          .filter(v -> tenantId.equals(v.getTenantId()) || v.getTenantId() == null)
          .toList();
    } else {
      views = metadataViewRepository.findAll().stream()
          .filter(v -> "form".equals(v.getType()))
          .toList();
    }

    // Tenant Admin: filter to own tenant forms + global forms
    if (currentTenantId != null && !currentRoles.contains("sys_admin")) {
      views = views.stream()
          .filter(v -> v.getTenantId() == null // global
              || v.getTenantId().equals(currentTenantId))
          .toList();
    }

    return views.stream().map(this::toFormDesignDto).toList();
  }

  /**
   * Get a single form with all its configuration.
   */
  public FormDesignDto getForm(UUID formId) {
    MetadataView view = metadataViewRepository.findById(formId)
        .orElseThrow(() -> new IllegalArgumentException("Form not found: " + formId));
    checkTenantAccess(view);
    return toFormDesignDto(view);
  }

  /**
   * Create a new form definition.
   */
  @Transactional
  public FormDesignDto createForm(FormCreateRequest request) {
    UUID currentTenantId = getCurrentTenantId();
    List<String> currentRoles = getCurrentRoles();

    // Validate unique form code
    if (metadataViewRepository.findByName(request.getName()).isPresent()) {
      throw new IllegalArgumentException("Form code already exists: " + request.getName());
    }

    // Non-system-admin: enforce tenant scope
    String scope = request.getScope() != null ? request.getScope() : "tenant";
    if (currentTenantId != null && !currentRoles.contains("sys_admin")) {
      if ("global".equals(scope)) {
        throw new SecurityException("Only System Admin can create global forms");
      }
      scope = "tenant";
    }

    // Validate model exists
    if (request.getModelName() != null) {
      metadataModelRepository.findByName(request.getModelName())
          .orElseThrow(() -> new IllegalArgumentException("Model not found: " + request.getModelName()));
    }

    MetadataView view = new MetadataView();
    view.setName(request.getName());
    view.setModelName(request.getModelName());
    view.setType("form");
    view.setScope(scope);
    view.setDescription(request.getDescription());
    view.setWhereClauseField(request.getWhereClauseField());
    view.setWhereClauseOperator(request.getWhereClauseOperator());
    view.setWhereClauseValue(request.getWhereClauseValue());
    view.setDefinition(new HashMap<>());

    // Set tenant ID for tenant-scoped forms
    if ("tenant".equals(scope) && currentTenantId != null) {
      view.setTenantId(currentTenantId);
    }

    MetadataView saved = metadataViewRepository.save(view);
    return toFormDesignDto(saved);
  }

  /**
   * Update the form header (label, description, where clause).
   */
  @Transactional
  public FormDesignDto updateForm(UUID formId, FormUpdateRequest request) {
    MetadataView view = metadataViewRepository.findById(formId)
        .orElseThrow(() -> new IllegalArgumentException("Form not found: " + formId));
    checkTenantOwnership(view);

    if (request.getLabel() != null) {
      view.setName(request.getLabel());
    }
    if (request.getDescription() != null) {
      view.setDescription(request.getDescription());
    }
    if (request.getWhereClauseField() != null) {
      view.setWhereClauseField(request.getWhereClauseField());
    }
    if (request.getWhereClauseOperator() != null) {
      view.setWhereClauseOperator(request.getWhereClauseOperator());
    }
    if (request.getWhereClauseValue() != null) {
      view.setWhereClauseValue(request.getWhereClauseValue());
    }

    MetadataView saved = metadataViewRepository.save(view);
    return toFormDesignDto(saved);
  }

  /**
   * Delete a form and cascade-delete all associated fields, rules,
   * validations, layout sections, and sub-form entries.
   */
  @Transactional
  public void deleteForm(UUID formId) {
    MetadataView view = metadataViewRepository.findById(formId)
        .orElseThrow(() -> new IllegalArgumentException("Form not found: " + formId));
    checkTenantOwnership(view);

    // Cascade delete: sub-forms
    List<FormSubFormEntity> subForms = formSubFormRepository.findByParentFormIdOrderByPosition(formId);
    formSubFormRepository.deleteAll(subForms);

    // Cascade delete: fields (and their rules/validations via DB cascade)
    List<FormFieldEntity> fields = formFieldRepository.findByFormIdOrderByPosition(formId);
    for (FormFieldEntity field : fields) {
      // Delete rules and validations explicitly
      List<FormFieldRuleEntity> rules = formFieldRuleRepository.findByFieldId(field.getId());
      formFieldRuleRepository.deleteAll(rules);
      List<FormFieldValidationEntity> validations = formFieldValidationRepository.findByFieldId(field.getId());
      formFieldValidationRepository.deleteAll(validations);
    }
    formFieldRepository.deleteAll(fields);

    // Delete the form itself
    metadataViewRepository.delete(view);
  }

  /**
   * Clone a form definition including all fields, rules, validations,
   * and layout. Role assignments are NOT copied.
   */
  @Transactional
  public FormDesignDto cloneForm(UUID sourceFormId, String newName, String newLabel) {
    MetadataView source = metadataViewRepository.findById(sourceFormId)
        .orElseThrow(() -> new IllegalArgumentException("Source form not found: " + sourceFormId));

    // Validate unique name
    if (metadataViewRepository.findByName(newName).isPresent()) {
      throw new IllegalArgumentException("Form code already exists: " + newName);
    }

    // Create new form header
    MetadataView clone = new MetadataView();
    clone.setName(newName);
    clone.setModelName(source.getModelName());
    clone.setType("form");
    clone.setScope(source.getScope());
    clone.setTenantId(source.getTenantId());
    clone.setDescription(source.getDescription());
    clone.setWhereClauseField(source.getWhereClauseField());
    clone.setWhereClauseOperator(source.getWhereClauseOperator());
    clone.setWhereClauseValue(source.getWhereClauseValue());
    clone.setDefinition(new HashMap<>(source.getDefinition()));
    MetadataView savedClone = metadataViewRepository.save(clone);

    // Clone fields (deep copy)
    List<FormFieldEntity> clonedFields = formFieldService.cloneFields(sourceFormId, savedClone.getId());

    // Build old-field-ID to new-field-ID map
    List<FormFieldEntity> sourceFields = formFieldRepository.findByFormIdAndIsActiveTrueOrderByPosition(sourceFormId);
    Map<UUID, UUID> oldToNewFieldIdMap = new HashMap<>();
    for (int i = 0; i < sourceFields.size() && i < clonedFields.size(); i++) {
      oldToNewFieldIdMap.put(sourceFields.get(i).getId(), clonedFields.get(i).getId());
    }

    // Clone field rules
    for (FormFieldEntity clonedField : clonedFields) {
      UUID originalFieldId = oldToNewFieldIdMap.entrySet().stream()
          .filter(e -> e.getValue().equals(clonedField.getId()))
          .map(Map.Entry::getKey)
          .findFirst().orElse(null);
      if (originalFieldId != null) {
        List<FormFieldRuleEntity> sourceRules = formFieldRuleRepository.findByFieldId(originalFieldId);
        for (FormFieldRuleEntity sourceRule : sourceRules) {
          FormFieldRuleEntity ruleClone = new FormFieldRuleEntity();
          ruleClone.setFieldId(clonedField.getId());
          ruleClone.setConditionField(sourceRule.getConditionField());
          ruleClone.setConditionOperator(sourceRule.getConditionOperator());
          ruleClone.setConditionValue(sourceRule.getConditionValue());
          ruleClone.setAction(sourceRule.getAction());
          ruleClone.setLogicGroup(sourceRule.getLogicGroup());
          ruleClone.setPosition(sourceRule.getPosition());
          formFieldRuleRepository.save(ruleClone);
        }
      }
    }

    // Clone field validations
    for (FormFieldEntity clonedField : clonedFields) {
      UUID originalFieldId = oldToNewFieldIdMap.entrySet().stream()
          .filter(e -> e.getValue().equals(clonedField.getId()))
          .map(Map.Entry::getKey)
          .findFirst().orElse(null);
      if (originalFieldId != null) {
        List<FormFieldValidationEntity> sourceValidations = formFieldValidationRepository.findByFieldId(originalFieldId);
        for (FormFieldValidationEntity sourceVal : sourceValidations) {
          FormFieldValidationEntity valClone = new FormFieldValidationEntity();
          valClone.setFieldId(clonedField.getId());
          valClone.setType(sourceVal.getType());
          valClone.setValue(sourceVal.getValue());
          valClone.setMessage(sourceVal.getMessage());
          valClone.setPosition(sourceVal.getPosition());
          formFieldValidationRepository.save(valClone);
        }
      }
    }

    // Clone layout sections
    formLayoutService.cloneLayout(sourceFormId, savedClone.getId(), oldToNewFieldIdMap);

    // Clone sub-forms (references to form definitions by code — copy directly)
    List<FormSubFormEntity> sourceSubForms = formSubFormRepository.findByParentFormIdOrderByPosition(sourceFormId);
    for (FormSubFormEntity sourceSubForm : sourceSubForms) {
      FormSubFormEntity subFormClone = new FormSubFormEntity();
      subFormClone.setParentFormId(savedClone.getId());
      subFormClone.setRelationCode(sourceSubForm.getRelationCode());
      subFormClone.setChildFormCode(sourceSubForm.getChildFormCode());
      subFormClone.setLabel(sourceSubForm.getLabel());
      subFormClone.setDisplayAs(sourceSubForm.getDisplayAs());
      subFormClone.setPosition(sourceSubForm.getPosition());
      formSubFormRepository.save(subFormClone);
    }

    return toFormDesignDto(savedClone);
  }

  // ---------------------------------------------------------------
  // Authorization helpers
  // ---------------------------------------------------------------

  private UUID getCurrentTenantId() {
    RuntimeContext ctx = RuntimeContextHolder.get();
    return ctx != null ? ctx.getTenantId() : null;
  }

  private List<String> getCurrentRoles() {
    RuntimeContext ctx = RuntimeContextHolder.get();
    return ctx != null && ctx.getRoles() != null ? ctx.getRoles() : List.of();
  }

  private void checkTenantAccess(MetadataView view) {
    UUID currentTenantId = getCurrentTenantId();
    List<String> roles = getCurrentRoles();
    if (roles.contains("sys_admin")) return;
    if (view.getTenantId() == null) return;
    if (currentTenantId != null && currentTenantId.equals(view.getTenantId())) return;
    throw new SecurityException("Access denied to form " + view.getName());
  }

  private void checkTenantOwnership(MetadataView view) {
    UUID currentTenantId = getCurrentTenantId();
    List<String> roles = getCurrentRoles();
    if (roles.contains("sys_admin")) return;
    if (view.getTenantId() == null) {
      throw new SecurityException("Cannot modify global form " + view.getName());
    }
    if (currentTenantId == null || !currentTenantId.equals(view.getTenantId())) {
      throw new SecurityException("Cannot modify form " + view.getName());
    }
  }

  /**
   * Get tables available for form creation.
   */
  public List<Map<String, Object>> getAvailableTables() {
    List<MetadataModel> models = metadataModelRepository.findAll();
    return models.stream()
        .filter(m -> Boolean.TRUE.equals(m.getIsActive()))
        .map(m -> {
          Map<String, Object> entry = new HashMap<>();
          entry.put("id", m.getId());
          entry.put("name", m.getName());
          entry.put("label", m.getLabel());
          entry.put("pluralLabel", m.getPluralLabel());
          entry.put("tableName", m.getTableName());
          return entry;
        })
        .toList();
  }

  /**
   * Build a full FormDesignDto from a MetadataView entity.
   */
  private FormDesignDto toFormDesignDto(MetadataView view) {
    FormDesignDto dto = new FormDesignDto();
    dto.setId(view.getId());
    dto.setName(view.getName());
    dto.setModelName(view.getModelName());
    dto.setType(view.getType());
    dto.setScope(view.getScope());
    dto.setTenantId(view.getTenantId());
    dto.setDescription(view.getDescription());
    dto.setWhereClauseField(view.getWhereClauseField());
    dto.setWhereClauseOperator(view.getWhereClauseOperator());
    dto.setWhereClauseValue(view.getWhereClauseValue());
    dto.setIsActive(view.getIsActive());

    // Load fields
    List<FormFieldDto> fields = formFieldService.getFields(view.getId());
    dto.setFields(fields);

    // Load sections
    List<FormLayoutSectionDto> sections = formLayoutService.getSections(view.getId());
    dto.setSections(sections);

    // Load sub-forms
    List<FormSubFormEntity> subFormEntities = formSubFormRepository.findByParentFormIdOrderByPosition(view.getId());
    List<FormSubFormDto> subFormDtos = subFormEntities.stream().map(sf -> {
      FormSubFormDto sfd = new FormSubFormDto();
      sfd.setId(sf.getId());
      sfd.setParentFormId(sf.getParentFormId());
      sfd.setRelationCode(sf.getRelationCode());
      sfd.setChildFormCode(sf.getChildFormCode());
      sfd.setLabel(sf.getLabel());
      sfd.setDisplayAs(sf.getDisplayAs());
      sfd.setPosition(sf.getPosition());
      return sfd;
    }).toList();
    dto.setSubForms(subFormDtos);

    return dto;
  }
}
