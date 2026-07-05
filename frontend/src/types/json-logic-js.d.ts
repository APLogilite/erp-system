declare module 'json-logic-js' {
  type JsonLogicValue =
    | string
    | number
    | boolean
    | null
    | JsonLogicValue[]
    | { [key: string]: JsonLogicValue };
  interface JsonLogic {
    apply(rule: unknown, data?: unknown): JsonLogicValue;
  }
  const jsonLogic: JsonLogic;
  export default jsonLogic;
}
