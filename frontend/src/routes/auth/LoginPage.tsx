import { Visibility, VisibilityOff, LockOutlined, PersonOutline } from '@mui/icons-material';
import {
  Box,
  Card,
  CardContent,
  TextField,
  Button,
  Typography,
  IconButton,
  InputAdornment,
  useTheme,
  Alert,
  Link,
} from '@mui/material';
import { useMutation } from '@tanstack/react-query';
import { useState } from 'react';
import { useNavigate, Link as RouterLink } from 'react-router-dom';

import { authService } from '@/core/api/services/authService';
import { useAuthStore } from '@/core/auth/authStore';

export function LoginPage() {
  const theme = useTheme();
  const navigate = useNavigate();
  const loginAction = useAuthStore((s) => s.login);

  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);

  const { mutate, isPending, error } = useMutation({
    mutationFn: () => authService.login({ username, password }),
    onSuccess: (data) => {
      console.log('[LoginPage] login success, user:', data.user.username);
      loginAction(
        {
          id: data.user.id,
          email: data.user.email,
          username: data.user.username,
          firstName: data.user.firstName,
          lastName: data.user.lastName,
          displayName: data.user.displayName,
          avatarUrl: data.user.avatarUrl,
          status: data.user.status,
          roles: data.user.roles,
          permissions: data.user.permissions,
        },
        data.accessToken,
        data.refreshToken
      );
      console.log('[LoginPage] navigating to /select-context');
      navigate('/select-context', { replace: true });
    },
  });

  return (
    <Box
      sx={{
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        minHeight: '100vh',
        background:
          theme.palette.mode === 'dark'
            ? 'radial-gradient(circle at 50% 50%, #151a30 0%, #0b0e1a 100%)'
            : 'radial-gradient(circle at 50% 50%, #f4f6fb 0%, #e2e8f0 100%)',
        p: 2,
      }}
    >
      <Card
        sx={{
          width: '100%',
          maxWidth: 450,
          borderRadius: 4,
          boxShadow:
            theme.palette.mode === 'dark'
              ? '0 12px 40px rgba(0,0,0,0.5)'
              : '0 12px 40px rgba(148,163,184,0.15)',
          border: `1px solid ${theme.palette.mode === 'dark' ? 'rgba(255,255,255,0.05)' : 'rgba(0,0,0,0.05)'}`,
          overflow: 'hidden',
        }}
      >
        <Box sx={{ height: 6, background: 'linear-gradient(90deg, #1976d2 0%, #82b1ff 100%)' }} />
        <CardContent sx={{ p: 4 }}>
          <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', mb: 3 }}>
            <Box
              sx={{
                width: 56,
                height: 56,
                borderRadius: '50%',
                backgroundColor: theme.palette.primary.main + '15',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                mb: 2,
              }}
            >
              <LockOutlined color="primary" sx={{ fontSize: 28 }} />
            </Box>
            <Typography variant="h5" component="h1" fontWeight={700} gutterBottom>
              ERP Portal Login
            </Typography>
            <Typography variant="body2" color="text.secondary" textAlign="center">
              Enter credentials to securely access the ERP core services.
            </Typography>
          </Box>

          {error && (
            <Alert severity="error" sx={{ mb: 3, borderRadius: 2 }}>
              {error instanceof Error ? error.message : 'Authentication failed'}
            </Alert>
          )}

          <form
            onSubmit={(e) => {
              e.preventDefault();
              mutate();
            }}
          >
            <TextField
              fullWidth
              label="Username"
              variant="outlined"
              margin="normal"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              disabled={isPending}
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <PersonOutline color="action" />
                  </InputAdornment>
                ),
              }}
              sx={{ mb: 2 }}
            />
            <TextField
              fullWidth
              label="Password"
              variant="outlined"
              margin="normal"
              type={showPassword ? 'text' : 'password'}
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              disabled={isPending}
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <LockOutlined color="action" />
                  </InputAdornment>
                ),
                endAdornment: (
                  <InputAdornment position="end">
                    <IconButton onClick={() => setShowPassword((p) => !p)} edge="end">
                      {showPassword ? <VisibilityOff /> : <Visibility />}
                    </IconButton>
                  </InputAdornment>
                ),
              }}
              sx={{ mb: 1 }}
            />
            <Box sx={{ textAlign: 'right', mb: 3 }}>
              <Link component={RouterLink} to="/forgot-password" variant="body2" underline="hover">
                Forgot password?
              </Link>
            </Box>
            <Button
              fullWidth
              variant="contained"
              size="large"
              type="submit"
              disabled={isPending}
              sx={{
                py: 1.5,
                borderRadius: 2,
                fontWeight: 600,
                textTransform: 'none',
                boxShadow: '0 4px 14px rgba(25,118,210,0.4)',
                '&:hover': { boxShadow: '0 6px 20px rgba(25,118,210,0.6)' },
              }}
            >
              {isPending ? 'Authenticating...' : 'Sign In'}
            </Button>
          </form>
        </CardContent>
      </Card>
    </Box>
  );
}
