package com.erp.core.metadata.service;

import com.erp.core.metadata.dto.FormFieldValidationCreateRequest;
import com.erp.core.metadata.dto.FormFieldValidationDto;
import com.erp.core.metadata.entity.FormFieldValidationEntity;
import com.erp.core.metadata.repository.FormFieldValidationRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FormValidationService {

  private final FormFieldValidationRepository validationRepository;

  public FormValidationService(FormFieldValidationRepository validationRepository) {
    this.validationRepository = validationRepository;
  }

  public List<FormFieldValidationDto> getValidations(UUID fieldId) {
    return validationRepository.findByFieldId(fieldId).stream().map(this::toDto).toList();
  }

  @Transactional
  public FormFieldValidationDto addValidation(UUID fieldId, FormFieldValidationCreateRequest req) {
    FormFieldValidationEntity entity = new FormFieldValidationEntity();
    entity.setFieldId(fieldId); entity.setType(req.getType());
    entity.setValue(req.getValue()); entity.setMessage(req.getMessage());
    entity.setPosition(req.getPosition());
    return toDto(validationRepository.save(entity));
  }

  @Transactional
  public FormFieldValidationDto updateValidation(UUID fieldId, UUID valId, FormFieldValidationCreateRequest req) {
    FormFieldValidationEntity entity = validationRepository.findById(valId)
        .orElseThrow(() -> new IllegalArgumentException("Validation not found: " + valId));
    entity.setType(req.getType()); entity.setValue(req.getValue());
    entity.setMessage(req.getMessage());
    if (req.getPosition() != null) entity.setPosition(req.getPosition());
    return toDto(validationRepository.save(entity));
  }

  @Transactional
  public void deleteValidation(UUID fieldId, UUID valId) {
    validationRepository.deleteById(valId);
  }

  private FormFieldValidationDto toDto(FormFieldValidationEntity e) {
    FormFieldValidationDto d = new FormFieldValidationDto();
    d.setId(e.getId()); d.setFieldId(e.getFieldId());
    d.setType(e.getType()); d.setValue(e.getValue());
    d.setMessage(e.getMessage()); d.setPosition(e.getPosition());
    return d;
  }
}
