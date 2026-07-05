import type { FieldComponent, Registry } from '../registry.types';

export interface FieldRegistry extends Registry<FieldComponent> {
  registerField(type: string, component: FieldComponent, priority?: number): void;
  registerLazyField(type: string, loader: () => Promise<FieldComponent>, priority?: number): void;
  resolveField(type: string): Promise<FieldComponent>;
}
