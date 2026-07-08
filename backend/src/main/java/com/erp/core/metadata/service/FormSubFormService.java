package com.erp.core.metadata.service;

import com.erp.core.metadata.dto.FormSubFormCreateRequest;
import com.erp.core.metadata.dto.FormSubFormDto;
import com.erp.core.metadata.entity.FormSubFormEntity;
import com.erp.core.metadata.repository.FormSubFormRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FormSubFormService {
  private final FormSubFormRepository subFormRepository;
  public FormSubFormService(FormSubFormRepository subFormRepository) { this.subFormRepository = subFormRepository; }

  public List<FormSubFormDto> getSubForms(UUID formId) {
    return subFormRepository.findByParentFormIdOrderByPosition(formId).stream().map(this::toDto).toList();
  }

  @Transactional
  public FormSubFormDto addSubForm(UUID formId, FormSubFormCreateRequest req) {
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
    e.setRelationCode(req.getRelationCode()); e.setChildFormCode(req.getChildFormCode());
    e.setLabel(req.getLabel());
    e.setDisplayAs(req.getDisplayAs() != null ? req.getDisplayAs() : e.getDisplayAs());
    if (req.getPosition() != null) e.setPosition(req.getPosition());
    return toDto(subFormRepository.save(e));
  }

  @Transactional
  public void deleteSubForm(UUID formId, UUID subFormId) { subFormRepository.deleteById(subFormId); }

  private FormSubFormDto toDto(FormSubFormEntity e) {
    FormSubFormDto d = new FormSubFormDto();
    d.setId(e.getId()); d.setParentFormId(e.getParentFormId());
    d.setRelationCode(e.getRelationCode()); d.setChildFormCode(e.getChildFormCode());
    d.setLabel(e.getLabel()); d.setDisplayAs(e.getDisplayAs()); d.setPosition(e.getPosition());
    return d;
  }
}
