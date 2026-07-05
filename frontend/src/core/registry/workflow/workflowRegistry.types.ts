import type { WorkflowComponent, Registry } from '../registry.types';

export interface WorkflowRegistry extends Registry<WorkflowComponent> {
  registerWorkflow(type: string, component: WorkflowComponent, priority?: number): void;
  registerLazyWorkflow(
    type: string,
    loader: () => Promise<WorkflowComponent>,
    priority?: number
  ): void;
  resolveWorkflow(type: string): Promise<WorkflowComponent>;
}
