import type { ActionHandler, Registry } from '../registry.types';

export interface ActionRegistry extends Registry<ActionHandler> {
  registerAction(type: string, handler: ActionHandler, priority?: number): void;
  resolveAction(type: string): Promise<ActionHandler>;
}
