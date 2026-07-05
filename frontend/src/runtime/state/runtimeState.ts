import { create } from 'zustand';

import type { RuntimeMode } from '@/runtime/context/runtime.types';

interface RuntimeState {
  currentViewCode: string | null;
  currentRecord?: Record<string, unknown>;
  mode: RuntimeMode;
  loading: boolean;
  permissions: string[];
  setCurrentViewCode: (viewCode: string) => void;
  setCurrentRecord: (record?: Record<string, unknown>) => void;
  setMode: (mode: RuntimeMode) => void;
  setLoading: (loading: boolean) => void;
  setPermissions: (permissions: string[]) => void;
}

export const useRuntimeState = create<RuntimeState>((set) => ({
  currentViewCode: null,
  currentRecord: undefined,
  mode: 'VIEW',
  loading: false,
  permissions: [],
  setCurrentViewCode: (viewCode) => set(() => ({ currentViewCode: viewCode })),
  setCurrentRecord: (record) => set(() => ({ currentRecord: record })),
  setMode: (mode) => set(() => ({ mode })),
  setLoading: (loading) => set(() => ({ loading })),
  setPermissions: (permissions) => set(() => ({ permissions })),
}));
