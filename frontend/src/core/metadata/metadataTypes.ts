export interface FieldMetadata {
  name: string;
  label: string;
  type:
    | 'string'
    | 'integer'
    | 'decimal'
    | 'boolean'
    | 'date'
    | 'datetime'
    | 'many2one'
    | 'one2many';
  required?: boolean;
  readonly?: boolean;
  relationModel?: string; // for relations
}

export interface ModelMetadata {
  name: string;
  label: string;
  pluralLabel: string;
  fields: Record<string, FieldMetadata>;
}

export interface ViewMetadata {
  id: string;
  model: string;
  type: 'list' | 'form' | 'search';
  title?: string;
  fields: string[];
  config?: Record<string, unknown>;
}

export interface WorkflowTransition {
  id: string;
  name: string;
  label: string;
  fromState: string;
  toState: string;
  requiredPermission?: string;
}

export interface WorkflowMetadata {
  id: string;
  model: string;
  initialState: string;
  states: string[];
  transitions: WorkflowTransition[];
}

export interface MetadataState {
  models: Record<string, ModelMetadata>;
  views: Record<string, ViewMetadata>;
  layouts: Record<string, unknown>;
  workflows: Record<string, WorkflowMetadata>;
  permissions: Record<string, string[]>;
  isInitialized: boolean;
}

export interface MetadataActions {
  setMetadata: (payload: {
    models?: ModelMetadata[];
    views?: ViewMetadata[];
    workflows?: WorkflowMetadata[];
  }) => void;
  registerModel: (model: ModelMetadata) => void;
  registerView: (view: ViewMetadata) => void;
  registerWorkflow: (workflow: WorkflowMetadata) => void;
  clearMetadata: () => void;
}

export type MetadataStore = MetadataState & MetadataActions;
