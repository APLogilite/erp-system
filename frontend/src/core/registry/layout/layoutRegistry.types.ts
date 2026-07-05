import type { LayoutComponent, Registry } from '../registry.types';

export interface LayoutRegistry extends Registry<LayoutComponent> {
  registerLayout(type: string, component: LayoutComponent, priority?: number): void;
  registerLazyLayout(type: string, loader: () => Promise<LayoutComponent>, priority?: number): void;
  resolveLayout(type: string): Promise<LayoutComponent>;
}
