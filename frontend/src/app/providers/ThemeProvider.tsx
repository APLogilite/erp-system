import { CssBaseline } from '@mui/material';
import { ThemeProvider as MuiThemeProvider } from '@mui/material/styles';
import { ReactNode, createContext, useContext } from 'react';

import { selectCurrentTheme } from '@/core/store/ui/uiSelectors';
import { useUiStore } from '@/core/store/ui/uiStore';
import { createAppTheme, ThemeMode } from '@/themes';

type ThemeContextType = {
  mode: ThemeMode;
  toggleTheme: () => void;
};

const ThemeContext = createContext<ThemeContextType | undefined>(undefined);

export function useTheme() {
  const context = useContext(ThemeContext);
  if (!context) {
    throw new Error('useTheme must be used within a ThemeProvider');
  }
  return context;
}

type ThemeProviderProps = {
  children: ReactNode;
};

export function ThemeProvider({ children }: ThemeProviderProps) {
  const mode = useUiStore(selectCurrentTheme);
  const toggleTheme = useUiStore((state) => state.toggleTheme);

  const theme = createAppTheme(mode);

  const contextValue: ThemeContextType = {
    mode,
    toggleTheme,
  };

  return (
    <ThemeContext.Provider value={contextValue}>
      <MuiThemeProvider theme={theme}>
        <CssBaseline />
        {children}
      </MuiThemeProvider>
    </ThemeContext.Provider>
  );
}
