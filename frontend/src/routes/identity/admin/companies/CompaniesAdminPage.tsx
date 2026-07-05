import { useQuery } from '@tanstack/react-query';

import { AdminListPage, ColumnDef } from '../AdminListPage';

import { apiClient } from '@/core/api/client';
import { ENDPOINTS } from '@/core/api/endpoints';

interface Company {
  id: string;
  code: string;
  name: string;
  taxId?: string;
  organizationName?: string;
  isActive: boolean;
  createdAt: string;
}

const columns: ColumnDef<Company>[] = [
  { key: 'code', label: 'Code', width: 120 },
  { key: 'name', label: 'Name' },
  { key: 'taxId', label: 'Tax ID', width: 150 },
  { key: 'organizationName', label: 'Organization', width: 150 },
  {
    key: 'isActive',
    label: 'Status',
    width: 100,
    render: (c) => (c.isActive ? 'Active' : 'Inactive'),
  },
];

export function CompaniesAdminPage() {
  const { data, isLoading, error, refetch } = useQuery<Company[]>({
    queryKey: ['identity', 'companies'],
    queryFn: async () => {
      const res = await apiClient.get(ENDPOINTS.identity.companies);
      return res.data.data || res.data;
    },
  });
  return (
    <AdminListPage
      title="Companies"
      columns={columns}
      data={data}
      isLoading={isLoading}
      error={error as Error | null}
      onRefresh={refetch}
    />
  );
}
