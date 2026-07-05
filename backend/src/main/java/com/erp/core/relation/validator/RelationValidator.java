package com.erp.core.relation.validator;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RelationValidator {

  public void validate(String model, Map<String, Object> payload) {
    if (payload == null || payload.isEmpty()) {
      throw new IllegalArgumentException("Relation payload cannot be empty.");
    }

    // TODO: Validate relation payload against relation metadata configuration.
  }
}
