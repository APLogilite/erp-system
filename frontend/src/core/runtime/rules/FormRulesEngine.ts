/**
 * Client-side form rules engine.
 *
 * Evaluates field conditions (visibility, read-only, required) in real-time
 * based on field values and configured rules.
 *
 * Pure functions — no side effects, fully testable without React or DOM.
 */

import type { FieldDefinition, FieldRule } from '../hooks/useForm.types';
import { getOperator } from './ruleOperators';

// ---- Types ----

export interface FieldState {
  visible: boolean;
  readOnly: boolean;
  required: boolean;
}

/** Map of columnCode → FieldState */
export type FieldStateMap = Record<string, FieldState>;

// ---- Engine ----

/**
 * Evaluate the field state for a single field given all field values and rules.
 */
export function evaluateFieldState(
  field: FieldDefinition,
  values: Record<string, unknown>,
): FieldState {
  const state: FieldState = {
    visible: field.visible,
    readOnly: field.readOnly,
    required: field.required,
  };

  if (!field.rules || field.rules.length === 0) {
    return state;
  }

  // Group rules by logicGroup for AND/OR handling
  const groups = groupRulesByLogicGroup(field.rules);

  for (const group of groups.values()) {
    const results = group.map((rule) => evaluateRule(rule, values));

    // Within a logic group, AND all conditions; OR across groups
    const groupResult = results.every(Boolean);
    if (groupResult) {
      for (const rule of group) {
        applyAction(state, rule.action);
      }
    }
  }

  return state;
}

/**
 * Evaluate all fields and return a FieldStateMap.
 */
export function evaluateAllFields(
  fields: FieldDefinition[],
  values: Record<string, unknown>,
): FieldStateMap {
  const map: FieldStateMap = {};
  for (const field of fields) {
    map[field.columnCode] = evaluateFieldState(field, values);
  }
  return map;
}

/**
 * Compute which field columns have changed between previous and current values.
 * Returns the set of column codes whose values changed.
 */
export function getChangedFields(
  prevValues: Record<string, unknown>,
  nextValues: Record<string, unknown>,
): Set<string> {
  const changed = new Set<string>();
  const allKeys = new Set([...Object.keys(prevValues), ...Object.keys(nextValues)]);
  for (const key of allKeys) {
    if (String(prevValues[key]) !== String(nextValues[key])) {
      changed.add(key);
    }
  }
  return changed;
}

/**
 * Find all fields whose rules reference one of the changed field columns.
 * This optimization avoids re-evaluating fields whose dependencies haven't changed.
 */
export function getAffectedFields(
  changedCodes: Set<string>,
  allFields: FieldDefinition[],
): FieldDefinition[] {
  return allFields.filter((field) => {
    if (!field.rules || field.rules.length === 0) return false;
    return field.rules.some((rule) => changedCodes.has(rule.conditionField));
  });
}

// ---- Private Helpers ----

function groupRulesByLogicGroup(rules: FieldRule[]): Map<number, FieldRule[]> {
  const groups = new Map<number, FieldRule[]>();
  for (const rule of rules) {
    const group = rule.logicGroup ?? 0;
    if (!groups.has(group)) groups.set(group, []);
    groups.get(group)!.push(rule);
  }
  return groups;
}

function evaluateRule(
  rule: FieldRule,
  values: Record<string, unknown>,
): boolean {
  const fieldValue = values[rule.conditionField];
  const operator = getOperator(rule.conditionOperator);
  return operator(fieldValue, rule.conditionValue);
}

function applyAction(state: FieldState, action: string): void {
  switch (action) {
    case 'show':
      state.visible = true;
      break;
    case 'hide':
      state.visible = false;
      break;
    case 'read_only':
      state.readOnly = true;
      break;
    case 'editable':
      state.readOnly = false;
      break;
    case 'required':
      state.required = true;
      break;
    case 'optional':
      state.required = false;
      break;
    default:
      break;
  }
}
