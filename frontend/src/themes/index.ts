import { Theme } from '@mui/material/styles';

import { darkTheme } from './darkTheme';
import { lightTheme } from './lightTheme';

export type ThemeMode = 'light' | 'dark';

export const createAppTheme = (mode: ThemeMode): Theme => {
  return mode === 'dark' ? darkTheme : lightTheme;
};

export { lightTheme, darkTheme };
