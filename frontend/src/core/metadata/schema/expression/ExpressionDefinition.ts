/**
 * JSON Logic rule expression or simple string evaluation rules.
 * Supported uses: visibility, readonly, validation, workflow guards, permissions, calculated fields.
 * Example:
 * {
 *   ">": [
 *     { "var": "amount" },
 *     1000
 *   ]
 * }
 */
export type ExpressionDefinition = Record<string, unknown> | string;
