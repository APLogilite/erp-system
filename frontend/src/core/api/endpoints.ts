export const ENDPOINTS = {
  auth: {
    login: '/auth/login',
    logout: '/auth/logout',
    refresh: '/auth/refresh',
    me: '/auth/me',
  },
  metadata: {
    all: '/metadata',
    models: '/metadata/models',
    views: '/metadata/views',
    workflows: '/metadata/workflows',
  },
  customers: {
    base: '/customers',
    detail: (id: string | number) => `/customers/${id}`,
  },
  users: {
    base: '/users',
    detail: (id: string | number) => `/users/${id}`,
  },
} as const;
