import { useMemo } from 'react';

import { evaluateExpression } from './expressionEngine';

export function useExpression(expression: unknown, context: Record<string, unknown>): boolean {
  return useMemo(
    () => evaluateExpression(expression, context),
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [expression, JSON.stringify(context)]
  );
}
