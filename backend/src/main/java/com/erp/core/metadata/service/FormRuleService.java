package com.erp.core.metadata.service;

import com.erp.core.metadata.dto.FormFieldRuleCreateRequest;
import com.erp.core.metadata.dto.FormFieldRuleDto;
import com.erp.core.metadata.entity.FormFieldRuleEntity;
import com.erp.core.metadata.repository.FormFieldRuleRepository;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FormRuleService {

  private static final Set<String> VALID_OPERATORS = Set.of(
      "equals", "not_equals", "greater_than", "less_than",
      "greater_than_or_equal", "less_than_or_equal",
      "contains", "is_empty", "is_not_empty", "in"
  );

  private static final Set<String> VALID_ACTIONS = Set.of(
      "show", "hide", "read_only", "editable", "required", "optional"
  );

  private static final Set<String> NO_VALUE_OPERATORS = Set.of("is_empty", "is_not_empty");

  private final FormFieldRuleRepository ruleRepository;

  public FormRuleService(FormFieldRuleRepository ruleRepository) {
    this.ruleRepository = ruleRepository;
  }

  public List<FormFieldRuleDto> getRules(UUID fieldId) {
    return ruleRepository.findByFieldId(fieldId).stream().map(this::toDto).toList();
  }

  @Transactional
  public FormFieldRuleDto addRule(UUID fieldId, FormFieldRuleCreateRequest req) {
    validateRule(req);
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
    validateRule(req);
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

  private void validateRule(FormFieldRuleCreateRequest req) {
    if (req.getConditionField() == null || req.getConditionField().isBlank()) {
      throw new IllegalArgumentException("conditionField is required");
    }
    if (req.getConditionOperator() == null || req.getConditionOperator().isBlank()) {
      throw new IllegalArgumentException("conditionOperator is required");
    }
    if (!VALID_OPERATORS.contains(req.getConditionOperator())) {
      throw new IllegalArgumentException("Invalid operator: " + req.getConditionOperator()
          + ". Supported: " + String.join(", ", VALID_OPERATORS));
    }
    if (!NO_VALUE_OPERATORS.contains(req.getConditionOperator())
        && (req.getConditionValue() == null || req.getConditionValue().isBlank())) {
      throw new IllegalArgumentException("conditionValue is required for operator: "
          + req.getConditionOperator());
    }
    if (req.getAction() == null || req.getAction().isBlank()) {
      throw new IllegalArgumentException("action is required");
    }
    if (!VALID_ACTIONS.contains(req.getAction())) {
      throw new IllegalArgumentException("Invalid action: " + req.getAction()
          + ". Supported: " + String.join(", ", VALID_ACTIONS));
    }
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
