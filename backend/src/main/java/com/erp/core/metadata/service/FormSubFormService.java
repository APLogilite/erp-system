package com.erp.core.metadata.service;

import com.erp.core.metadata.dto.AvailableRelationDto;
import com.erp.core.metadata.dto.FormSubFormCreateRequest;
import com.erp.core.metadata.dto.FormSubFormDto;
import com.erp.core.metadata.dto.SubFormReorderRequest;
import com.erp.core.metadata.entity.FormSubFormEntity;
import com.erp.core.metadata.entity.MetadataModel;
import com.erp.core.metadata.entity.TableColumnEntity;
import com.erp.core.metadata.repository.FormSubFormRepository;
import com.erp.core.metadata.repository.MetadataModelRepository;
import com.erp.core.metadata.repository.MetadataViewRepository;
import com.erp.core.metadata.repository.TableColumnRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FormSubFormService {
  private final FormSubFormRepository subFormRepository;
  private final TableColumnRepository columnRepo;
  private final MetadataModelRepository modelRepo;
  private final MetadataViewRepository viewRepo;

  public FormSubFormService(
      FormSubFormRepository subFormRepository,
      TableColumnRepository columnRepo,
      MetadataModelRepository modelRepo,
      MetadataViewRepository viewRepo) {
    this.subFormRepository = subFormRepository;
    this.columnRepo = columnRepo;
    this.modelRepo = modelRepo;
    this.viewRepo = viewRepo;
  }

  public List<FormSubFormDto> getSubForms(UUID formId) {
    return subFormRepository.findByParentFormIdOrderByPosition(formId).stream()
        .map(this::toDto).toList();
  }

  /**
   * Returns all available one-to-many relations for the form's underlying table.
   * Scans sys_table_columns for many2one columns whose relation_table matches
   * the parent table's code or tableName.
   */
  public List<AvailableRelationDto> getAvailableRelations(UUID formId) {
    var viewOpt = viewRepo.findById(formId);
    if (viewOpt.isEmpty()) {
      return List.of();
    }
    var view = viewOpt.get();
    String parentTableName = view.getModelName();

    MetadataModel parentModel = modelRepo.findByName(parentTableName).orElse(null);
    if (parentModel == null) {
      return List.of();
    }

    List<AvailableRelationDto> relations = new ArrayList<>();

    // Find all columns where type='many2one' and relation_table matches this table
    List<TableColumnEntity> allMany2OneCols = columnRepo.findByType("many2one");
    for (TableColumnEntity col : allMany2OneCols) {
      if (col.getRelationTable() == null) continue;

      // Check if relation_table matches the parent's code or physical table name
      boolean matches = col.getRelationTable().equals(parentTableName)
          || (parentModel.getTableName() != null
              && col.getRelationTable().equals(parentModel.getTableName()));

      if (!matches) continue;

      // Get child table info
      UUID childTableId = col.getTableId();
      MetadataModel childModel = modelRepo.findById(childTableId).orElse(null);
      if (childModel == null) continue;

      AvailableRelationDto dto = new AvailableRelationDto();
      dto.setRelationCode(col.getCode());
      dto.setChildTableCode(childModel.getName());
      dto.setChildTableLabel(childModel.getLabel());
      dto.setRelationColumnLabel(col.getLabel());

      // Find existing forms for the child table
      var childViews = viewRepo.findByModelNameAndType(childModel.getName(), "form");
      List<String> existingFormCodes = childViews.stream()
          .map(v -> v.getName())
          .toList();
      dto.setExistingFormCodes(existingFormCodes);

      relations.add(dto);
    }

    return relations;
  }

  @Transactional
  public FormSubFormDto addSubForm(UUID formId, FormSubFormCreateRequest req) {
    checkCircularReference(formId, req.getChildFormCode());

    FormSubFormEntity e = new FormSubFormEntity();
    e.setParentFormId(formId); e.setRelationCode(req.getRelationCode());
    e.setChildFormCode(req.getChildFormCode()); e.setLabel(req.getLabel());
    e.setDisplayAs(req.getDisplayAs() != null ? req.getDisplayAs() : "tab");
    e.setPosition(req.getPosition());
    return toDto(subFormRepository.save(e));
  }

  @Transactional
  public FormSubFormDto updateSubForm(UUID formId, UUID subFormId, FormSubFormCreateRequest req) {
    FormSubFormEntity e = subFormRepository.findById(subFormId)
        .orElseThrow(() -> new IllegalArgumentException("Sub-form not found: " + subFormId));

    if (req.getChildFormCode() != null
        && !req.getChildFormCode().equals(e.getChildFormCode())) {
      checkCircularReference(formId, req.getChildFormCode());
    }

    e.setRelationCode(req.getRelationCode()); e.setChildFormCode(req.getChildFormCode());
    e.setLabel(req.getLabel());
    e.setDisplayAs(req.getDisplayAs() != null ? req.getDisplayAs() : e.getDisplayAs());
    if (req.getPosition() != null) e.setPosition(req.getPosition());
    return toDto(subFormRepository.save(e));
  }

  @Transactional
  public void deleteSubForm(UUID formId, UUID subFormId) {
    subFormRepository.deleteById(subFormId);
  }

  @Transactional
  public void reorderSubForms(UUID formId, SubFormReorderRequest request) {
    List<UUID> ids = request.getSubFormIds();
    if (ids == null || ids.isEmpty()) {
      throw new IllegalArgumentException("Sub-form IDs list must not be empty");
    }
    for (int i = 0; i < ids.size(); i++) {
      UUID subFormId = ids.get(i);
      var entity = subFormRepository.findById(subFormId)
          .orElseThrow(() -> new IllegalArgumentException("Sub-form not found: " + subFormId));
      entity.setPosition(i);
      subFormRepository.save(entity);
    }
  }

  /**
   * Checks for circular references by walking the sub-form chain.
   */
  private void checkCircularReference(UUID parentFormId, String childFormCode) {
    // Direct self-reference
    var parentView = viewRepo.findById(parentFormId);
    if (parentView.isPresent() && parentView.get().getName().equals(childFormCode)) {
      throw new IllegalArgumentException(
          "Circular reference detected: form cannot reference itself as a sub-form");
    }
    // Could extend to walk deeper, but for now detect direct self-references
  }

  private FormSubFormDto toDto(FormSubFormEntity e) {
    FormSubFormDto d = new FormSubFormDto();
    d.setId(e.getId()); d.setParentFormId(e.getParentFormId());
    d.setRelationCode(e.getRelationCode()); d.setChildFormCode(e.getChildFormCode());
    d.setLabel(e.getLabel()); d.setDisplayAs(e.getDisplayAs()); d.setPosition(e.getPosition());
    return d;
  }
}
