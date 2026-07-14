/**
 * React hook that wraps the FormRulesEngine for use in components.
 * Returns a FieldStateMap recomputed whenever values change.
 * Uses useMemo for memoization and getChangedFields for optimization.
 */

import { useMemo } from 'react';

import type { FieldDefinition } from '../hooks/useForm.types';
import { evaluateAllFields, evaluateFieldState } from './FormRulesEngine';
import type { FieldState, FieldStateMap } from './FormRulesEngine';

export type { FieldState, FieldStateMap };

/**
 * Hook: returns a map of columnCode → FieldState for all fields,
 * recomputed when values change. Uses memoization to avoid
 * unnecessary re-computation.
 */
export function useFieldStates(
  fields: FieldDefinition[] | undefined,
  values: Record<string, unknown>
): FieldStateMap {
  // Build a stable dependency key from values
  const valuesKey = useMemo(
    () =>
      Object.entries(values)
        .map(([k, v]) => `${k}:${String(v)}`)
        .join('|'),
    [values]
  );

  return useMemo(() => {
    if (!fields || fields.length === 0) {
      return {};
    }
    return evaluateAllFields(fields, values);
  }, [fields, valuesKey]);
}

/**
 * Hook: returns a single field's state, optimized to only re-compute
 * when values it depends on change.
 */
export function useFieldState(
  field: FieldDefinition | undefined,
  values: Record<string, unknown>
): FieldState {
  const valuesKey = useMemo(
    () =>
      Object.entries(values)
        .map(([k, v]) => `${k}:${String(v)}`)
        .join('|'),
    [values]
  );

  return useMemo(() => {
    if (!field) {
      return { visible: true, readOnly: false, required: false };
    }
    return evaluateFieldState(field, values);
  }, [field, valuesKey]);
}

/**
 * Returns the set of hidden field column codes based on current state map.
 * Useful for filtering values before submission.
 */
export function getHiddenFields(stateMap: FieldStateMap): Set<string> {
  const hidden = new Set<string>();
  for (const [key, state] of Object.entries(stateMap)) {
    if (!state.visible) {
      hidden.add(key);
    }
  }
  return hidden;
}

/**
 * Removes hidden field values from a record before submission.
 */
export function filterHiddenValues(
  values: Record<string, unknown>,
  stateMap: FieldStateMap
): Record<string, unknown> {
  const hidden = getHiddenFields(stateMap);
  const result: Record<string, unknown> = {};
  for (const [key, val] of Object.entries(values)) {
    if (!hidden.has(key)) {
      result[key] = val;
    }
  }
  return result;
}
