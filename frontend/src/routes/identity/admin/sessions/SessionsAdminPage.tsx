import { Logout } from '@mui/icons-material';
import { Chip, IconButton } from '@mui/material';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';

import { AdminListPage, ColumnDef } from '../AdminListPage';

import { apiClient } from '@/core/api/client';
import { ENDPOINTS } from '@/core/api/endpoints';

interface AdminSession {
  id: string;
  userId: string;
  username: string;
  ipAddress?: string;
  userAgent?: string;
  lastActivityAt?: string;
  expiresAt?: string;
  isActive: boolean;
}

const columns: ColumnDef<AdminSession>[] = [
  { key: 'username', label: 'User', width: 130 },
  { key: 'ipAddress', label: 'IP', width: 130 },
  { key: 'userAgent', label: 'User Agent' },
  {
    key: 'isActive',
    label: 'Status',
    width: 100,
    render: (s) =>
      s.isActive ? (
        <Chip label="Active" size="small" color="success" variant="outlined" />
      ) : (
        <Chip label="Inactive" size="small" variant="outlined" />
      ),
  },
  {
    key: 'lastActivityAt',
    label: 'Last Activity',
    width: 180,
    render: (s) => (s.lastActivityAt ? new Date(s.lastActivityAt).toLocaleString() : '-'),
  },
];

export function SessionsAdminPage() {
  const queryClient = useQueryClient();
  const { data, isLoading, error, refetch } = useQuery<AdminSession[]>({
    queryKey: ['identity', 'admin-sessions'],
    queryFn: async () => {
      const res = await apiClient.get(ENDPOINTS.identity.sessions);
      return res.data.data || res.data;
    },
  });

  const forceLogout = useMutation({
    mutationFn: async (sessionId: string) => {
      await apiClient.delete(ENDPOINTS.identity.session(sessionId));
    },
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['identity', 'admin-sessions'] }),
  });

  return (
    <AdminListPage
      title="Session Management"
      columns={columns}
      data={data}
      isLoading={isLoading}
      error={error as Error | null}
      onRefresh={refetch}
      renderActions={(session) => (
        <IconButton
          size="small"
          color="error"
          onClick={() => forceLogout.mutate(session.id)}
          disabled={!session.isActive}
        >
          <Logout fontSize="small" />
        </IconButton>
      )}
    />
  );
}
