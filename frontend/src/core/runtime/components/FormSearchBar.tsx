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
  const { data: forms, isLoading } = useAccessibleForms();
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

  const filtered = (forms ?? []).filter(
    (f: AccessibleForm) =>
      !query ||
      f.formLabel.toLowerCase().includes(query.toLowerCase()) ||
      f.formCode.toLowerCase().includes(query.toLowerCase()) ||
      (f.modelLabel ?? f.modelName).toLowerCase().includes(query.toLowerCase())
  );

  const handleSelect = (f: AccessibleForm) => {
    setOpen(false);
    setQuery('');
    navigate(`/app/runtime?form=${encodeURIComponent(f.formCode)}`);
  };

  return (
    <>
      {/* Visible trigger button in the header */}
      <Tooltip title="Search forms (Ctrl+K)">
        <IconButton aria-label="Search forms" onClick={() => setOpen(true)} {...ButtonProps}>
          <Search />
        </IconButton>
      </Tooltip>

      {/* Search dialog */}
      <Dialog open={open} onClose={() => setOpen(false)} maxWidth="sm" fullWidth>
        <DialogTitle sx={{ pb: 0 }}>
          <TextField
            autoFocus
            fullWidth
            placeholder="Search forms..."
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
              No forms matching &quot;{query}&quot;.
            </Typography>
          )}
          {!isLoading && filtered.length === 0 && !query && (
            <Typography color="text.secondary" sx={{ py: 2, textAlign: 'center' }}>
              {forms === undefined || forms.length === 0
                ? 'No accessible forms. Contact your system administrator.'
                : 'No forms found.'}
            </Typography>
          )}
          <List dense>
            {filtered.slice(0, 15).map((f) => (
              <ListItemButton key={f.formCode} onClick={() => handleSelect(f)}>
                <ListItemText
                  primary={f.formLabel}
                  secondary={`${f.modelLabel ?? f.modelName} — ${f.formCode}`}
                />
              </ListItemButton>
            ))}
          </List>
        </DialogContent>
      </Dialog>
    </>
  );
}
