import { ArrowBack } from '@mui/icons-material';
import { Box, Button, Card, CardContent, Divider, Typography } from '@mui/material';
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';

import { ColumnFormDialog } from './components/ColumnFormDialog';
import { useCreateTable } from './hooks/useTables';
import type { CreateColumnPayload, CreateTablePayload } from './types';
import { SNAKE_CASE_REGEX } from './types';

import { notifyActions } from '@/core/store/notifications/notificationStore';

export function CreateTablePage() {
  const navigate = useNavigate();
  const createMutation = useCreateTable();

  const [code, setCode] = useState('');
  const [label, setLabel] = useState('');
  const [pluralLabel, setPluralLabel] = useState('');
  const [description, setDescription] = useState('');
  const [columns, setColumns] = useState<CreateColumnPayload[]>([]);

  const [colDialogOpen, setColDialogOpen] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const isValidCode = SNAKE_CASE_REGEX.test(code);
  const canSave = isValidCode && label.trim() && pluralLabel.trim() && code.trim();

  const handleCreate = async () => {
    setSaving(true);
    setError(null);
    try {
      const tableName = `t_${code}`;
      const payload: CreateTablePayload = {
        code,
        label,
        pluralLabel,
        description: description || undefined,
        tableName,
        columns: columns.length > 0 ? columns : undefined,
      };
      const result = await createMutation.mutateAsync(payload);
      notifyActions.success(`Table "${label}" created successfully.`);
      navigate(`/app/admin/tables/${result.id}`);
    } catch (err: unknown) {
      const msg = err instanceof Error ? err.message : 'Failed to create table';
      setError(msg);
    } finally {
      setSaving(false);
    }
  };

  const handleAddColumn = (col: CreateColumnPayload) => {
    setColumns((prev) => [...prev, col]);
    return Promise.resolve();
  };

  const labelField = (fieldName: string, value: string, onChange: (v: string) => void) => (
    <Box sx={{ mb: 2 }}>
      <Typography variant="body2" sx={{ mb: 0.5, fontWeight: 500 }}>
        {fieldName}
      </Typography>
      <input
        type="text"
        value={value}
        onChange={(e) => onChange(e.target.value)}
        style={{
          width: '100%',
          padding: '8px 12px',
          border: '1px solid #ccc',
          borderRadius: 6,
          fontSize: 14,
        }}
      />
    </Box>
  );

  return (
    <Box sx={{ p: 3 }}>
      <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 3 }}>
        <Button
          startIcon={<ArrowBack />}
          onClick={() => navigate('/app/admin/tables')}
          sx={{ textTransform: 'none' }}
        >
          Back
        </Button>
        <Typography variant="h5" fontWeight={700}>
          Create Table
        </Typography>
      </Box>

      <Card sx={{ borderRadius: 3, maxWidth: 640 }}>
        <CardContent>
          {error && (
            <Typography color="error" sx={{ mb: 2 }}>
              {error}
            </Typography>
          )}

          {labelField('Code (snake_case)', code, setCode)}
          {code.length > 0 && !isValidCode && (
            <Typography color="error" variant="caption">
              Must be snake_case: lowercase letters, numbers, and underscores only (e.g.
              expense_report).
            </Typography>
          )}

          {labelField('Label (singular)', label, setLabel)}
          {labelField('Label (plural)', pluralLabel, setPluralLabel)}

          <Box sx={{ mb: 2 }}>
            <Typography variant="body2" sx={{ mb: 0.5, fontWeight: 500 }}>
              Description
            </Typography>
            <textarea
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              rows={3}
              style={{
                width: '100%',
                padding: '8px 12px',
                border: '1px solid #ccc',
                borderRadius: 6,
                fontSize: 14,
                fontFamily: 'inherit',
                resize: 'vertical',
              }}
            />
          </Box>

          {code && (
            <Box sx={{ mb: 2 }}>
              <Typography variant="caption" color="text.secondary">
                Physical table name: <code>{`t_${code}`}</code>
              </Typography>
            </Box>
          )}

          <Divider sx={{ my: 2 }} />

          <Typography variant="subtitle1" fontWeight={600} sx={{ mb: 1 }}>
            Columns ({columns.length})
          </Typography>
          {columns.length > 0 && (
            <Box sx={{ mb: 2 }}>
              {columns.map((col, idx) => (
                <Box key={idx} sx={{ display: 'flex', gap: 1, mb: 0.5, alignItems: 'center' }}>
                  <code>{col.code}</code>
                  <Typography variant="body2">{col.label}</Typography>
                  <Typography variant="caption" color="text.secondary">
                    ({col.type})
                  </Typography>
                </Box>
              ))}
            </Box>
          )}
          <Button variant="outlined" size="small" onClick={() => setColDialogOpen(true)}>
            + Add Column
          </Button>

          <Divider sx={{ my: 3 }} />

          <Box sx={{ display: 'flex', gap: 2 }}>
            <Button onClick={() => navigate('/app/admin/tables')} sx={{ textTransform: 'none' }}>
              Cancel
            </Button>
            <Button
              variant="contained"
              onClick={handleCreate}
              disabled={!canSave || saving}
              sx={{ textTransform: 'none' }}
            >
              {saving ? 'Creating...' : 'Create Table'}
            </Button>
          </Box>
        </CardContent>
      </Card>

      <ColumnFormDialog
        open={colDialogOpen}
        onClose={() => setColDialogOpen(false)}
        onSave={handleAddColumn}
      />
    </Box>
  );
}
