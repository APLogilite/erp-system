package com.erp.core.metadata.service;

import com.erp.core.metadata.dto.ExpressionResultResponse;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Validates rule expressions and custom validation expressions for syntax
 * correctness. Supports both validation (syntax-only check) and evaluation
 * (test with sample data).
 */
@Service
public class ExpressionValidationService {

  private static final Logger log = LoggerFactory.getLogger(ExpressionValidationService.class);

  private static final Set<String> VALID_OPERATORS = Set.of(
      "equals", "not_equals", "greater_than", "less_than",
      "greater_than_or_equal", "less_than_or_equal",
      "contains", "is_empty", "is_not_empty", "in"
  );

  private static final Set<String> VALID_ACTIONS = Set.of(
      "show", "hide", "read_only", "editable", "required", "optional"
  );

  /**
   * Validates a rule expression for syntax correctness.
   * A rule expression consists of conditionField, conditionOperator, conditionValue
   * separated by spaces.
   */
  public ExpressionResultResponse validateExpression(String expression) {
    ExpressionResultResponse response = new ExpressionResultResponse();

    if (expression == null || expression.isBlank()) {
      response.setValid(false);
      response.setMessage("Expression must not be empty");
      return response;
    }

    String[] parts = expression.trim().split("\\s+", 3);

    if (parts.length < 2) {
      response.setValid(false);
      response.setMessage("Expression must have at least: field operator [value]. "
          + "Example: 'amount greater_than 100'");
      return response;
    }

    String field = parts[0];
    String operator = parts[1];

    // Validate field name
    if (!isValidFieldName(field)) {
      response.setValid(false);
      response.setMessage("Invalid field name: " + field
          + ". Must be snake_case: lowercase letters, numbers, underscores");
      return response;
    }

    // Validate operator
    if (!VALID_OPERATORS.contains(operator)) {
      response.setValid(false);
      response.setMessage("Invalid operator: " + operator
          + ". Supported operators: " + String.join(", ", VALID_OPERATORS));
      return response;
    }

    // Check value for operators that require it
    boolean requiresValue = !operator.equals("is_empty") && !operator.equals("is_not_empty");
    String value = parts.length >= 3 ? parts[2] : null;

    if (requiresValue && (value == null || value.isBlank())) {
      response.setValid(false);
      response.setMessage("Operator '" + operator + "' requires a value");
      return response;
    }

    // Validate value format for 'in' operator
    if (operator.equals("in") && value != null) {
      if (!value.matches("^\\[.*\\]$")) {
        response.setValid(false);
        response.setMessage("'in' operator requires a JSON array value, e.g. [1, 2, 3]");
        return response;
      }
    }

    response.setValid(true);
    response.setPassed(true);
    response.setMessage("Expression is valid");
    response.setResult(Map.of(
        "field", field,
        "operator", operator,
        "value", operator.equals("is_empty") || operator.equals("is_not_empty") ? null : (value != null ? value : "")
    ));
    return response;
  }

  /**
   * Validates an action keyword.
   */
  public ExpressionResultResponse validateAction(String action) {
    ExpressionResultResponse response = new ExpressionResultResponse();

    if (action == null || action.isBlank()) {
      response.setValid(false);
      response.setMessage("Action must not be empty");
      return response;
    }

    if (!VALID_ACTIONS.contains(action.trim().toLowerCase())) {
      response.setValid(false);
      response.setMessage("Invalid action: " + action
          + ". Supported actions: " + String.join(", ", VALID_ACTIONS));
      return response;
    }

    response.setValid(true);
    response.setPassed(true);
    response.setMessage("Action is valid: " + action);
    return response;
  }

  /**
   * Validates a regex pattern string.
   */
  public ExpressionResultResponse validatePattern(String pattern) {
    ExpressionResultResponse response = new ExpressionResultResponse();

    if (pattern == null || pattern.isBlank()) {
      response.setValid(false);
      response.setMessage("Pattern must not be empty");
      return response;
    }

    try {
      Pattern.compile(pattern);
      response.setValid(true);
      response.setPassed(true);
      response.setMessage("Pattern is a valid regular expression");
    } catch (PatternSyntaxException e) {
      response.setValid(false);
      response.setMessage("Invalid regex pattern: " + e.getMessage());
    }

    return response;
  }

  /**
   * Test-evaluates an expression against sample data.
   * Parses the expression and compares against the provided data map.
   */
  public ExpressionResultResponse evaluateExpression(String expression,
                                                      Map<String, Object> sampleData) {
    ExpressionResultResponse response = new ExpressionResultResponse();

    // First validate syntax
    ExpressionResultResponse syntaxCheck = validateExpression(expression);
    if (!syntaxCheck.isValid()) {
      return syntaxCheck;
    }

    if (sampleData == null || sampleData.isEmpty()) {
      response.setValid(true);
      response.setPassed(false);
      response.setMessage("No sample data provided for evaluation");
      return response;
    }

    String[] parts = expression.trim().split("\\s+", 3);
    String field = parts[0];
    String operator = parts[1];
    String expectedValue = parts.length >= 3 ? parts[2] : null;

    // Check if field exists in sample data
    if (!sampleData.containsKey(field)) {
      response.setValid(true);
      response.setPassed(false);
      response.setMessage("Field '" + field + "' not found in sample data. "
          + "Available fields: " + sampleData.keySet());
      return response;
    }

    Object actualValue = sampleData.get(field);
    boolean result = evaluateOperator(operator, actualValue, expectedValue);

    response.setValid(true);
    response.setPassed(result);
    response.setResult(Map.of(
        "field", field,
        "operator", operator,
        "expectedValue", expectedValue != null ? expectedValue : "null",
        "actualValue", String.valueOf(actualValue),
        "result", result
    ));
    response.setMessage(result
        ? "Expression evaluates to TRUE"
        : "Expression evaluates to FALSE");

    return response;
  }

  // ---------------------------------------------------------------
  // Private helpers
  // ---------------------------------------------------------------

  private boolean isValidFieldName(String field) {
    return field != null && Pattern.matches("^[a-z][a-z0-9_]*$", field);
  }

  @SuppressWarnings("unchecked")
  private boolean evaluateOperator(String operator, Object actual, String expected) {
    if (actual == null) {
      return operator.equals("is_empty");
    }

    String actualStr = String.valueOf(actual);

    switch (operator) {
      case "equals":
        return actualStr.equals(expected);
      case "not_equals":
        return !actualStr.equals(expected);
      case "greater_than":
        return compareNumeric(actual, expected) > 0;
      case "less_than":
        return compareNumeric(actual, expected) < 0;
      case "greater_than_or_equal":
        return compareNumeric(actual, expected) >= 0;
      case "less_than_or_equal":
        return compareNumeric(actual, expected) <= 0;
      case "contains":
        return actualStr.toLowerCase().contains(
            expected != null ? expected.toLowerCase() : "");
      case "is_empty":
        return actualStr == null || actualStr.isBlank();
      case "is_not_empty":
        return actualStr != null && !actualStr.isBlank();
      case "in":
        if (expected != null && expected.startsWith("[") && expected.endsWith("]")) {
          String inner = expected.substring(1, expected.length() - 1);
          String[] items = inner.split(",");
          for (String item : items) {
            if (actualStr.equals(item.trim().replace("\"", ""))) {
              return true;
            }
          }
        }
        return false;
      default:
        return false;
    }
  }

  private int compareNumeric(Object actual, String expected) {
    try {
      double a = Double.parseDouble(String.valueOf(actual));
      double e = Double.parseDouble(expected);
      return Double.compare(a, e);
    } catch (NumberFormatException ex) {
      return String.valueOf(actual).compareTo(expected);
    }
  }
}
