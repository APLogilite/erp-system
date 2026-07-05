import { BaseRegistry } from '../registry';
import { DEFAULT_REGISTRY_PRIORITY } from '../registry.constants';
import type { LayoutComponent } from '../registry.types';

import type { LayoutRegistry as LayoutRegistryContract } from './layoutRegistry.types';

export class LayoutRegistry
  extends BaseRegistry<LayoutComponent>
  implements LayoutRegistryContract
{
  registerLayout(
    type: string,
    component: LayoutComponent,
    priority: number = DEFAULT_REGISTRY_PRIORITY
  ): void {
    this.register(type, component, priority);
  }

  registerLazyLayout(
    type: string,
    loader: () => Promise<LayoutComponent>,
    priority: number = DEFAULT_REGISTRY_PRIORITY
  ): void {
    this.register(type, { load: loader }, priority);
  }

  async resolveLayout(type: string): Promise<LayoutComponent> {
    return this.resolve(type);
  }
}
