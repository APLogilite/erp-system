import type { ComponentType } from 'react';

import type { ActionRegistry } from './action/actionRegistry.types';
import type { FieldRegistry } from './field/fieldRegistry.types';
import type { LayoutRegistry } from './layout/layoutRegistry.types';
import type { ViewRegistry } from './view/viewRegistry.types';
import type { WorkflowRegistry } from './workflow/workflowRegistry.types';

export type RegistryPriority = number;

export type LazyRegistryValue<T> = {
  load: () => Promise<T>;
};

export type RegistryValue<T> = T | LazyRegistryValue<T>;

export interface RegistryEntry<T> {
  key: string;
  value: RegistryValue<T>;
  priority: RegistryPriority;
}

export interface Registry<T> {
  register(key: string, value: RegistryValue<T>, priority?: RegistryPriority): void;
  unregister(key: string): void;
  resolve(key: string): Promise<T>;
  has(key: string): boolean;
  getAll(): Record<string, RegistryEntry<T>>;
  clear(): void;
}

export function isLazyRegistryValue<T>(value: RegistryValue<T>): value is LazyRegistryValue<T> {
  return (
    typeof value === 'object' &&
    value !== null &&
    'load' in value &&
    typeof (value as LazyRegistryValue<T>).load === 'function'
  );
}

// These component types accept dynamic metadata-driven props.
// eslint-disable-next-line @typescript-eslint/no-explicit-any
export type FieldComponent = ComponentType<any>;
// eslint-disable-next-line @typescript-eslint/no-explicit-any
export type LayoutComponent = ComponentType<any>;
// eslint-disable-next-line @typescript-eslint/no-explicit-any
export type ViewComponent = ComponentType<any>;
// eslint-disable-next-line @typescript-eslint/no-explicit-any
export type WorkflowComponent = ComponentType<any>;

export type ActionExecutor = (payload?: unknown) => Promise<unknown>;
// eslint-disable-next-line @typescript-eslint/no-explicit-any
export type ActionComponent = ComponentType<any>;
export type ActionHandler =
  | ActionExecutor
  | ActionComponent
  | {
      run: ActionExecutor;
      label?: string;
      icon?: string;
    };

export interface RegistryPlugin {
  registerFields?: (registry: FieldRegistry) => void;
  registerLayouts?: (registry: LayoutRegistry) => void;
  registerActions?: (registry: ActionRegistry) => void;
  registerWorkflows?: (registry: WorkflowRegistry) => void;
  registerViews?: (registry: ViewRegistry) => void;
}
