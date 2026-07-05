import type { ViewComponent, Registry } from '../registry.types';

export interface ViewRegistry extends Registry<ViewComponent> {
  registerView(type: string, component: ViewComponent, priority?: number): void;
  registerLazyView(type: string, loader: () => Promise<ViewComponent>, priority?: number): void;
  resolveView(type: string): Promise<ViewComponent>;
}
