import jsonLogic from 'json-logic-js';

export function evaluateExpression(expression: unknown, context: Record<string, unknown>): boolean {
  if (expression == null) {
    return true;
  }

  if (typeof expression === 'boolean') {
    return expression;
  }

  try {
    return Boolean(jsonLogic.apply(expression, context));
  } catch {
    return false;
  }
}
