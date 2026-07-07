import { CircularProgress, Box } from '@mui/material';
import { ReactNode, useEffect, useState } from 'react';
import { Navigate } from 'react-router-dom';

import { selectIsAuthenticated } from '@/core/auth/authSelectors';
import { useAuthStore } from '@/core/auth/authStore';

type GuestRouteProps = {
  children?: ReactNode;
};

export function GuestRoute({ children }: GuestRouteProps) {
  const isAuthenticated = useAuthStore(selectIsAuthenticated);
  const [hydrated, setHydrated] = useState(() => useAuthStore.persist.hasHydrated());

  useEffect(() => {
    if (hydrated) return;
    const unsub = useAuthStore.persist.onFinishHydration(() => setHydrated(true));
    return unsub;
  }, [hydrated]);

  if (!hydrated) {
    return (
      <Box
        sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', minHeight: '100vh' }}
      >
        <CircularProgress />
      </Box>
    );
  }

  if (isAuthenticated) {
    return <Navigate to="/select-context" replace />;
  }

  return children ? <>{children}</> : null;
}
