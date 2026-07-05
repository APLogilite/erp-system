import { BaseRegistry } from '../registry';
import { DEFAULT_REGISTRY_PRIORITY } from '../registry.constants';
import type { ActionHandler } from '../registry.types';

import type { ActionRegistry as ActionRegistryContract } from './actionRegistry.types';

export class ActionRegistry extends BaseRegistry<ActionHandler> implements ActionRegistryContract {
  registerAction(
    type: string,
    handler: ActionHandler,
    priority: number = DEFAULT_REGISTRY_PRIORITY
  ): void {
    this.register(type, handler, priority);
  }

  async resolveAction(type: string): Promise<ActionHandler> {
    return this.resolve(type);
  }
}
