import { Avatar, Box, Chip, Typography } from '@mui/material';
import { useQuery, useQueryClient } from '@tanstack/react-query';
import { useState } from 'react';

import { AdminListPage, ColumnDef } from '../AdminListPage';

import { UserFormDialog } from '@/components/dialogs/UserFormDialog';
import { apiClient } from '@/core/api/client';
import { ENDPOINTS } from '@/core/api/endpoints';

interface User {
  id: string;
  username: string;
  email: string;
  firstName?: string;
  lastName?: string;
  displayName?: string;
  birthDate?: string;
  emailVerified?: boolean;
  website?: string;
  employeeId?: string;
  address?: string;
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
    key: 'birthDate',
    label: 'Birth Date',
    width: 120,
    render: (u) => u.birthDate ?? '—',
  },
  {
    key: 'emailVerified',
    label: 'Verified',
    width: 80,
    render: (u) => (
      <Typography color={u.emailVerified ? 'success.main' : 'text.disabled'}>
        {u.emailVerified ? 'Yes' : 'No'}
      </Typography>
    ),
  },
  {
    key: 'website',
    label: 'Website',
    width: 180,
    render: (u) => u.website ?? '—',
  },
  { key: 'employeeId', label: 'Emp ID', width: 100 },
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
        {(u.roles ?? []).map((r) => (
          <Chip key={r} label={r} size="small" variant="outlined" sx={{ fontSize: 11 }} />
        ))}
      </Box>
    ),
  },
];

export function UsersAdminPage() {
  const queryClient = useQueryClient();
  const { data, isLoading, error, refetch } = useQuery<User[]>({
    queryKey: ['identity', 'users'],
    queryFn: async () => {
      const res = await apiClient.get(ENDPOINTS.identity.users);
      return res.data.data || res.data;
    },
  });

  const [dialogOpen, setDialogOpen] = useState(false);
  const [editingUser, setEditingUser] = useState<User | null>(null);

  const handleCreate = () => {
    setEditingUser(null);
    setDialogOpen(true);
  };

  const handleEdit = (user: User) => {
    setEditingUser(user);
    setDialogOpen(true);
  };

  const handleDelete = async (user: User) => {
    if (!window.confirm(`Delete user "${user.username}"?`)) return;
    try {
      await apiClient.delete(ENDPOINTS.identity.user(user.id));
      queryClient.invalidateQueries({ queryKey: ['identity', 'users'] });
    } catch {
      // error handled by the API interceptor
    }
  };

  const handleSaved = () => {
    queryClient.invalidateQueries({ queryKey: ['identity', 'users'] });
  };

  return (
    <>
      <AdminListPage
        title="Users"
        columns={columns}
        data={data}
        isLoading={isLoading}
        error={error as Error | null}
        onRefresh={refetch}
        onCreate={handleCreate}
        onEdit={handleEdit}
        onDelete={handleDelete}
      />
      <UserFormDialog
        open={dialogOpen}
        user={editingUser}
        onClose={() => setDialogOpen(false)}
        onSaved={handleSaved}
      />
    </>
  );
}
