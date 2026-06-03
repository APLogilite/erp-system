import { useUiStore } from './uiStore';
import { ThemeMode } from './uiTypes';

export const uiActions = {
  toggleSidebar: () => useUiStore.getState().toggleSidebar(),
  setSidebarCollapsed: (collapsed: boolean) => useUiStore.getState().setSidebarCollapsed(collapsed),
  toggleTheme: () => useUiStore.getState().toggleTheme(),
  setTheme: (theme: ThemeMode) => useUiStore.getState().setTheme(theme),
  setActiveModule: (moduleName: string) => useUiStore.getState().setActiveModule(moduleName),
  setLoading: (key: string, isLoading: boolean) => useUiStore.getState().setLoading(key, isLoading),
  setDialogOpen: (key: string, isOpen: boolean) => useUiStore.getState().setDialogOpen(key, isOpen),
};
