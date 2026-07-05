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
} from '@mui/material';
import { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';

import { useAuthStore } from '@/core/auth/authStore';

export function LoginPage() {
  const theme = useTheme();
  const navigate = useNavigate();
  const location = useLocation();
  const loginAction = useAuthStore((state) => state.login);

  const [username, setUsername] = useState('admin');
  const [password, setPassword] = useState('password');
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const state = location.state as { from?: { pathname?: string } } | null;
  const from = state?.from?.pathname || '/app/dashboard';

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setLoading(true);

    try {
      // Perform mock validation (successful by default for admin/password)
      if (!username || !password) {
        setError('Please enter both username and password.');
        setLoading(false);
        return;
      }

      // Simulate network request
      await new Promise((resolve) => setTimeout(resolve, 800));

      const mockUser = {
        id: 'usr_1',
        email: `${username}@example.com`,
        username: username,
        roles: ['admin', 'manager'],
        permissions: ['read:all', 'write:all', 'delete:all'],
      };

      loginAction(mockUser, 'mock_access_token_123', 'mock_refresh_token_456');
      navigate(from, { replace: true });
    } catch (err: unknown) {
      const message =
        err instanceof Error ? err.message : 'An error occurred during authentication.';
      setError(message);
    } finally {
      setLoading(false);
    }
  };

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
              ? '0 12px 40px rgba(0, 0, 0, 0.5)'
              : '0 12px 40px rgba(148, 163, 184, 0.15)',
          border: `1px solid ${
            theme.palette.mode === 'dark' ? 'rgba(255,255,255,0.05)' : 'rgba(0,0,0,0.05)'
          }`,
          overflow: 'hidden',
        }}
      >
        <Box
          sx={{
            height: 6,
            background: 'linear-gradient(90deg, #1976d2 0%, #82b1ff 100%)',
          }}
        />
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
              {error}
            </Alert>
          )}

          <form onSubmit={handleLogin}>
            <TextField
              fullWidth
              label="Username"
              variant="outlined"
              margin="normal"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              disabled={loading}
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
              disabled={loading}
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <LockOutlined color="action" />
                  </InputAdornment>
                ),
                endAdornment: (
                  <InputAdornment position="end">
                    <IconButton
                      aria-label="toggle password visibility"
                      onClick={() => setShowPassword((prev) => !prev)}
                      edge="end"
                    >
                      {showPassword ? <VisibilityOff /> : <Visibility />}
                    </IconButton>
                  </InputAdornment>
                ),
              }}
              sx={{ mb: 3 }}
            />

            <Button
              fullWidth
              variant="contained"
              size="large"
              type="submit"
              disabled={loading}
              sx={{
                py: 1.5,
                borderRadius: 2,
                fontWeight: 600,
                textTransform: 'none',
                boxShadow: '0 4px 14px rgba(25, 118, 210, 0.4)',
                '&:hover': {
                  boxShadow: '0 6px 20px rgba(25, 118, 210, 0.6)',
                },
              }}
            >
              {loading ? 'Authenticating...' : 'Sign In'}
            </Button>
          </form>

          <Box sx={{ mt: 3, textAlign: 'center' }}>
            <Typography variant="caption" color="text.secondary">
              Forgot password? Please contact your system administrator.
            </Typography>
          </Box>
        </CardContent>
      </Card>
    </Box>
  );
}
