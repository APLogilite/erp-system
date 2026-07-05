import { ActionDefinition } from '@/core/metadata/schema/action/ActionDefinition';
import { LayoutDefinition } from '@/core/metadata/schema/layout/LayoutDefinition';
import { RuntimeMetadataBundle } from '@/core/metadata/schema/RuntimeMetadataBundle';
import { ViewDefinition } from '@/core/metadata/schema/view/ViewDefinition';
import { WorkflowDefinition } from '@/core/metadata/schema/workflow/WorkflowDefinition';

export type RuntimeMode = 'CREATE' | 'EDIT' | 'VIEW' | 'INLINE' | 'DIALOG';

export interface RuntimeContextValue {
  metadataBundle: RuntimeMetadataBundle;
  currentView: ViewDefinition;
  currentLayout: LayoutDefinition;
  record?: Record<string, unknown>;
  mode: RuntimeMode;
  loading: boolean;
  permissions: string[];
  actions: ActionDefinition[];
  workflow?: WorkflowDefinition;
}

export interface RuntimeRenderOptions {
  metadataBundle: RuntimeMetadataBundle;
  viewCode: string;
  record?: Record<string, unknown>;
  mode?: RuntimeMode;
  permissions?: string[];
}
