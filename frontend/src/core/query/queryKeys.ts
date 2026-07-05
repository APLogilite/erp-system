export const queryKeys = {
  auth: {
    all: ['auth'] as const,
    currentUser: () => ['auth', 'current-user'] as const,
  },
  metadata: {
    all: ['metadata'] as const,
    models: () => ['metadata', 'models'] as const,
    model: (modelName: string) => ['metadata', 'models', modelName] as const,
    views: () => ['metadata', 'views'] as const,
    view: (viewName: string) => ['metadata', 'views', viewName] as const,
    layouts: () => ['metadata', 'layouts'] as const,
    workflows: () => ['metadata', 'workflows'] as const,
  },
  models: {
    all: ['models'] as const,
    list: (modelName: string, filters?: Record<string, unknown>) =>
      ['models', modelName, 'list', filters ?? {}] as const,
    detail: (modelName: string, id: string | number) =>
      ['models', modelName, 'detail', id] as const,
  },
  forms: {
    all: ['forms'] as const,
    layout: (formName: string) => ['forms', 'layout', formName] as const,
  },
  grids: {
    all: ['grids'] as const,
    config: (gridName: string) => ['grids', 'config', gridName] as const,
  },
  customers: {
    all: ['customers'] as const,
    list: (filters?: Record<string, unknown>) => ['customers', 'list', filters ?? {}] as const,
    detail: (id: string | number) => ['customers', 'detail', id] as const,
  },
  users: {
    all: ['users'] as const,
    list: (filters?: Record<string, unknown>) => ['users', 'list', filters ?? {}] as const,
    detail: (id: string | number) => ['users', 'detail', id] as const,
  },
} as const;
