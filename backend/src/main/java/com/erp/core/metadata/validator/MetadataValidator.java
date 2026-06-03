package com.erp.core.metadata.validator;

import com.erp.core.metadata.dto.FieldMetadataDto;
import com.erp.core.metadata.dto.ModelMetadataDto;
import com.erp.core.metadata.dto.WorkflowMetadataDto;
import com.erp.core.metadata.exception.MetadataValidationException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Validates metadata definitions for consistency and completeness.
 */
@Component
public class MetadataValidator {

  /**
   * Validate a model metadata definition.
   */
  public void validateModel(ModelMetadataDto model) {
    List<String> errors = new ArrayList<>();

    if (model == null) {
      errors.add("Model cannot be null");
    } else {
      if (model.getCode() == null || model.getCode().isBlank()) {
        errors.add("Model code cannot be empty");
      }

      if (model.getName() == null || model.getName().isBlank()) {
        errors.add("Model name cannot be empty");
      }

      if (model.getFields() != null && !model.getFields().isEmpty()) {
        validateFields(model.getFields(), errors);
      }
    }

    if (!errors.isEmpty()) {
      throw new MetadataValidationException("Model validation failed", errors);
    }
  }

  /**
   * Validate field definitions for duplicates and required attributes.
   */
  private void validateFields(List<FieldMetadataDto> fields, List<String> errors) {
    Set<String> fieldCodes = new HashSet<>();

    for (FieldMetadataDto field : fields) {
      if (field.getCode() == null || field.getCode().isBlank()) {
        errors.add("Field code cannot be empty");
      } else if (!fieldCodes.add(field.getCode())) {
        errors.add("Duplicate field code: " + field.getCode());
      }

      if (field.getName() == null || field.getName().isBlank()) {
        errors.add("Field name cannot be empty for code: " + field.getCode());
      }

      if (field.getType() == null || field.getType().isBlank()) {
        errors.add("Field type cannot be empty for code: " + field.getCode());
      }
    }
  }

  /**
   * Validate workflow metadata definition.
   */
  public void validateWorkflow(WorkflowMetadataDto workflow) {
    List<String> errors = new ArrayList<>();

    if (workflow == null) {
      errors.add("Workflow cannot be null");
    } else {
      if (workflow.getCode() == null || workflow.getCode().isBlank()) {
        errors.add("Workflow code cannot be empty");
      }

      if (workflow.getModelCode() == null || workflow.getModelCode().isBlank()) {
        errors.add("Workflow model code cannot be empty");
      }

      if (workflow.getStates() == null || workflow.getStates().isEmpty()) {
        errors.add("Workflow must have at least one state");
      } else {
        boolean hasInitialState = workflow.getStates().stream()
            .anyMatch(s -> s.isInitial());
        if (!hasInitialState) {
          errors.add("Workflow must have an initial state");
        }
      }

      if (workflow.getTransitions() != null) {
        validateTransitions(workflow, errors);
      }
    }

    if (!errors.isEmpty()) {
      throw new MetadataValidationException("Workflow validation failed", errors);
    }
  }

  /**
   * Validate workflow transitions reference valid states.
   */
  private void validateTransitions(WorkflowMetadataDto workflow, List<String> errors) {
    Set<String> validStates = workflow.getStates().stream()
        .map(s -> s.getCode())
        .collect(java.util.stream.Collectors.toSet());

    for (var transition : workflow.getTransitions()) {
      if (!validStates.contains(transition.getFromState())) {
        errors.add("Invalid fromState in transition: " + transition.getFromState());
      }
      if (!validStates.contains(transition.getToState())) {
        errors.add("Invalid toState in transition: " + transition.getToState());
      }
    }
  }
}
