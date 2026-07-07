import { CircularProgress, Box } from '@mui/material';
import { useQuery } from '@tanstack/react-query';
import { Navigate, Outlet } from 'react-router-dom';

import { apiClient } from '@/core/api/client';
import { ENDPOINTS } from '@/core/api/endpoints';

interface ContextCurrent {
  tenantId?: string | null;
  organizationId?: string | null;
  companyId?: string | null;
  branchId?: string | null;
  roles?: string[] | null;
}

interface ContextOption {
  id: string;
}

interface ContextOptionsResponse {
  tenants: ContextOption[];
  organizations: ContextOption[];
  companies: ContextOption[];
  branches: ContextOption[];
  roles: string[];
}

export function ContextGuard() {
  const { data: current, isLoading: curLoading } = useQuery<ContextCurrent>({
    queryKey: ['context', 'current-guard'],
    queryFn: async () => {
      const res = await apiClient.get(ENDPOINTS.context.current);
      return res.data.data || res.data;
    },
    retry: 1,
    staleTime: 15000,
  });

  const { data: options, isLoading: optLoading } = useQuery<ContextOptionsResponse>({
    queryKey: ['context', 'options-guard'],
    queryFn: async () => {
      const res = await apiClient.get(ENDPOINTS.context.options);
      return res.data.data || res.data;
    },
    retry: 1,
    staleTime: 15000,
  });

  if (curLoading || optLoading) {
    return (
      <Box
        sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', minHeight: '100vh' }}
      >
        <CircularProgress />
      </Box>
    );
  }

  if (!current || !options) {
    return <Navigate to="/select-context" replace />;
  }

  const hasOrgs = (options.organizations?.length ?? 0) > 0;
  const hasCompanies = (options.companies?.length ?? 0) > 0;
  const hasBranches = (options.branches?.length ?? 0) > 0;
  const hasRoles = (options.roles?.length ?? 0) > 0;

  const needsContext =
    !current.tenantId ||
    (hasOrgs && !current.organizationId) ||
    (hasCompanies && !current.companyId) ||
    (hasBranches && !current.branchId) ||
    (hasRoles && (!current.roles || current.roles.length === 0));

  if (needsContext) {
    return <Navigate to="/select-context" replace />;
  }

  return <Outlet />;
}
