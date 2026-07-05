/**
 * Generic Registry contract for looking up and resolving components.
 * Supports override priority for custom plugin integrations.
 */
export interface Registry<T> {
  /** Register a component/value under a unique key with optional priority */
  register(key: string, value: T, priority?: number): void;
  /** Unregister a key */
  unregister(key: string): void;
  /** Resolve the registered component/value for a key */
  resolve(key: string): T;
  /** Check if a key has a registered component/value */
  has(key: string): boolean;
  /** Retrieve all registered components/values mapped by key */
  getAll(): Record<string, T>;
  /** Clear all registered components/values */
  clear(): void;
}

/** FieldRegistry resolves metadata field types to React component types */
export interface FieldRegistry extends Registry<unknown> {}

/** LayoutRegistry resolves layout metadata types to React component types */
export interface LayoutRegistry extends Registry<unknown> {}

/** ActionRegistry resolves action codes/types to execution actions */
export interface ActionRegistry extends Registry<unknown> {}

/** WorkflowRegistry resolves workflow state/transition visual representations */
export interface WorkflowRegistry extends Registry<unknown> {}
