import {
  Button,
  Checkbox,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  FormControlLabel,
  MenuItem,
  TextField,
  Typography,
} from '@mui/material';
import { useEffect, useState } from 'react';

import type { ColumnType, CreateColumnPayload, TableColumn } from '../types';
import { COLUMN_TYPE_LABELS, SNAKE_CASE_REGEX } from '../types';

interface Props {
  open: boolean;
  editColumn?: TableColumn | null;
  onClose: () => void;
  onSave: (payload: CreateColumnPayload) => Promise<void>;
}

const INITIAL: CreateColumnPayload = {
  code: '',
  label: '',
  type: 'string',
  required: false,
  maxLength: 255,
  precision: 15,
  scale: 2,
};

export function ColumnFormDialog({ open, editColumn, onClose, onSave }: Props) {
  const [payload, setPayload] = useState<CreateColumnPayload>(INITIAL);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (open) {
      if (editColumn) {
        setPayload({
          code: editColumn.code,
          label: editColumn.label,
          type: editColumn.type,
          required: editColumn.required,
          defaultValue: editColumn.defaultValue ?? '',
          maxLength: editColumn.maxLength ?? 255,
          precision: editColumn.precision ?? 15,
          scale: editColumn.scale ?? 2,
          relationTable: editColumn.relationTable ?? '',
          enumOptions: editColumn.enumOptions,
        });
      } else {
        setPayload(INITIAL);
      }
      setError(null);
    }
  }, [open, editColumn]);

  const update = (key: string, value: unknown) => {
    setPayload((prev) => ({ ...prev, [key]: value }));
  };

  const isValidCode = SNAKE_CASE_REGEX.test(payload.code);
  const canSave = isValidCode && payload.label.trim().length > 0 && payload.type.length > 0;

  const handleSubmit = async () => {
    setSaving(true);
    setError(null);
    try {
      await onSave(payload);
      onClose();
    } catch (err: unknown) {
      const msg = err instanceof Error ? err.message : 'Failed to save column';
      setError(msg);
    } finally {
      setSaving(false);
    }
  };

  const showStringFields = payload.type === 'string';
  const showDecimalFields = payload.type === 'decimal';
  const showRelationFields = payload.type === 'many2one';
  const showEnumFields = payload.type === 'enum';

  return (
    <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
      <DialogTitle>{editColumn ? `Edit Column: ${editColumn.code}` : 'Add Column'}</DialogTitle>
      <DialogContent>
        {error && (
          <Typography color="error" sx={{ mb: 2 }}>
            {error}
          </Typography>
        )}

        <TextField
          label="Code"
          value={payload.code}
          onChange={(e) => update('code', e.target.value)}
          fullWidth
          required
          margin="dense"
          disabled={!!editColumn}
          error={payload.code.length > 0 && !isValidCode}
          helperText={
            payload.code.length > 0 && !isValidCode
              ? 'Must be snake_case (e.g. order_total)'
              : 'Lowercase letters, numbers, underscores'
          }
          autoFocus
        />

        <TextField
          label="Label"
          value={payload.label}
          onChange={(e) => update('label', e.target.value)}
          fullWidth
          required
          margin="dense"
        />

        <TextField
          label="Type"
          value={payload.type}
          onChange={(e) => update('type', e.target.value as ColumnType)}
          fullWidth
          select
          margin="dense"
          disabled={!!editColumn}
        >
          {(Object.keys(COLUMN_TYPE_LABELS) as ColumnType[]).map((t) => (
            <MenuItem key={t} value={t}>
              {COLUMN_TYPE_LABELS[t]}
            </MenuItem>
          ))}
        </TextField>

        {showStringFields && (
          <TextField
            label="Max Length"
            type="number"
            value={payload.maxLength ?? 255}
            onChange={(e) => update('maxLength', parseInt(e.target.value, 10) || 255)}
            fullWidth
            margin="dense"
          />
        )}

        {showDecimalFields && (
          <>
            <TextField
              label="Precision"
              type="number"
              value={payload.precision ?? 15}
              onChange={(e) => update('precision', parseInt(e.target.value, 10) || 15)}
              fullWidth
              margin="dense"
            />
            <TextField
              label="Scale"
              type="number"
              value={payload.scale ?? 2}
              onChange={(e) => update('scale', parseInt(e.target.value, 10) || 2)}
              fullWidth
              margin="dense"
            />
          </>
        )}

        {showRelationFields && (
          <TextField
            label="Related Table Code"
            value={payload.relationTable ?? ''}
            onChange={(e) => update('relationTable', e.target.value)}
            fullWidth
            required
            margin="dense"
            helperText="The code of the table this column references"
          />
        )}

        {showEnumFields && (
          <TextField
            label="Enum Options (JSON)"
            value={payload.enumOptions ? JSON.stringify(payload.enumOptions, null, 2) : '{}'}
            onChange={(e) => {
              try {
                const parsed = JSON.parse(e.target.value);
                update('enumOptions', parsed);
              } catch {
                // Let user keep typing
              }
            }}
            fullWidth
            margin="dense"
            multiline
            rows={3}
            helperText='Key-value pairs, e.g. {"draft": "Draft", "active": "Active"}'
          />
        )}

        <FormControlLabel
          control={
            <Checkbox
              checked={payload.required ?? false}
              onChange={(e) => update('required', e.target.checked)}
            />
          }
          label="Required"
          sx={{ mt: 1 }}
        />

        <TextField
          label="Default Value"
          value={payload.defaultValue ?? ''}
          onChange={(e) => update('defaultValue', e.target.value)}
          fullWidth
          margin="dense"
        />
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose}>Cancel</Button>
        <Button variant="contained" onClick={handleSubmit} disabled={!canSave || saving}>
          {saving ? 'Saving...' : editColumn ? 'Update' : 'Add'}
        </Button>
      </DialogActions>
    </Dialog>
  );
}
