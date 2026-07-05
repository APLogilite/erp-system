import { createContext, ReactNode, useContext } from 'react';

import type { RuntimeContextValue } from './runtime.types';

const RuntimeContext = createContext<RuntimeContextValue | null>(null);

export function useRuntimeContext() {
  const context = useContext(RuntimeContext);
  if (!context) {
    throw new Error('useRuntimeContext must be used within a RuntimeProvider');
  }
  return context;
}

export function RuntimeProvider({
  value,
  children,
}: {
  value: RuntimeContextValue;
  children: ReactNode;
}) {
  return <RuntimeContext.Provider value={value}>{children}</RuntimeContext.Provider>;
}
