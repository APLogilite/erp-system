package com.erp.core.metadata.service;

import com.erp.core.metadata.dto.FormFieldCreateRequest;
import com.erp.core.metadata.dto.FormFieldDto;
import com.erp.core.metadata.entity.FormFieldEntity;
import com.erp.core.metadata.repository.FormFieldRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing form field configurations within a form definition.
 */
@Service
public class FormFieldService {

  private final FormFieldRepository formFieldRepository;

  public FormFieldService(FormFieldRepository formFieldRepository) {
    this.formFieldRepository = formFieldRepository;
  }

  /**
   * Get all fields for a form, ordered by position.
   */
  public List<FormFieldDto> getFields(UUID formId) {
    List<FormFieldEntity> entities = formFieldRepository.findByFormIdAndIsActiveTrueOrderByPosition(formId);
    return entities.stream().map(this::toDto).toList();
  }

  /**
   * Add a new field to a form.
   */
  @Transactional
  public FormFieldDto addField(UUID formId, FormFieldCreateRequest request) {
    FormFieldEntity entity = new FormFieldEntity();
    entity.setFormId(formId);
    entity.setColumnCode(request.getColumnCode());
    entity.setLabelOverride(request.getLabelOverride());
    entity.setVisible(request.getVisible() != null ? request.getVisible() : true);
    entity.setReadOnly(request.getReadOnly() != null ? request.getReadOnly() : false);
    entity.setRequired(request.getRequired() != null ? request.getRequired() : false);
    entity.setPosition(resolvePosition(request.getPosition()));
    entity.setDefaultValue(request.getDefaultValue());
    entity.setPlaceholder(request.getPlaceholder());

    FormFieldEntity saved = formFieldRepository.save(entity);
    return toDto(saved);
  }

  /**
   * Update an existing field's configuration.
   */
  @Transactional
  public FormFieldDto updateField(UUID formId, UUID fieldId, FormFieldCreateRequest request) {
    FormFieldEntity entity = formFieldRepository.findById(fieldId)
        .orElseThrow(() -> new IllegalArgumentException("Field not found: " + fieldId));

    if (!entity.getFormId().equals(formId)) {
      throw new IllegalArgumentException("Field does not belong to form: " + formId);
    }

    entity.setLabelOverride(request.getLabelOverride());
    entity.setVisible(request.getVisible() != null ? request.getVisible() : entity.getVisible());
    entity.setReadOnly(request.getReadOnly() != null ? request.getReadOnly() : entity.getReadOnly());
    entity.setRequired(request.getRequired() != null ? request.getRequired() : entity.getRequired());
    entity.setDefaultValue(request.getDefaultValue());
    entity.setPlaceholder(request.getPlaceholder());

    if (request.getPosition() != null) {
      entity.setPosition(request.getPosition());
    }

    FormFieldEntity saved = formFieldRepository.save(entity);
    return toDto(saved);
  }

  /**
   * Remove (soft-delete) a field from a form.
   */
  @Transactional
  public void deleteField(UUID formId, UUID fieldId) {
    FormFieldEntity entity = formFieldRepository.findById(fieldId)
        .orElseThrow(() -> new IllegalArgumentException("Field not found: " + fieldId));

    if (!entity.getFormId().equals(formId)) {
      throw new IllegalArgumentException("Field does not belong to form: " + formId);
    }

    entity.softDelete();
    formFieldRepository.save(entity);
  }

  /**
   * Reorder fields by providing the full ordered list of field IDs.
   * Each field's position is updated to match its index in the list.
   */
  @Transactional
  public List<FormFieldDto> reorderFields(UUID formId, List<UUID> fieldIds) {
    List<FormFieldEntity> updated = new ArrayList<>();
    for (int i = 0; i < fieldIds.size(); i++) {
      UUID fieldId = fieldIds.get(i);
      FormFieldEntity entity = formFieldRepository.findById(fieldId)
          .orElseThrow(() -> new IllegalArgumentException("Field not found: " + fieldId));

      if (!entity.getFormId().equals(formId)) {
        throw new IllegalArgumentException("Field does not belong to form: " + formId);
      }

      entity.setPosition(i);
      updated.add(formFieldRepository.save(entity));
    }

    return updated.stream()
        .sorted((a, b) -> Integer.compare(a.getPosition(), b.getPosition()))
        .map(this::toDto)
        .toList();
  }

  /**
   * Deep-copy all fields from a source form to a target form.
   * Used during form cloning.
   */
  @Transactional
  public List<FormFieldEntity> cloneFields(UUID sourceFormId, UUID targetFormId) {
    List<FormFieldEntity> sourceFields = formFieldRepository.findByFormIdAndIsActiveTrueOrderByPosition(sourceFormId);
    List<FormFieldEntity> clonedFields = new ArrayList<>();

    for (FormFieldEntity source : sourceFields) {
      FormFieldEntity clone = new FormFieldEntity();
      clone.setFormId(targetFormId);
      clone.setColumnCode(source.getColumnCode());
      clone.setLabelOverride(source.getLabelOverride());
      clone.setVisible(source.getVisible());
      clone.setReadOnly(source.getReadOnly());
      clone.setRequired(source.getRequired());
      clone.setPosition(source.getPosition());
      clone.setDefaultValue(source.getDefaultValue());
      clone.setPlaceholder(source.getPlaceholder());
      clonedFields.add(formFieldRepository.save(clone));
    }

    return clonedFields;
  }

  private int resolvePosition(Integer requestedPosition) {
    return requestedPosition != null ? requestedPosition : 0;
  }

  private FormFieldDto toDto(FormFieldEntity entity) {
    FormFieldDto dto = new FormFieldDto();
    dto.setId(entity.getId());
    dto.setFormId(entity.getFormId());
    dto.setColumnCode(entity.getColumnCode());
    dto.setLabelOverride(entity.getLabelOverride());
    dto.setVisible(entity.getVisible());
    dto.setReadOnly(entity.getReadOnly());
    dto.setRequired(entity.getRequired());
    dto.setPosition(entity.getPosition());
    dto.setDefaultValue(entity.getDefaultValue());
    dto.setPlaceholder(entity.getPlaceholder());
    dto.setIsActive(entity.getIsActive());
    return dto;
  }
}
