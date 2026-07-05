import { Chip } from '@mui/material';
import { useQuery } from '@tanstack/react-query';

import { AdminListPage, ColumnDef } from '../AdminListPage';

import { apiClient } from '@/core/api/client';
import { ENDPOINTS } from '@/core/api/endpoints';

interface Role {
  id: string;
  code: string;
  name: string;
  description?: string;
  isSystem: boolean;
  createdAt: string;
}

const columns: ColumnDef<Role>[] = [
  { key: 'code', label: 'Code', width: 150 },
  { key: 'name', label: 'Name' },
  { key: 'description', label: 'Description' },
  {
    key: 'isSystem',
    label: 'Type',
    width: 100,
    render: (r) =>
      r.isSystem ? (
        <Chip label="System" size="small" color="info" variant="outlined" />
      ) : (
        <Chip label="Custom" size="small" variant="outlined" />
      ),
  },
];

export function RolesAdminPage() {
  const { data, isLoading, error, refetch } = useQuery<Role[]>({
    queryKey: ['identity', 'roles'],
    queryFn: async () => {
      const res = await apiClient.get(ENDPOINTS.identity.roles);
      return res.data.data || res.data;
    },
  });
  return (
    <AdminListPage
      title="Roles"
      columns={columns}
      data={data}
      isLoading={isLoading}
      error={error as Error | null}
      onRefresh={refetch}
    />
  );
}
