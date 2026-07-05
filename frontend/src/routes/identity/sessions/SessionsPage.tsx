import { Logout, Laptop, Smartphone, Tablet } from '@mui/icons-material';
import {
  Box,
  Card,
  Typography,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  IconButton,
  Chip,
  Button,
  Divider,
} from '@mui/material';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';

import { apiClient } from '@/core/api/client';
import { ENDPOINTS } from '@/core/api/endpoints';

interface Session {
  id: string;
  ipAddress?: string;
  userAgent?: string;
  lastActivityAt?: string;
  expiresAt?: string;
  isActive: boolean;
}

function deviceIcon(ua?: string) {
  const agent = (ua || '').toLowerCase();
  if (agent.includes('mobile')) return <Smartphone />;
  if (agent.includes('tablet')) return <Tablet />;
  return <Laptop />;
}

export function SessionsPage() {
  const queryClient = useQueryClient();

  const { data: sessions, isLoading } = useQuery<Session[]>({
    queryKey: ['identity', 'sessions'],
    queryFn: async () => {
      const res = await apiClient.get(ENDPOINTS.identity.sessions);
      return res.data.data || res.data;
    },
  });

  const logoutMutation = useMutation({
    mutationFn: async (sessionId: string) => {
      await apiClient.delete(ENDPOINTS.identity.session(sessionId));
    },
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['identity', 'sessions'] }),
  });

  const logoutAllMutation = useMutation({
    mutationFn: async () => {
      await apiClient.delete(ENDPOINTS.identity.sessions + '/all');
    },
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['identity', 'sessions'] }),
  });

  const currentSessionId = sessions?.find((s) => s.isActive)?.id;

  return (
    <Box sx={{ maxWidth: 700, mx: 'auto', p: 3 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Box>
          <Typography variant="h5" fontWeight={700}>
            Active Sessions
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Manage your active login sessions.
          </Typography>
        </Box>
        <Button
          variant="outlined"
          color="error"
          size="small"
          startIcon={<Logout />}
          onClick={() => logoutAllMutation.mutate()}
          disabled={logoutAllMutation.isPending}
          sx={{ borderRadius: 2, textTransform: 'none' }}
        >
          Logout All
        </Button>
      </Box>

      <Card sx={{ borderRadius: 3 }}>
        <List disablePadding>
          {isLoading && (
            <ListItem>
              <ListItemText primary="Loading sessions..." />
            </ListItem>
          )}
          {sessions?.map((session, idx) => (
            <Box key={session.id}>
              {idx > 0 && <Divider component="li" />}
              <ListItem
                secondaryAction={
                  <IconButton
                    edge="end"
                    onClick={() => logoutMutation.mutate(session.id)}
                    disabled={logoutMutation.isPending}
                    color="error"
                  >
                    <Logout fontSize="small" />
                  </IconButton>
                }
              >
                <ListItemIcon>{deviceIcon(session.userAgent)}</ListItemIcon>
                <ListItemText
                  primary={
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                      <Typography variant="body2" fontWeight={600}>
                        {session.ipAddress || 'Unknown IP'}
                      </Typography>
                      {session.id === currentSessionId && (
                        <Chip
                          label="Current"
                          size="small"
                          color="primary"
                          sx={{ height: 20, fontSize: 11 }}
                        />
                      )}
                      {!session.isActive && (
                        <Chip
                          label="Inactive"
                          size="small"
                          color="default"
                          sx={{ height: 20, fontSize: 11 }}
                        />
                      )}
                    </Box>
                  }
                  secondary={
                    <>
                      {session.userAgent && (
                        <Typography
                          variant="caption"
                          display="block"
                          color="text.secondary"
                          noWrap
                          sx={{ maxWidth: 300 }}
                        >
                          {session.userAgent}
                        </Typography>
                      )}
                      {session.lastActivityAt && (
                        <Typography variant="caption" color="text.secondary">
                          Last active: {new Date(session.lastActivityAt).toLocaleString()}
                        </Typography>
                      )}
                    </>
                  }
                />
              </ListItem>
            </Box>
          ))}
          {sessions && sessions.length === 0 && (
            <ListItem>
              <ListItemText primary="No active sessions" />
            </ListItem>
          )}
        </List>
      </Card>
    </Box>
  );
}
