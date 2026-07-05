import { createContext, ReactNode, useContext, useMemo } from 'react';

import { ActionRegistry } from './action/actionRegistry';
import { FieldRegistry } from './field/fieldRegistry';
import { LayoutRegistry } from './layout/layoutRegistry';
import type { RegistryPlugin } from './registry.types';
import { ViewRegistry } from './view/viewRegistry';
import { WorkflowRegistry } from './workflow/workflowRegistry';

export interface RegistryContextValue {
  fields: FieldRegistry;
  layouts: LayoutRegistry;
  actions: ActionRegistry;
  workflows: WorkflowRegistry;
  views: ViewRegistry;
}

const RegistryContext = createContext<RegistryContextValue | null>(null);

const DefaultTextField = ({ label }: { label?: string }) => <div>{label ?? 'Text Field'}</div>;
const DefaultTextAreaField = ({ label }: { label?: string }) => <div>{label ?? 'Text Area'}</div>;
const DefaultNumberField = ({ label }: { label?: string }) => <div>{label ?? 'Number Field'}</div>;
const DefaultBooleanField = ({ label }: { label?: string }) => (
  <div>{label ?? 'Boolean Field'}</div>
);
const DefaultSelectField = ({ label }: { label?: string }) => <div>{label ?? 'Select Field'}</div>;
const DefaultRelationField = ({ label }: { label?: string }) => (
  <div>{label ?? 'Relation Field'}</div>
);

const DefaultPageLayout = ({ children }: { children?: ReactNode }) => <section>{children}</section>;
const DefaultSectionLayout = ({ children }: { children?: ReactNode }) => <div>{children}</div>;
const DefaultGroupLayout = ({ children }: { children?: ReactNode }) => <div>{children}</div>;
const DefaultRowLayout = ({ children }: { children?: ReactNode }) => <div>{children}</div>;
const DefaultColumnLayout = ({ children }: { children?: ReactNode }) => <div>{children}</div>;
const DefaultTabsLayout = ({ children }: { children?: ReactNode }) => <div>{children}</div>;
const DefaultTabLayout = ({ children }: { children?: ReactNode }) => <div>{children}</div>;
const DefaultGridLayout = ({ children }: { children?: ReactNode }) => <div>{children}</div>;
const DefaultPanelLayout = ({ children }: { children?: ReactNode }) => <div>{children}</div>;

const DefaultWorkflowView = () => <div>Workflow</div>;
const DefaultViewComponent = () => <div>Runtime View</div>;

function bootstrapRegistries(registries: RegistryContextValue, plugins: RegistryPlugin[]) {
  registries.fields.registerField('TEXT', DefaultTextField);
  registries.fields.registerField('TEXTAREA', DefaultTextAreaField);
  registries.fields.registerField('NUMBER', DefaultNumberField);
  registries.fields.registerField('DECIMAL', DefaultNumberField);
  registries.fields.registerField('BOOLEAN', DefaultBooleanField);
  registries.fields.registerField('SELECT', DefaultSelectField);
  registries.fields.registerField('MULTI_SELECT', DefaultSelectField);
  registries.fields.registerField('MANY_TO_ONE', DefaultRelationField);
  registries.fields.registerField('ONE_TO_MANY', DefaultRelationField);
  registries.fields.registerField('MANY_TO_MANY', DefaultRelationField);
  registries.fields.registerField('TREE', DefaultRelationField);
  registries.fields.registerField('JSON', DefaultTextField);

  registries.layouts.registerLayout('PAGE', DefaultPageLayout);
  registries.layouts.registerLayout('SECTION', DefaultSectionLayout);
  registries.layouts.registerLayout('GROUP', DefaultGroupLayout);
  registries.layouts.registerLayout('ROW', DefaultRowLayout);
  registries.layouts.registerLayout('COLUMN', DefaultColumnLayout);
  registries.layouts.registerLayout('TABS', DefaultTabsLayout);
  registries.layouts.registerLayout('TAB', DefaultTabLayout);
  registries.layouts.registerLayout('GRID', DefaultGridLayout);
  registries.layouts.registerLayout('PANEL', DefaultPanelLayout);

  registries.actions.registerAction('BUTTON', async (payload: unknown) => ({
    success: true,
    payload,
  }));
  registries.actions.registerAction('SERVER_ACTION', async (payload: unknown) => ({
    success: true,
    payload,
  }));
  registries.actions.registerAction('NAVIGATION', async (payload: unknown) => ({
    success: true,
    payload,
  }));
  registries.actions.registerAction('WORKFLOW', async (payload: unknown) => ({
    success: true,
    payload,
  }));
  registries.actions.registerAction('CUSTOM', async (payload: unknown) => ({
    success: true,
    payload,
  }));

  registries.workflows.registerWorkflow('APPROVAL', DefaultWorkflowView);
  registries.workflows.registerWorkflow('DOCUMENT', DefaultWorkflowView);
  registries.workflows.registerWorkflow('CUSTOM', DefaultWorkflowView);

  registries.views.registerView('FORM', DefaultViewComponent);
  registries.views.registerView('GRID', DefaultViewComponent);
  registries.views.registerView('KANBAN', DefaultViewComponent);
  registries.views.registerView('DETAIL', DefaultViewComponent);
  registries.views.registerView('DASHBOARD', DefaultViewComponent);

  plugins.forEach((plugin) => {
    plugin.registerFields?.(registries.fields);
    plugin.registerLayouts?.(registries.layouts);
    plugin.registerActions?.(registries.actions);
    plugin.registerWorkflows?.(registries.workflows);
    plugin.registerViews?.(registries.views);
  });
}

export function RegistryProvider({
  children,
  plugins = [],
}: {
  children: ReactNode;
  plugins?: RegistryPlugin[];
}) {
  const value = useMemo<RegistryContextValue>(() => {
    const registries = {
      fields: new FieldRegistry(),
      layouts: new LayoutRegistry(),
      actions: new ActionRegistry(),
      workflows: new WorkflowRegistry(),
      views: new ViewRegistry(),
    };

    bootstrapRegistries(registries, plugins);

    return registries;
  }, [plugins]);

  return <RegistryContext.Provider value={value}>{children}</RegistryContext.Provider>;
}

export function useRegistry() {
  const context = useContext(RegistryContext);
  if (!context) {
    throw new Error('useRegistry must be used within a RegistryProvider');
  }
  return context;
}
