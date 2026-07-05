import { ThemeMode, UiStore } from './uiTypes';

export const selectSidebarCollapsed = (state: UiStore): boolean => state.sidebarCollapsed;
export const selectCurrentTheme = (state: UiStore): ThemeMode => state.currentTheme;
export const selectActiveModule = (state: UiStore): string => state.activeModule;
export const selectIsLoading =
  (key: string) =>
  (state: UiStore): boolean =>
    !!state.loadingStates[key];
export const selectIsDialogOpen =
  (key: string) =>
  (state: UiStore): boolean =>
    !!state.dialogStates[key];
