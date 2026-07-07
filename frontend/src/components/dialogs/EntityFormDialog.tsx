import {
  Alert,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  MenuItem,
  TextField,
} from '@mui/material';
import { useState, useEffect } from 'react';

export interface FieldDef {
  name: string;
  label: string;
  type?: 'text' | 'email' | 'password' | 'select';
  options?: { value: string; label: string }[];
  required?: boolean;
  initialValue?: string;
  allowNone?: boolean;
}

interface Props {
  open: boolean;
  title: string;
  fields: FieldDef[];
  data: Record<string, string> | null;
  onClose: () => void;
  onSave: (values: Record<string, string>) => Promise<void>;
}

export function EntityFormDialog({ open, title, fields, data, onClose, onSave }: Props) {
  const isEdit = !!data;
  const [form, setForm] = useState<Record<string, string>>({});
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (open) {
      if (data) {
        const initial: Record<string, string> = {};
        for (const f of fields) {
          initial[f.name] = data[f.name] ?? f.initialValue ?? '';
        }
        setForm(initial);
      } else {
        const initial: Record<string, string> = {};
        for (const f of fields) {
          initial[f.name] = f.initialValue ?? '';
        }
        setForm(initial);
      }
      setError(null);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [open, data]);

  const handleChange = (name: string) => (e: React.ChangeEvent<HTMLInputElement>) => {
    setForm((prev) => ({ ...prev, [name]: e.target.value }));
  };

  const handleSubmit = async () => {
    setSaving(true);
    setError(null);
    try {
      await onSave(form);
      onClose();
    } catch (err: unknown) {
      const msg = err instanceof Error ? err.message : 'Failed to save';
      setError(msg);
    } finally {
      setSaving(false);
    }
  };

  return (
    <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
      <DialogTitle>{title}</DialogTitle>
      <DialogContent>
        {error && (
          <Alert severity="error" sx={{ mb: 2, mt: 1 }}>
            {error}
          </Alert>
        )}
        {fields.map((f) => (
          <TextField
            key={f.name}
            label={f.label}
            type={f.type === 'password' ? 'password' : 'text'}
            value={form[f.name] ?? ''}
            onChange={handleChange(f.name)}
            fullWidth
            required={f.required}
            margin="normal"
            select={f.type === 'select'}
          >
            {f.type === 'select' && (
              <>
                {f.allowNone && (
                  <MenuItem value="">
                    <em>None</em>
                  </MenuItem>
                )}
                {f.options?.map((o) => (
                  <MenuItem key={o.value} value={o.value}>
                    {o.label}
                  </MenuItem>
                ))}
              </>
            )}
          </TextField>
        ))}
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose}>Cancel</Button>
        <Button variant="contained" onClick={handleSubmit} disabled={saving}>
          {saving ? 'Saving...' : isEdit ? 'Update' : 'Create'}
        </Button>
      </DialogActions>
    </Dialog>
  );
}
