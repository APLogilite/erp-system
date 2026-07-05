package com.erp.core.workflow.service;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class WorkflowGuardService {

  /**
   * Evaluate guard conditions for a transition.
   * Supports simple key/value equality checks against the provided context.
   */
  public boolean evaluateGuards(Map<String, Object> guards, Map<String, Object> context) {
    if (guards == null || guards.isEmpty()) {
      return true;
    }

    if (context == null || context.isEmpty()) {
      return false;
    }

    for (Map.Entry<String, Object> guardEntry : guards.entrySet()) {
      String contextKey = guardEntry.getKey();
      Object expectedValue = guardEntry.getValue();
      Object actualValue = context.get(contextKey);

      if (expectedValue == null) {
        if (actualValue != null) {
          return false;
        }
      } else if (!expectedValue.equals(actualValue)) {
        return false;
      }
    }

    return true;
  }
}
