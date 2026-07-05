import { BaseRegistry } from '../registry';
import { DEFAULT_REGISTRY_PRIORITY } from '../registry.constants';
import type { WorkflowComponent } from '../registry.types';

import type { WorkflowRegistry as WorkflowRegistryContract } from './workflowRegistry.types';

export class WorkflowRegistry
  extends BaseRegistry<WorkflowComponent>
  implements WorkflowRegistryContract
{
  registerWorkflow(
    type: string,
    component: WorkflowComponent,
    priority: number = DEFAULT_REGISTRY_PRIORITY
  ): void {
    this.register(type, component, priority);
  }

  registerLazyWorkflow(
    type: string,
    loader: () => Promise<WorkflowComponent>,
    priority: number = DEFAULT_REGISTRY_PRIORITY
  ): void {
    this.register(type, { load: loader }, priority);
  }

  async resolveWorkflow(type: string): Promise<WorkflowComponent> {
    return this.resolve(type);
  }
}
