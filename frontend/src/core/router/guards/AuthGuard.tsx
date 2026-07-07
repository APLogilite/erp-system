import { CircularProgress, Box } from '@mui/material';
import { ReactNode, useEffect, useState } from 'react';
import { Navigate } from 'react-router-dom';

import { useAuthStore } from '@/core/auth/authStore';

export function AuthGuard({ children }: { children: ReactNode }) {
  const isAuthenticated = useAuthStore((s) => s.isAuthenticated);
  const [hydrated, setHydrated] = useState(() => {
    const h = useAuthStore.persist.hasHydrated();
    console.log(
      '[AuthGuard] initial hydrated:',
      h,
      'isAuthenticated:',
      useAuthStore.getState().isAuthenticated
    );
    return h;
  });

  useEffect(() => {
    if (hydrated) {
      console.log('[AuthGuard] already hydrated, isAuthenticated:', isAuthenticated);
      return;
    }
    console.log('[AuthGuard] waiting for hydration...');
    const unsub = useAuthStore.persist.onFinishHydration(() => {
      console.log(
        '[AuthGuard] hydration finished, isAuthenticated:',
        useAuthStore.getState().isAuthenticated
      );
      setHydrated(true);
    });
    const timeout = setTimeout(() => {
      console.log('[AuthGuard] hydration timeout fallback');
      setHydrated(true);
    }, 2000);
    return () => {
      unsub();
      clearTimeout(timeout);
    };
  }, [hydrated, isAuthenticated]);

  if (!hydrated) {
    return (
      <Box
        sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', minHeight: '100vh' }}
      >
        <CircularProgress />
      </Box>
    );
  }

  if (!isAuthenticated) {
    console.log('[AuthGuard] NOT authenticated, redirecting to login');
    return <Navigate to="/login" replace />;
  }
  console.log('[AuthGuard] authenticated, rendering children');
  return <>{children}</>;
}
