package com.erp.core.runtime.service;

import com.erp.core.runtime.dto.FieldDefinitionResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * Validates record payloads against form field definitions.
 * Checks required fields, enforces read-only fields, and collects
 * validation errors with field-level detail.
 */
@Service
public class RecordValidationService {

  /**
   * Validates a create/update payload against form field definitions.
   * Returns a list of validation error messages. Empty list = valid.
   */
  public List<String> validate(
      Map<String, Object> data,
      List<FieldDefinitionResponse> fieldDefs,
      boolean isCreate) {

    List<String> errors = new ArrayList<>();
    Set<String> readOnlyFields = fieldDefs.stream()
        .filter(f -> Boolean.TRUE.equals(f.getReadOnly()))
        .map(FieldDefinitionResponse::getColumnCode)
        .collect(Collectors.toSet());

    for (FieldDefinitionResponse field : fieldDefs) {
      String code = field.getColumnCode();
      Object value = data.get(code);

      // Check read-only enforcement
      if (readOnlyFields.contains(code) && value != null && !isCreate) {
        errors.add("Field '" + field.getLabel() + "' is read-only and cannot be modified.");
        continue;
      }

      // Check required fields
      if (Boolean.TRUE.equals(field.getRequired())) {
        if (value == null || (value instanceof String && ((String) value).isBlank())) {
          errors.add("Field '" + field.getLabel() + "' is required.");
        }
      }
    }

    return errors;
  }

  /**
   * Strips read-only fields from the update payload.
   * Returns a new map without read-only entries.
   */
  public Map<String, Object> stripReadOnly(
      Map<String, Object> data,
      List<FieldDefinitionResponse> fieldDefs) {

    Set<String> readOnlyFields = fieldDefs.stream()
        .filter(f -> Boolean.TRUE.equals(f.getReadOnly()))
        .map(FieldDefinitionResponse::getColumnCode)
        .collect(Collectors.toSet());

    return data.entrySet().stream()
        .filter(e -> !readOnlyFields.contains(e.getKey()))
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }
}
