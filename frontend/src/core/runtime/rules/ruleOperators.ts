/**
 * Rule operator functions. Each evaluates a condition against a field value.
 * All operators are pure functions — no side effects, no DOM access.
 */

export type OperatorFn = (value: unknown, conditionValue: string | null) => boolean;

export const OPERATORS: Record<string, OperatorFn> = {
  equals: (value, conditionValue) => {
    if (value === null || value === undefined) return conditionValue === null;
    return String(value) === String(conditionValue);
  },

  not_equals: (value, conditionValue) => {
    if (value === null || value === undefined) return conditionValue !== null;
    return String(value) !== String(conditionValue);
  },

  greater_than: (value, conditionValue) => {
    if (value === null || value === undefined || conditionValue === null) return false;
    return Number(value) > Number(conditionValue);
  },

  less_than: (value, conditionValue) => {
    if (value === null || value === undefined || conditionValue === null) return false;
    return Number(value) < Number(conditionValue);
  },

  greater_than_or_equal: (value, conditionValue) => {
    if (value === null || value === undefined || conditionValue === null) return false;
    return Number(value) >= Number(conditionValue);
  },

  less_than_or_equal: (value, conditionValue) => {
    if (value === null || value === undefined || conditionValue === null) return false;
    return Number(value) <= Number(conditionValue);
  },

  contains: (value, conditionValue) => {
    if (value === null || value === undefined) return false;
    if (conditionValue === null) return false;
    return String(value).toLowerCase().includes(String(conditionValue).toLowerCase());
  },

  is_empty: (value) => {
    return value === null || value === undefined || value === '';
  },

  is_not_empty: (value) => {
    return value !== null && value !== undefined && value !== '';
  },

  in: (value, conditionValue) => {
    if (value === null || value === undefined) return false;
    if (!conditionValue) return false;
    try {
      const list: unknown[] = JSON.parse(conditionValue);
      return Array.isArray(list) && list.some((item) => String(item) === String(value));
    } catch {
      return false;
    }
  },
};

/** Check if an operator requires a value (vs is_empty/is_not_empty) */
export const OPERATOR_REQUIRES_VALUE: Record<string, boolean> = {
  is_empty: false,
  is_not_empty: false,
};

export function getOperator(name: string): OperatorFn {
  const op = OPERATORS[name];
  if (!op) {
    throw new Error(`Unknown operator: ${name}. Supported: ${Object.keys(OPERATORS).join(', ')}`);
  }
  return op;
}

export function requiresValue(name: string): boolean {
  return !OPERATOR_REQUIRES_VALUE[name];
}
