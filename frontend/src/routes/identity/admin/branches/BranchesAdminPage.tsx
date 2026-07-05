import { useQuery } from '@tanstack/react-query';

import { AdminListPage, ColumnDef } from '../AdminListPage';

import { apiClient } from '@/core/api/client';
import { ENDPOINTS } from '@/core/api/endpoints';

interface Branch {
  id: string;
  code: string;
  name: string;
  address?: string;
  phone?: string;
  companyName?: string;
  isActive: boolean;
}

const columns: ColumnDef<Branch>[] = [
  { key: 'code', label: 'Code', width: 120 },
  { key: 'name', label: 'Name' },
  { key: 'address', label: 'Address' },
  { key: 'phone', label: 'Phone', width: 150 },
  { key: 'companyName', label: 'Company', width: 150 },
  {
    key: 'isActive',
    label: 'Status',
    width: 100,
    render: (b) => (b.isActive ? 'Active' : 'Inactive'),
  },
];

export function BranchesAdminPage() {
  const { data, isLoading, error, refetch } = useQuery<Branch[]>({
    queryKey: ['identity', 'branches'],
    queryFn: async () => {
      const res = await apiClient.get(ENDPOINTS.identity.branches);
      return res.data.data || res.data;
    },
  });
  return (
    <AdminListPage
      title="Branches"
      columns={columns}
      data={data}
      isLoading={isLoading}
      error={error as Error | null}
      onRefresh={refetch}
    />
  );
}
