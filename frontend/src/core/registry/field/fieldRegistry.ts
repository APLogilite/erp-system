import { BaseRegistry } from '../registry';
import { DEFAULT_REGISTRY_PRIORITY } from '../registry.constants';
import type { FieldComponent } from '../registry.types';

import type { FieldRegistry as FieldRegistryContract } from './fieldRegistry.types';

export class FieldRegistry extends BaseRegistry<FieldComponent> implements FieldRegistryContract {
  registerField(
    type: string,
    component: FieldComponent,
    priority: number = DEFAULT_REGISTRY_PRIORITY
  ): void {
    this.register(type, component, priority);
  }

  registerLazyField(
    type: string,
    loader: () => Promise<FieldComponent>,
    priority: number = DEFAULT_REGISTRY_PRIORITY
  ): void {
    this.register(type, { load: loader }, priority);
  }

  async resolveField(type: string): Promise<FieldComponent> {
    return this.resolve(type);
  }

  async getFieldComponent(type: string): Promise<FieldComponent> {
    return this.resolveField(type);
  }
}
