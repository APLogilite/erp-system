import { useMemo } from 'react';

import { useRuntimeContext } from '@/runtime/context/RuntimeContext';

export function useRenderer() {
  const runtime = useRuntimeContext();
  return useMemo(
    () => ({
      currentView: runtime.currentView,
      currentLayout: runtime.currentLayout,
      mode: runtime.mode,
      permissions: runtime.permissions,
    }),
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [runtime.currentView, runtime.currentLayout, runtime.mode, JSON.stringify(runtime.permissions)]
  );
}
