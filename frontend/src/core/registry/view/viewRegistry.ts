import { BaseRegistry } from '../registry';
import { DEFAULT_REGISTRY_PRIORITY } from '../registry.constants';
import type { ViewComponent } from '../registry.types';

import type { ViewRegistry as ViewRegistryContract } from './viewRegistry.types';

export class ViewRegistry extends BaseRegistry<ViewComponent> implements ViewRegistryContract {
  registerView(
    type: string,
    component: ViewComponent,
    priority: number = DEFAULT_REGISTRY_PRIORITY
  ): void {
    this.register(type, component, priority);
  }

  registerLazyView(
    type: string,
    loader: () => Promise<ViewComponent>,
    priority: number = DEFAULT_REGISTRY_PRIORITY
  ): void {
    this.register(type, { load: loader }, priority);
  }

  async resolveView(type: string): Promise<ViewComponent> {
    return this.resolve(type);
  }
}
