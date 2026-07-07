package com.erp.core.runtime.service;

import com.erp.core.metadata.entity.FormFieldEntity;
import com.erp.core.metadata.entity.FormFieldRuleEntity;
import com.erp.core.metadata.entity.FormFieldValidationEntity;
import com.erp.core.metadata.entity.FormLayoutSectionEntity;
import com.erp.core.metadata.entity.FormSectionFieldEntity;
import com.erp.core.metadata.entity.FormSubFormEntity;
import com.erp.core.metadata.entity.MetadataModel;
import com.erp.core.metadata.entity.MetadataView;
import com.erp.core.metadata.entity.TableColumnEntity;
import com.erp.core.metadata.repository.FormFieldRepository;
import com.erp.core.metadata.repository.FormFieldRuleRepository;
import com.erp.core.metadata.repository.FormFieldValidationRepository;
import com.erp.core.metadata.repository.FormLayoutSectionRepository;
import com.erp.core.metadata.repository.FormSectionFieldRepository;
import com.erp.core.metadata.repository.FormSubFormRepository;
import com.erp.core.metadata.repository.MetadataModelRepository;
import com.erp.core.metadata.repository.MetadataViewRepository;
import com.erp.core.metadata.repository.TableColumnRepository;
import com.erp.core.runtime.dto.FieldDefinitionResponse;
import com.erp.core.runtime.dto.FormDefinitionBundleResponse;
import com.erp.core.runtime.dto.LayoutDefinitionResponse;
import com.erp.core.runtime.dto.SubFormDefinitionResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * Assembles a complete form definition bundle from normalized metadata tables.
 * Queries sys_metadata_views, sys_form_fields, sys_form_field_rules,
 * sys_form_field_validations, sys_form_layout_sections, sys_form_section_fields,
 * sys_form_sub_forms, sys_table_columns, and sys_metadata_models.
 */
@Service
public class FormDefinitionAssemblyService {

  private final MetadataViewRepository metadataViewRepository;
  private final MetadataModelRepository metadataModelRepository;
  private final FormFieldRepository formFieldRepository;
  private final FormFieldRuleRepository formFieldRuleRepository;
  private final FormFieldValidationRepository formFieldValidationRepository;
  private final FormLayoutSectionRepository formLayoutSectionRepository;
  private final FormSectionFieldRepository formSectionFieldRepository;
  private final FormSubFormRepository formSubFormRepository;
  private final TableColumnRepository tableColumnRepository;

  public FormDefinitionAssemblyService(
      MetadataViewRepository metadataViewRepository,
      MetadataModelRepository metadataModelRepository,
      FormFieldRepository formFieldRepository,
      FormFieldRuleRepository formFieldRuleRepository,
      FormFieldValidationRepository formFieldValidationRepository,
      FormLayoutSectionRepository formLayoutSectionRepository,
      FormSectionFieldRepository formSectionFieldRepository,
      FormSubFormRepository formSubFormRepository,
      TableColumnRepository tableColumnRepository) {
    this.metadataViewRepository = metadataViewRepository;
    this.metadataModelRepository = metadataModelRepository;
    this.formFieldRepository = formFieldRepository;
    this.formFieldRuleRepository = formFieldRuleRepository;
    this.formFieldValidationRepository = formFieldValidationRepository;
    this.formLayoutSectionRepository = formLayoutSectionRepository;
    this.formSectionFieldRepository = formSectionFieldRepository;
    this.formSubFormRepository = formSubFormRepository;
    this.tableColumnRepository = tableColumnRepository;
  }

  /**
   * Assembles the full form definition bundle for a given form code.
   *
   * @param formCode the form code (name field in sys_metadata_views)
   * @return the assembled bundle, or null if form not found
   */
  public FormDefinitionBundleResponse assembleDefinition(String formCode) {
    // 1. Form header
    MetadataView view = metadataViewRepository.findByName(formCode).orElse(null);
    if (view == null || !Boolean.TRUE.equals(view.getIsActive())) {
      return null;
    }

    // 2. Model header
    MetadataModel model = metadataModelRepository.findByName(view.getModelName()).orElse(null);

    FormDefinitionBundleResponse bundle = new FormDefinitionBundleResponse();
    bundle.setFormId(view.getId());
    bundle.setFormCode(view.getName());
    bundle.setFormLabel(view.getName());
    bundle.setModelName(view.getModelName());
    bundle.setModelLabel(model != null ? model.getLabel() : null);
    bundle.setTableName(model != null ? model.getTableName() : null);
    bundle.setWhereClauseField(view.getWhereClauseField());
    bundle.setWhereClauseOperator(view.getWhereClauseOperator());
    bundle.setWhereClauseValue(view.getWhereClauseValue());

    // 3. Fields with type info from model columns
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

    // 4. Layout sections
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

    // 5. Sub-forms
    List<FormSubFormEntity> subForms = formSubFormRepository.findByParentFormIdOrderByPosition(view.getId());
    bundle.setSubForms(subForms.stream().map(sf -> {
      SubFormDefinitionResponse sd = new SubFormDefinitionResponse();
      sd.setId(sf.getId());
      sd.setRelationCode(sf.getRelationCode());
      sd.setChildFormCode(sf.getChildFormCode());
      sd.setLabel(sf.getLabel());
      sd.setDisplayAs(sf.getDisplayAs());
      sd.setPosition(sf.getPosition());
      return sd;
    }).toList());

    return bundle;
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
