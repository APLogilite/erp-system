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
import { useQuery } from '@tanstack/react-query';
import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';

import { searchWindows, type WindowSearchResult } from '../api/runtimeApi';

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

  // Backend search — only search when dialog is open and query has at least 2 chars
  const { data: results, isLoading } = useQuery({
    queryKey: ['window-search', query],
    queryFn: () => searchWindows(query),
    enabled: open && query.length >= 2,
    staleTime: 30000,
  });

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

  const handleSelect = (w: WindowSearchResult) => {
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
          {!isLoading && results && results.length === 0 && query.length >= 2 && (
            <Typography color="text.secondary" sx={{ py: 2, textAlign: 'center' }}>
              No windows matching &quot;{query}&quot;.
            </Typography>
          )}
          {!isLoading && (!results || results.length === 0) && query.length < 2 && (
            <Typography color="text.secondary" sx={{ py: 2, textAlign: 'center' }}>
              Type at least 2 characters to search windows.
            </Typography>
          )}
          <List dense>
            {(results ?? []).slice(0, 20).map((w) => (
              <ListItemButton key={w.windowName} onClick={() => handleSelect(w)}>
                <ListItemText
                  primary={w.windowLabel}
                  secondary={`${w.tableLabel ?? w.tableName}${w.menuPath ? ' — ' + w.menuPath : ''}`}
                />
              </ListItemButton>
            ))}
          </List>
        </DialogContent>
      </Dialog>
    </>
  );
}
