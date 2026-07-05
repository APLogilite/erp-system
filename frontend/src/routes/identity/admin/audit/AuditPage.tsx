import { Chip, Box, TextField, MenuItem, Stack } from '@mui/material';
import { useQuery } from '@tanstack/react-query';
import { useState } from 'react';

import { AdminListPage, ColumnDef } from '../AdminListPage';

import { apiClient } from '@/core/api/client';
import { ENDPOINTS } from '@/core/api/endpoints';

interface AuditRecord {
  id: string;
  eventType: string;
  userId?: string;
  username?: string;
  ipAddress?: string;
  userAgent?: string;
  oldValue?: string;
  newValue?: string;
  occurredAt: string;
}

const eventTypes = [
  'LOGIN_SUCCESS',
  'LOGIN_FAILURE',
  'LOGOUT',
  'USER_CREATED',
  'USER_ACTIVATED',
  'USER_DEACTIVATED',
  'PASSWORD_CHANGED',
  'PASSWORD_RESET',
  'ROLE_ASSIGNED',
  'ROLE_REMOVED',
  'PERMISSION_ASSIGNED',
  'PERMISSION_REMOVED',
  'CONTEXT_CHANGED',
  'SESSION_EXPIRED',
  'SESSION_REVOKED',
];

const columns: ColumnDef<AuditRecord>[] = [
  {
    key: 'eventType',
    label: 'Event',
    width: 150,
    render: (a) => <Chip label={a.eventType} size="small" color="primary" variant="outlined" />,
  },
  { key: 'username', label: 'User', width: 130 },
  { key: 'ipAddress', label: 'IP', width: 130 },
  {
    key: 'newValue',
    label: 'Details',
    render: (a) => a.newValue || a.oldValue || '-',
  },
  {
    key: 'occurredAt',
    label: 'Timestamp',
    width: 180,
    render: (a) => new Date(a.occurredAt).toLocaleString(),
  },
];

export function AuditPage() {
  const [eventFilter, setEventFilter] = useState('');
  const [userFilter, setUserFilter] = useState('');

  const queryParams = new URLSearchParams();
  if (eventFilter) queryParams.set('eventType', eventFilter);
  if (userFilter) queryParams.set('username', userFilter);

  const { data, isLoading, error, refetch } = useQuery<AuditRecord[]>({
    queryKey: ['identity', 'audit', eventFilter, userFilter],
    queryFn: async () => {
      const qs = queryParams.toString();
      const url = ENDPOINTS.identity.audit + (qs ? `?${qs}` : '');
      const res = await apiClient.get(url);
      return res.data.data || res.data;
    },
  });

  return (
    <Box>
      <Box sx={{ p: 3, pb: 0 }}>
        <Stack direction="row" spacing={2} sx={{ mb: 2 }}>
          <TextField
            select
            label="Event Type"
            size="small"
            value={eventFilter}
            onChange={(e) => setEventFilter(e.target.value)}
            sx={{ minWidth: 180 }}
          >
            <MenuItem value="">All Events</MenuItem>
            {eventTypes.map((et) => (
              <MenuItem key={et} value={et}>
                {et}
              </MenuItem>
            ))}
          </TextField>
          <TextField
            label="Username"
            size="small"
            value={userFilter}
            onChange={(e) => setUserFilter(e.target.value)}
            sx={{ minWidth: 200 }}
          />
        </Stack>
      </Box>
      <AdminListPage
        title="Audit Log"
        columns={columns}
        data={data}
        isLoading={isLoading}
        error={error as Error | null}
        onRefresh={refetch}
      />
    </Box>
  );
}
