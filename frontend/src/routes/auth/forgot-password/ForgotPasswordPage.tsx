import { LockReset, MailOutline } from '@mui/icons-material';
import {
  Box,
  Card,
  CardContent,
  TextField,
  Button,
  Typography,
  useTheme,
  Alert,
  Link,
} from '@mui/material';
import { useState } from 'react';
import { Link as RouterLink } from 'react-router-dom';

import { apiClient } from '@/core/api/client';

export function ForgotPasswordPage() {
  const theme = useTheme();
  const [email, setEmail] = useState('');
  const [sent, setSent] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setLoading(true);
    try {
      await apiClient.post('/auth/forgot-password', { email });
      setSent(true);
    } catch {
      setError('Failed to send reset email. Please try again.');
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
      <Card sx={{ width: '100%', maxWidth: 450, borderRadius: 4, overflow: 'hidden' }}>
        <Box sx={{ height: 6, background: 'linear-gradient(90deg, #1976d2 0%, #82b1ff 100%)' }} />
        <CardContent sx={{ p: 4, textAlign: 'center' }}>
          <Box
            sx={{
              width: 56,
              height: 56,
              borderRadius: '50%',
              backgroundColor: theme.palette.primary.main + '15',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              mx: 'auto',
              mb: 2,
            }}
          >
            <LockReset color="primary" sx={{ fontSize: 28 }} />
          </Box>
          <Typography variant="h5" fontWeight={700} gutterBottom>
            Forgot Password
          </Typography>

          {sent ? (
            <>
              <Alert severity="success" sx={{ mb: 3, borderRadius: 2 }}>
                Reset link sent! Check your email inbox.
              </Alert>
              <Button component={RouterLink} to="/login" variant="contained">
                Back to Login
              </Button>
            </>
          ) : (
            <>
              <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
                Enter your email address and we&apos;ll send you a password reset link.
              </Typography>
              {error && (
                <Alert severity="error" sx={{ mb: 2, borderRadius: 2 }}>
                  {error}
                </Alert>
              )}
              <form onSubmit={handleSubmit}>
                <TextField
                  fullWidth
                  label="Email"
                  type="email"
                  variant="outlined"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  disabled={loading}
                  InputProps={{ startAdornment: <MailOutline color="action" sx={{ mr: 1 }} /> }}
                  sx={{ mb: 3 }}
                />
                <Button
                  fullWidth
                  variant="contained"
                  size="large"
                  type="submit"
                  disabled={loading}
                  sx={{ py: 1.5, borderRadius: 2, textTransform: 'none' }}
                >
                  {loading ? 'Sending...' : 'Send Reset Link'}
                </Button>
              </form>
              <Box sx={{ mt: 2 }}>
                <Link component={RouterLink} to="/login" variant="body2">
                  Back to Login
                </Link>
              </Box>
            </>
          )}
        </CardContent>
      </Card>
    </Box>
  );
}
