import { create } from 'zustand';
import { persist } from 'zustand/middleware';

import { UiStore } from './uiTypes';

export const useUiStore = create<UiStore>()(
  persist(
    (set) => ({
      sidebarCollapsed: false,
      currentTheme: 'light',
      activeModule: 'dashboard',
      loadingStates: {},
      dialogStates: {},

      toggleSidebar: () => set((state) => ({ sidebarCollapsed: !state.sidebarCollapsed })),

      setSidebarCollapsed: (collapsed) => set({ sidebarCollapsed: collapsed }),

      toggleTheme: () =>
        set((state) => ({ currentTheme: state.currentTheme === 'light' ? 'dark' : 'light' })),

      setTheme: (theme) => set({ currentTheme: theme }),

      setActiveModule: (activeModule) => set({ activeModule }),

      setLoading: (key, isLoading) =>
        set((state) => ({
          loadingStates: { ...state.loadingStates, [key]: isLoading },
        })),

      setDialogOpen: (key, isOpen) =>
        set((state) => ({
          dialogStates: { ...state.dialogStates, [key]: isOpen },
        })),
    }),
    {
      name: 'erp-ui-state',
      partialize: (state) => ({
        sidebarCollapsed: state.sidebarCollapsed,
        currentTheme: state.currentTheme,
      }),
    }
  )
);
