import { Search } from '@mui/icons-material';
import {
  CircularProgress,
  Dialog,
  DialogContent,
  DialogTitle,
  IconButton,
  IconButtonProps,
  InputAdornment,
  List,
  ListItemButton,
  ListItemText,
  TextField,
  Tooltip,
  Typography,
} from '@mui/material';
import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';

import { useAccessibleForms } from '../hooks/useAccessibleForms';
import type { AccessibleForm } from '../hooks/useAccessibleForms';

type FormSearchBarProps = {
  /**
   * Override for the trigger IconButton's color/size props.
   * Defaults to standard header icon button styling.
   */
  ButtonProps?: Partial<IconButtonProps>;
};

export function FormSearchBar({ ButtonProps }: FormSearchBarProps) {
  const [open, setOpen] = useState(false);
  const [query, setQuery] = useState('');
  const { data: windows, isLoading } = useAccessibleForms();
  const navigate = useNavigate();

  // Ctrl+K / Cmd+K to open
  useEffect(() => {
    const handler = (e: KeyboardEvent) => {
      if ((e.ctrlKey || e.metaKey) && e.key === 'k') {
        e.preventDefault();
        setOpen((prev) => !prev);
      }
    };
    window.addEventListener('keydown', handler);
    return () => window.removeEventListener('keydown', handler);
  }, []);

  const filtered = (windows ?? []).filter(
    (w: AccessibleForm) =>
      !query ||
      w.windowLabel.toLowerCase().includes(query.toLowerCase()) ||
      w.windowName.toLowerCase().includes(query.toLowerCase()) ||
      (w.tableLabel ?? w.tableName).toLowerCase().includes(query.toLowerCase())
  );

  const handleSelect = (w: AccessibleForm) => {
    setOpen(false);
    setQuery('');
    navigate(`/app/window/${encodeURIComponent(w.windowName)}`);
  };

  return (
    <>
      {/* Visible trigger button in the header */}
      <Tooltip title="Search windows (Ctrl+K)">
        <IconButton aria-label="Search windows" onClick={() => setOpen(true)} {...ButtonProps}>
          <Search />
        </IconButton>
      </Tooltip>

      {/* Search dialog */}
      <Dialog open={open} onClose={() => setOpen(false)} maxWidth="sm" fullWidth>
        <DialogTitle sx={{ pb: 0 }}>
          <TextField
            autoFocus
            fullWidth
            placeholder="Search windows..."
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            variant="standard"
            InputProps={{
              startAdornment: (
                <InputAdornment position="start">
                  <Search />
                </InputAdornment>
              ),
            }}
          />
        </DialogTitle>
        <DialogContent sx={{ pt: 1 }}>
          {isLoading && <CircularProgress sx={{ display: 'block', mx: 'auto', my: 2 }} />}
          {!isLoading && filtered.length === 0 && query && (
            <Typography color="text.secondary" sx={{ py: 2, textAlign: 'center' }}>
              No windows matching &quot;{query}&quot;.
            </Typography>
          )}
          {!isLoading && filtered.length === 0 && !query && (
            <Typography color="text.secondary" sx={{ py: 2, textAlign: 'center' }}>
              {windows === undefined || windows.length === 0
                ? 'No accessible windows. Contact your system administrator.'
                : 'No windows found.'}
            </Typography>
          )}
          <List dense>
            {filtered.slice(0, 15).map((w) => (
              <ListItemButton key={w.windowName} onClick={() => handleSelect(w)}>
                <ListItemText
                  primary={w.windowLabel}
                  secondary={`${w.tableLabel ?? w.tableName} — ${w.windowName}`}
                />
              </ListItemButton>
            ))}
          </List>
        </DialogContent>
      </Dialog>
    </>
  );
}
