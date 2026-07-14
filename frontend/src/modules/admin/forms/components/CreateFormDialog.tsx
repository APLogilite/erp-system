import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  FormControl,
  FormControlLabel,
  InputLabel,
  MenuItem,
  Radio,
  RadioGroup,
  Select,
  TextField,
  Typography,
} from '@mui/material';
import { useState } from 'react';

import { useAvailableTables, useCreateForm } from '../hooks/useFormDesigner';
import type { AvailableTable, FormScope } from '../types';

interface Props {
  open: boolean;
  onClose: () => void;
  onCreated: (id: string) => void;
}

export function CreateFormDialog({ open, onClose, onCreated }: Props) {
  const [scope, setScope] = useState<FormScope>('global');
  const [tableCode, setTableCode] = useState('');
  const [code, setCode] = useState('');
  const [label, setLabel] = useState('');
  const [description, setDescription] = useState('');
  const [error, setError] = useState<string | null>(null);

  const { data: tables, isLoading: tablesLoading } = useAvailableTables();
  const createMutation = useCreateForm();

  const handleCreate = async () => {
    setError(null);
    try {
      const result = await createMutation.mutateAsync({
        code,
        label,
        description: description || undefined,
        modelName: tableCode,
        scope,
      });
      onCreated(result.id);
      onClose();
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'Failed to create form');
    }
  };

  const canCreate = code.trim() && label.trim() && tableCode && scope;

  return (
    <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
      <DialogTitle>Create Form</DialogTitle>
      <DialogContent>
        {error && (
          <Typography color="error" sx={{ mb: 2 }}>
            {error}
          </Typography>
        )}

        <FormControl component="fieldset" sx={{ mb: 2 }}>
          <Typography variant="body2" sx={{ mb: 1, fontWeight: 500 }}>
            Scope
          </Typography>
          <RadioGroup row value={scope} onChange={(e) => setScope(e.target.value as FormScope)}>
            <FormControlLabel value="global" control={<Radio />} label="Global" />
            <FormControlLabel value="tenant" control={<Radio />} label="Tenant" />
          </RadioGroup>
        </FormControl>

        <FormControl fullWidth sx={{ mb: 2 }}>
          <InputLabel>Table</InputLabel>
          <Select
            value={tableCode}
            label="Table"
            onChange={(e) => setTableCode(e.target.value)}
            disabled={tablesLoading}
          >
            {tables?.map((t: AvailableTable) => (
              <MenuItem key={t.tableCode} value={t.tableCode}>
                {t.tableLabel} ({t.tableCode}) — {t.columnCount} columns
              </MenuItem>
            ))}
          </Select>
        </FormControl>

        <TextField
          label="Code"
          value={code}
          onChange={(e) => setCode(e.target.value)}
          fullWidth
          required
          margin="dense"
        />
        <TextField
          label="Label"
          value={label}
          onChange={(e) => setLabel(e.target.value)}
          fullWidth
          required
          margin="dense"
        />
        <TextField
          label="Description"
          value={description}
          onChange={(e) => setDescription(e.target.value)}
          fullWidth
          multiline
          rows={2}
          margin="dense"
        />
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose}>Cancel</Button>
        <Button
          variant="contained"
          onClick={handleCreate}
          disabled={!canCreate || createMutation.isPending}
        >
          {createMutation.isPending ? 'Creating...' : 'Create'}
        </Button>
      </DialogActions>
    </Dialog>
  );
}
