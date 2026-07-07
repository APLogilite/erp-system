import {
  Alert,
  Button,
  Checkbox,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  FormControl,
  FormControlLabel,
  FormHelperText,
  MenuItem,
  TextField,
} from '@mui/material';
import { useState, useEffect } from 'react';

export interface FieldDef {
  name: string;
  label: string;
  type?:
    | 'text'
    | 'email'
    | 'password'
    | 'select'
    | 'date'
    | 'datetime'
    | 'time'
    | 'number'
    | 'url'
    | 'tel'
    | 'textarea'
    | 'checkbox';
  options?: { value: string; label: string }[];
  required?: boolean;
  initialValue?: string;
  allowNone?: boolean;
  rows?: number;
  placeholder?: string;
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
  }, [open, data, fields]);

  const handleChange = (name: string) => (e: React.ChangeEvent<HTMLInputElement>) => {
    setForm((prev) => ({ ...prev, [name]: e.target.value }));
  };

  const handleCheckbox = (name: string) => (e: React.ChangeEvent<HTMLInputElement>) => {
    setForm((prev) => ({ ...prev, [name]: e.target.checked ? 'true' : 'false' }));
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

  const renderField = (f: FieldDef) => {
    if (f.type === 'checkbox') {
      return (
        <FormControl key={f.name} fullWidth margin="normal">
          <FormControlLabel
            control={
              <Checkbox checked={form[f.name] === 'true'} onChange={handleCheckbox(f.name)} />
            }
            label={f.label}
          />
          {f.required && <FormHelperText>Required</FormHelperText>}
        </FormControl>
      );
    }

    if (f.type === 'date' || f.type === 'datetime' || f.type === 'time') {
      const htmlType = f.type === 'datetime' ? 'datetime-local' : f.type;
      return (
        <TextField
          key={f.name}
          label={f.label}
          type={htmlType}
          value={form[f.name] ?? ''}
          onChange={handleChange(f.name)}
          fullWidth
          required={f.required}
          margin="normal"
          InputLabelProps={{ shrink: true }}
        />
      );
    }

    if (f.type === 'textarea') {
      return (
        <TextField
          key={f.name}
          label={f.label}
          value={form[f.name] ?? ''}
          onChange={handleChange(f.name)}
          fullWidth
          required={f.required}
          margin="normal"
          multiline
          rows={f.rows ?? 3}
          placeholder={f.placeholder}
        />
      );
    }

    const htmlType =
      f.type === 'password'
        ? 'password'
        : f.type === 'number'
          ? 'number'
          : f.type === 'url'
            ? 'url'
            : f.type === 'tel'
              ? 'tel'
              : f.type === 'email'
                ? 'email'
                : 'text';

    return (
      <TextField
        key={f.name}
        label={f.label}
        type={htmlType}
        value={form[f.name] ?? ''}
        onChange={handleChange(f.name)}
        fullWidth
        required={f.required}
        margin="normal"
        select={f.type === 'select'}
        placeholder={f.placeholder}
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
    );
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
        {fields.map(renderField)}
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
