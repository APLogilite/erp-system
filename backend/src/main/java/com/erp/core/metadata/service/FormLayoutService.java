package com.erp.core.metadata.service;

import com.erp.core.metadata.dto.FormLayoutSectionCreateRequest;
import com.erp.core.metadata.dto.FormLayoutSectionDto;
import com.erp.core.metadata.dto.FormSectionFieldDto;
import com.erp.core.metadata.entity.FormLayoutSectionEntity;
import com.erp.core.metadata.entity.FormSectionFieldEntity;
import com.erp.core.metadata.repository.FormLayoutSectionRepository;
import com.erp.core.metadata.repository.FormSectionFieldRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing form layout sections and field-to-section assignments.
 */
@Service
public class FormLayoutService {

  private final FormLayoutSectionRepository layoutSectionRepository;
  private final FormSectionFieldRepository sectionFieldRepository;

  public FormLayoutService(
      FormLayoutSectionRepository layoutSectionRepository,
      FormSectionFieldRepository sectionFieldRepository) {
    this.layoutSectionRepository = layoutSectionRepository;
    this.sectionFieldRepository = sectionFieldRepository;
  }

  /**
   * Get all layout sections for a form, ordered by position.
   */
  public List<FormLayoutSectionDto> getSections(UUID formId) {
    List<FormLayoutSectionEntity> entities = layoutSectionRepository.findByFormIdOrderByPosition(formId);
    return entities.stream().map(this::toDto).toList();
  }

  /**
   * Add a new layout section to a form.
   */
  @Transactional
  public FormLayoutSectionDto addSection(UUID formId, FormLayoutSectionCreateRequest request) {
    FormLayoutSectionEntity entity = new FormLayoutSectionEntity();
    entity.setFormId(formId);
    entity.setCode(request.getCode());
    entity.setLabel(request.getLabel());
    entity.setCollapsible(request.getCollapsible() != null ? request.getCollapsible() : false);
    entity.setColumns(request.getColumns() != null ? request.getColumns() : 1);
    entity.setPosition(request.getPosition() != null ? request.getPosition() : 0);

    FormLayoutSectionEntity saved = layoutSectionRepository.save(entity);
    return toDto(saved);
  }

  /**
   * Update an existing layout section.
   */
  @Transactional
  public FormLayoutSectionDto updateSection(UUID formId, UUID sectionId, FormLayoutSectionCreateRequest request) {
    FormLayoutSectionEntity entity = layoutSectionRepository.findById(sectionId)
        .orElseThrow(() -> new IllegalArgumentException("Section not found: " + sectionId));

    if (!entity.getFormId().equals(formId)) {
      throw new IllegalArgumentException("Section does not belong to form: " + formId);
    }

    entity.setCode(request.getCode());
    entity.setLabel(request.getLabel());
    entity.setCollapsible(request.getCollapsible() != null ? request.getCollapsible() : entity.getCollapsible());
    entity.setColumns(request.getColumns() != null ? request.getColumns() : entity.getColumns());
    if (request.getPosition() != null) {
      entity.setPosition(request.getPosition());
    }

    FormLayoutSectionEntity saved = layoutSectionRepository.save(entity);
    return toDto(saved);
  }

  /**
   * Remove a layout section. The section-field assignments are cascade-deleted.
   */
  @Transactional
  public void deleteSection(UUID formId, UUID sectionId) {
    FormLayoutSectionEntity entity = layoutSectionRepository.findById(sectionId)
        .orElseThrow(() -> new IllegalArgumentException("Section not found: " + sectionId));

    if (!entity.getFormId().equals(formId)) {
      throw new IllegalArgumentException("Section does not belong to form: " + formId);
    }

    layoutSectionRepository.delete(entity);
  }

  /**
   * Assign fields to a section. Replaces all existing field assignments
   * for the section with the provided list.
   */
  @Transactional
  public List<FormSectionFieldDto> assignFieldsToSection(UUID formId, UUID sectionId, List<UUID> fieldIds) {
    // Verify section exists and belongs to the form
    FormLayoutSectionEntity section = layoutSectionRepository.findById(sectionId)
        .orElseThrow(() -> new IllegalArgumentException("Section not found: " + sectionId));

    if (!section.getFormId().equals(formId)) {
      throw new IllegalArgumentException("Section does not belong to form: " + formId);
    }

    // Remove existing assignments for this section
    List<FormSectionFieldEntity> existing = sectionFieldRepository.findBySectionId(sectionId);
    sectionFieldRepository.deleteAll(existing);

    // Create new assignments
    List<FormSectionFieldEntity> newAssignments = new ArrayList<>();
    for (int i = 0; i < fieldIds.size(); i++) {
      FormSectionFieldEntity sfe = new FormSectionFieldEntity();
      sfe.setSectionId(sectionId);
      sfe.setFieldId(fieldIds.get(i));
      sfe.setPosition(i);
      newAssignments.add(sectionFieldRepository.save(sfe));
    }

    return newAssignments.stream().map(this::toSectionFieldDto).toList();
  }

  /**
   * Deep-copy all layout sections and their field assignments from source to target form.
   * Returns a map of old field ID → new field ID for updating section references.
   */
  @Transactional
  public void cloneLayout(UUID sourceFormId, UUID targetFormId,
                          java.util.Map<UUID, UUID> oldToNewFieldIdMap) {
    List<FormLayoutSectionEntity> sourceSections = layoutSectionRepository.findByFormIdOrderByPosition(sourceFormId);

    for (FormLayoutSectionEntity sourceSection : sourceSections) {
      // Clone the section
      FormLayoutSectionEntity clonedSection = new FormLayoutSectionEntity();
      clonedSection.setFormId(targetFormId);
      clonedSection.setCode(sourceSection.getCode());
      clonedSection.setLabel(sourceSection.getLabel());
      clonedSection.setCollapsible(sourceSection.getCollapsible());
      clonedSection.setColumns(sourceSection.getColumns());
      clonedSection.setPosition(sourceSection.getPosition());
      FormLayoutSectionEntity savedSection = layoutSectionRepository.save(clonedSection);

      // Clone field assignments with new field IDs
      List<FormSectionFieldEntity> sourceAssignments = sectionFieldRepository.findBySectionId(sourceSection.getId());
      for (FormSectionFieldEntity sa : sourceAssignments) {
        UUID newFieldId = oldToNewFieldIdMap.get(sa.getFieldId());
        if (newFieldId != null) {
          FormSectionFieldEntity clonedAssignment = new FormSectionFieldEntity();
          clonedAssignment.setSectionId(savedSection.getId());
          clonedAssignment.setFieldId(newFieldId);
          clonedAssignment.setPosition(sa.getPosition());
          sectionFieldRepository.save(clonedAssignment);
        }
      }
    }
  }

  private FormLayoutSectionDto toDto(FormLayoutSectionEntity entity) {
    FormLayoutSectionDto dto = new FormLayoutSectionDto();
    dto.setId(entity.getId());
    dto.setFormId(entity.getFormId());
    dto.setCode(entity.getCode());
    dto.setLabel(entity.getLabel());
    dto.setCollapsible(entity.getCollapsible());
    dto.setColumns(entity.getColumns());
    dto.setPosition(entity.getPosition());
    return dto;
  }

  private FormSectionFieldDto toSectionFieldDto(FormSectionFieldEntity entity) {
    FormSectionFieldDto dto = new FormSectionFieldDto();
    dto.setId(entity.getId());
    dto.setSectionId(entity.getSectionId());
    dto.setFieldId(entity.getFieldId());
    dto.setPosition(entity.getPosition());
    return dto;
  }
}
