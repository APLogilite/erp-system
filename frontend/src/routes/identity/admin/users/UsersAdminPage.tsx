import { Avatar, Chip, Box } from '@mui/material';
import { useQuery } from '@tanstack/react-query';

import { AdminListPage, ColumnDef } from '../AdminListPage';

import { apiClient } from '@/core/api/client';
import { ENDPOINTS } from '@/core/api/endpoints';

interface User {
  id: string;
  username: string;
  email: string;
  firstName?: string;
  lastName?: string;
  displayName?: string;
  status: string;
  roles: string[];
  createdAt: string;
}

const columns: ColumnDef<User>[] = [
  {
    key: 'avatar',
    label: '',
    width: 60,
    render: (u) => (
      <Avatar sx={{ width: 32, height: 32, fontSize: 13, bgcolor: 'primary.main' }}>
        {(u.displayName || u.username).charAt(0).toUpperCase()}
      </Avatar>
    ),
  },
  { key: 'username', label: 'Username', width: 130 },
  { key: 'email', label: 'Email' },
  { key: 'displayName', label: 'Display Name', width: 150 },
  {
    key: 'status',
    label: 'Status',
    width: 100,
    render: (u) => (
      <Chip
        label={u.status}
        size="small"
        color={u.status === 'ACTIVE' ? 'success' : 'default'}
        variant="outlined"
      />
    ),
  },
  {
    key: 'roles',
    label: 'Roles',
    width: 200,
    render: (u) => (
      <Box sx={{ display: 'flex', gap: 0.3, flexWrap: 'wrap' }}>
        {u.roles.map((r) => (
          <Chip key={r} label={r} size="small" variant="outlined" sx={{ fontSize: 11 }} />
        ))}
      </Box>
    ),
  },
];

export function UsersAdminPage() {
  const { data, isLoading, error, refetch } = useQuery<User[]>({
    queryKey: ['identity', 'users'],
    queryFn: async () => {
      const res = await apiClient.get(ENDPOINTS.identity.users);
      return res.data.data || res.data;
    },
  });
  return (
    <AdminListPage
      title="Users"
      columns={columns}
      data={data}
      isLoading={isLoading}
      error={error as Error | null}
      onRefresh={refetch}
    />
  );
}
