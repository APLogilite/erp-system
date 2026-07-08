import { useEffect } from 'react';

export interface KeyboardShortcut {
  key: string;
  ctrlKey?: boolean;
  shiftKey?: boolean;
  altKey?: boolean;
  action: () => void;
  enabled?: () => boolean;
}

/**
 * Registers global keyboard shortcuts. Handles preventDefault
 * for Ctrl+S and F5 to avoid browser save/refresh.
 */
export function useKeyboardShortcuts(shortcuts: KeyboardShortcut[]) {
  useEffect(() => {
    const handler = (e: KeyboardEvent) => {
      for (const s of shortcuts) {
        const keyMatch = e.key.toLowerCase() === s.key.toLowerCase();
        const ctrlMatch = s.ctrlKey ? e.ctrlKey || e.metaKey : !e.ctrlKey && !e.metaKey;
        const shiftMatch = s.shiftKey ? e.shiftKey : !e.shiftKey;
        const altMatch = s.altKey ? e.altKey : !e.altKey;

        if (keyMatch && ctrlMatch && shiftMatch && altMatch) {
          if (s.enabled && !s.enabled()) continue;

          // Prevent browser defaults for common shortcuts
          if (
            (s.key === 's' && (e.ctrlKey || e.metaKey)) ||
            s.key === 'F5'
          ) {
            e.preventDefault();
          }

          s.action();
          return;
        }
      }
    };

    window.addEventListener('keydown', handler);
    return () => window.removeEventListener('keydown', handler);
  }, [shortcuts]);
}
