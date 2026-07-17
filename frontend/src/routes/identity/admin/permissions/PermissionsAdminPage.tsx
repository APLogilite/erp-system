import { Chip } from '@mui/material';
import { useQuery } from '@tanstack/react-query';

import { AdminListPage, ColumnDef } from '../AdminListPage';

import { apiClient } from '@/core/api/client';
import { ENDPOINTS } from '@/core/api/endpoints';

interface Permission {
  id: string;
  code: string;
  name: string;
  resourceType: string;
  resource: string;
  action: string;
  module: string;
}

const columns: ColumnDef<Permission>[] = [
  { key: 'code', label: 'Code', width: 150 },
  { key: 'name', label: 'Name' },
  {
    key: 'resourceType',
    label: 'Resource Type',
    width: 130,
    render: (p) => <Chip label={p.resourceType} size="small" variant="outlined" />,
  },
  { key: 'resource', label: 'Resource', width: 120 },
  {
    key: 'action',
    label: 'Action',
    width: 100,
    render: (p) => (
      <Chip
        label={p.action}
        size="small"
        color={p.action === 'ADMIN' ? 'warning' : 'primary'}
        variant="outlined"
      />
    ),
  },
  { key: 'module', label: 'Module', width: 120 },
];

export function PermissionsAdminPage() {
  const { data, isLoading, error, refetch } = useQuery<Permission[]>({
    queryKey: ['identity', 'permissions'],
    queryFn: async () => {
      const res = await apiClient.get(ENDPOINTS.identity.permissions);
      return res.data.data || res.data;
    },
  });
  return (
    <AdminListPage
      title="Permissions"
      columns={columns}
      data={data}
      isLoading={isLoading}
      error={error as Error | null}
      onRefresh={refetch}
    />
  );
}
