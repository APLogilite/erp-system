import { Person, Email, Badge, CalendarToday, CheckCircle, Block } from '@mui/icons-material';
import { Box, Card, CardContent, Typography, Avatar, Chip, Grid, Divider } from '@mui/material';
import { useQuery } from '@tanstack/react-query';

import { authService } from '@/core/api/services/authService';
import { selectCurrentUser } from '@/core/auth/authSelectors';
import { useAuthStore } from '@/core/auth/authStore';

export function ProfilePage() {
  const user = useAuthStore(selectCurrentUser);

  const { data: profile } = useQuery({
    queryKey: ['auth', 'me'],
    queryFn: () => authService.getCurrentUser(),
    initialData: user || undefined,
  });

  const statusColor = profile?.status === 'ACTIVE' ? 'success' : 'error';
  const StatusIcon = profile?.status === 'ACTIVE' ? CheckCircle : Block;
  const displayName =
    profile?.displayName ||
    `${profile?.firstName || ''} ${profile?.lastName || ''}`.trim() ||
    profile?.username;

  return (
    <Box sx={{ maxWidth: 800, mx: 'auto', p: 3 }}>
      <Card sx={{ borderRadius: 3, overflow: 'hidden' }}>
        <Box
          sx={{ height: 120, background: 'linear-gradient(135deg, #1976d2 0%, #82b1ff 100%)' }}
        />
        <CardContent
          sx={{ p: 3, mt: -8, display: 'flex', flexDirection: 'column', alignItems: 'center' }}
        >
          <Avatar
            src={profile?.avatarUrl}
            sx={{
              width: 96,
              height: 96,
              border: '4px solid white',
              boxShadow: 2,
              mb: 2,
              bgcolor: 'primary.main',
              fontSize: 36,
            }}
          >
            {profile?.username?.charAt(0).toUpperCase()}
          </Avatar>
          <Typography variant="h5" fontWeight={700}>
            {displayName}
          </Typography>
          <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>
            @{profile?.username}
          </Typography>
          <Chip
            icon={<StatusIcon />}
            label={profile?.status || 'UNKNOWN'}
            color={statusColor}
            size="small"
            variant="outlined"
          />
        </CardContent>
        <Divider />
        <CardContent sx={{ p: 3 }}>
          <Typography variant="subtitle1" fontWeight={600} gutterBottom>
            Account Details
          </Typography>
          <Grid container spacing={2}>
            <Grid item xs={12} sm={6}>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 2 }}>
                <Email color="action" fontSize="small" />
                <Box>
                  <Typography variant="caption" color="text.secondary">
                    Email
                  </Typography>
                  <Typography variant="body2">{profile?.email}</Typography>
                </Box>
              </Box>
            </Grid>
            <Grid item xs={12} sm={6}>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 2 }}>
                <Badge color="action" fontSize="small" />
                <Box>
                  <Typography variant="caption" color="text.secondary">
                    Username
                  </Typography>
                  <Typography variant="body2">{profile?.username}</Typography>
                </Box>
              </Box>
            </Grid>
            <Grid item xs={12} sm={6}>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                <Person color="action" fontSize="small" />
                <Box>
                  <Typography variant="caption" color="text.secondary">
                    Display Name
                  </Typography>
                  <Typography variant="body2">{displayName}</Typography>
                </Box>
              </Box>
            </Grid>
            <Grid item xs={12} sm={6}>
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                <CalendarToday color="action" fontSize="small" />
                <Box>
                  <Typography variant="caption" color="text.secondary">
                    User ID
                  </Typography>
                  <Typography variant="body2" sx={{ fontFamily: 'monospace', fontSize: 12 }}>
                    {profile?.id}
                  </Typography>
                </Box>
              </Box>
            </Grid>
          </Grid>
        </CardContent>
        <Divider />
        <CardContent sx={{ p: 3 }}>
          <Typography variant="subtitle1" fontWeight={600} gutterBottom>
            Roles & Permissions
          </Typography>
          <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1, mb: 2 }}>
            {profile?.roles?.map((role) => (
              <Chip key={role} label={role} size="small" color="primary" variant="outlined" />
            ))}
            {(!profile?.roles || profile.roles.length === 0) && (
              <Typography variant="body2" color="text.secondary">
                No roles assigned
              </Typography>
            )}
          </Box>
          <Typography variant="subtitle2" color="text.secondary" gutterBottom>
            Permissions ({profile?.permissions?.length || 0})
          </Typography>
          <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 0.5 }}>
            {profile?.permissions?.slice(0, 20).map((perm) => (
              <Chip key={perm} label={perm} size="small" variant="outlined" sx={{ fontSize: 11 }} />
            ))}
            {(profile?.permissions?.length || 0) > 20 && (
              <Typography variant="caption" color="text.secondary" sx={{ alignSelf: 'center' }}>
                +{profile!.permissions.length - 20} more
              </Typography>
            )}
          </Box>
        </CardContent>
      </Card>
    </Box>
  );
}
