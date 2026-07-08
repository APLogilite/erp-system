package com.erp.core.metadata.service;

import com.erp.core.metadata.dto.FormFieldRuleCreateRequest;
import com.erp.core.metadata.dto.FormFieldRuleDto;
import com.erp.core.metadata.entity.FormFieldRuleEntity;
import com.erp.core.metadata.repository.FormFieldRuleRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FormRuleService {

  private final FormFieldRuleRepository ruleRepository;

  public FormRuleService(FormFieldRuleRepository ruleRepository) {
    this.ruleRepository = ruleRepository;
  }

  public List<FormFieldRuleDto> getRules(UUID fieldId) {
    return ruleRepository.findByFieldId(fieldId).stream().map(this::toDto).toList();
  }

  @Transactional
  public FormFieldRuleDto addRule(UUID fieldId, FormFieldRuleCreateRequest req) {
    FormFieldRuleEntity entity = new FormFieldRuleEntity();
    entity.setFieldId(fieldId);
    entity.setConditionField(req.getConditionField());
    entity.setConditionOperator(req.getConditionOperator());
    entity.setConditionValue(req.getConditionValue());
    entity.setAction(req.getAction());
    entity.setLogicGroup(req.getLogicGroup() != null ? req.getLogicGroup() : 0);
    entity.setPosition(req.getPosition());
    return toDto(ruleRepository.save(entity));
  }

  @Transactional
  public FormFieldRuleDto updateRule(UUID fieldId, UUID ruleId, FormFieldRuleCreateRequest req) {
    FormFieldRuleEntity entity = ruleRepository.findById(ruleId)
        .orElseThrow(() -> new IllegalArgumentException("Rule not found: " + ruleId));
    entity.setConditionField(req.getConditionField());
    entity.setConditionOperator(req.getConditionOperator());
    entity.setConditionValue(req.getConditionValue());
    entity.setAction(req.getAction());
    entity.setLogicGroup(req.getLogicGroup() != null ? req.getLogicGroup() : entity.getLogicGroup());
    if (req.getPosition() != null) entity.setPosition(req.getPosition());
    return toDto(ruleRepository.save(entity));
  }

  @Transactional
  public void deleteRule(UUID fieldId, UUID ruleId) {
    ruleRepository.deleteById(ruleId);
  }

  private FormFieldRuleDto toDto(FormFieldRuleEntity e) {
    FormFieldRuleDto d = new FormFieldRuleDto();
    d.setId(e.getId()); d.setFieldId(e.getFieldId());
    d.setConditionField(e.getConditionField()); d.setConditionOperator(e.getConditionOperator());
    d.setConditionValue(e.getConditionValue()); d.setAction(e.getAction());
    d.setLogicGroup(e.getLogicGroup()); d.setPosition(e.getPosition());
    return d;
  }
}
