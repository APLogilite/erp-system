export type ThemeMode = 'light' | 'dark';

export interface UiState {
  sidebarCollapsed: boolean;
  currentTheme: ThemeMode;
  activeModule: string;
  loadingStates: Record<string, boolean>;
  dialogStates: Record<string, boolean>;
}

export interface UiActions {
  toggleSidebar: () => void;
  setSidebarCollapsed: (collapsed: boolean) => void;
  toggleTheme: () => void;
  setTheme: (theme: ThemeMode) => void;
  setActiveModule: (moduleName: string) => void;
  setLoading: (key: string, isLoading: boolean) => void;
  setDialogOpen: (key: string, isOpen: boolean) => void;
}

export type UiStore = UiState & UiActions;
