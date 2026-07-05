import { Chip } from '@mui/material';
import { useQuery } from '@tanstack/react-query';

import { AdminListPage, ColumnDef } from '../AdminListPage';

import { apiClient } from '@/core/api/client';
import { ENDPOINTS } from '@/core/api/endpoints';

interface Org {
  id: string;
  code: string;
  name: string;
  tenantName?: string;
  level: number;
  path: string;
  isActive: boolean;
  createdAt: string;
}

const columns: ColumnDef<Org>[] = [
  { key: 'code', label: 'Code', width: 120 },
  { key: 'name', label: 'Name' },
  { key: 'tenantName', label: 'Tenant', width: 150 },
  {
    key: 'level',
    label: 'Level',
    width: 80,
    render: (o) => <Chip label={o.level} size="small" variant="outlined" />,
  },
  { key: 'path', label: 'Path' },
  {
    key: 'isActive',
    label: 'Status',
    width: 100,
    render: (o) => (o.isActive ? 'Active' : 'Inactive'),
  },
];

export function OrganizationsAdminPage() {
  const { data, isLoading, error, refetch } = useQuery<Org[]>({
    queryKey: ['identity', 'organizations'],
    queryFn: async () => {
      const res = await apiClient.get(ENDPOINTS.identity.organizations);
      return res.data.data || res.data;
    },
  });
  return (
    <AdminListPage
      title="Organizations"
      columns={columns}
      data={data}
      isLoading={isLoading}
      error={error as Error | null}
      onRefresh={refetch}
    />
  );
}
