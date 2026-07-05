import { useQuery } from '@tanstack/react-query';

import { AdminListPage, ColumnDef } from '../AdminListPage';

import { apiClient } from '@/core/api/client';
import { ENDPOINTS } from '@/core/api/endpoints';

interface Tenant {
  id: string;
  code: string;
  name: string;
  domain?: string;
  isActive: boolean;
  createdAt: string;
}

const columns: ColumnDef<Tenant>[] = [
  { key: 'code', label: 'Code', width: 120 },
  { key: 'name', label: 'Name' },
  { key: 'domain', label: 'Domain', width: 200 },
  {
    key: 'isActive',
    label: 'Status',
    width: 100,
    render: (t) => (t.isActive ? 'Active' : 'Inactive'),
  },
  {
    key: 'createdAt',
    label: 'Created',
    width: 180,
    render: (t) => new Date(t.createdAt).toLocaleDateString(),
  },
];

export function TenantsAdminPage() {
  const { data, isLoading, error, refetch } = useQuery<Tenant[]>({
    queryKey: ['identity', 'tenants'],
    queryFn: async () => {
      const res = await apiClient.get(ENDPOINTS.identity.tenants);
      return res.data.data || res.data;
    },
  });
  return (
    <AdminListPage
      title="Tenants"
      columns={columns}
      data={data}
      isLoading={isLoading}
      error={error as Error | null}
      onRefresh={refetch}
    />
  );
}
