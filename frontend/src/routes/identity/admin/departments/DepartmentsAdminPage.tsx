import { useQuery } from '@tanstack/react-query';

import { AdminListPage, ColumnDef } from '../AdminListPage';

import { apiClient } from '@/core/api/client';
import { ENDPOINTS } from '@/core/api/endpoints';

interface Dept {
  id: string;
  code: string;
  name: string;
  companyName?: string;
  headName?: string;
  isActive: boolean;
}

const columns: ColumnDef<Dept>[] = [
  { key: 'code', label: 'Code', width: 120 },
  { key: 'name', label: 'Name' },
  { key: 'companyName', label: 'Company', width: 150 },
  { key: 'headName', label: 'Head', width: 150 },
  {
    key: 'isActive',
    label: 'Status',
    width: 100,
    render: (d) => (d.isActive ? 'Active' : 'Inactive'),
  },
];

export function DepartmentsAdminPage() {
  const { data, isLoading, error, refetch } = useQuery<Dept[]>({
    queryKey: ['identity', 'departments'],
    queryFn: async () => {
      const res = await apiClient.get(ENDPOINTS.identity.departments);
      return res.data.data || res.data;
    },
  });
  return (
    <AdminListPage
      title="Departments"
      columns={columns}
      data={data}
      isLoading={isLoading}
      error={error as Error | null}
      onRefresh={refetch}
    />
  );
}
