import { Add, Delete } from '@mui/icons-material';
import {
  Box,
  Button,
  CircularProgress,
  IconButton,
  MenuItem,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TextField,
  Typography,
} from '@mui/material';
import { useEffect, useState } from 'react';

import { apiClient } from '@/core/api/client';
import { notifyActions } from '@/core/store/notifications/notificationStore';

interface SubFormEntry {
  id: string;
  parentFormId: string;
  relationCode: string;
  childFormCode: string;
  label: string;
  displayAs: string;
  position: number;
}

interface AvailableRelation {
  relationCode: string;
  childTableCode: string;
  childTableLabel: string;
  relationColumnLabel: string;
  existingFormCodes: string[];
}

interface Props {
  formId: string;
}

export function SubFormsTab({ formId }: Props) {
  const [subForms, setSubForms] = useState<SubFormEntry[]>([]);
  const [relations, setRelations] = useState<AvailableRelation[]>([]);
  const [loading, setLoading] = useState(true);
  const [adding, setAdding] = useState(false);
  const [newSf, setNewSf] = useState({
    relationCode: '',
    childFormCode: '',
    label: '',
    displayAs: 'tab',
  });

  useEffect(() => {
    loadData();
  }, [formId]);

  const loadData = async () => {
    setLoading(true);
    try {
      const [sfRes, relRes] = await Promise.all([
        apiClient.get(`/metadata/forms/${formId}/subforms`),
        apiClient.get(`/metadata/forms/${formId}/subforms/available-relations`),
      ]);
      setSubForms(sfRes.data.data ?? []);
      setRelations(relRes.data.data ?? []);
    } catch {
      /* handled */
    }
    setLoading(false);
  };

  const handleAdd = async () => {
    try {
      await apiClient.post(`/metadata/forms/${formId}/subforms`, newSf);
      notifyActions.success('Sub-form added.');
      setAdding(false);
      setNewSf({ relationCode: '', childFormCode: '', label: '', displayAs: 'tab' });
      loadData();
    } catch {
      notifyActions.error('Failed to add sub-form.');
    }
  };

  const handleDelete = async (id: string) => {
    try {
      await apiClient.delete(`/metadata/forms/${formId}/subforms/${id}`);
      notifyActions.success('Sub-form removed.');
      loadData();
    } catch {
      notifyActions.error('Failed to remove sub-form.');
    }
  };

  if (loading) return <CircularProgress sx={{ m: 2 }} />;

  return (
    <Box>
      <Typography variant="subtitle2" sx={{ mb: 2 }}>
        Configured Sub-Forms
      </Typography>

      {subForms.length === 0 && !adding && (
        <Typography color="text.secondary" sx={{ mb: 2 }}>
          No sub-forms configured.
        </Typography>
      )}

      {subForms.length > 0 && (
        <TableContainer>
          <Table size="small">
            <TableHead>
              <TableRow>
                <TableCell>Label</TableCell>
                <TableCell>Relation</TableCell>
                <TableCell>Child Form</TableCell>
                <TableCell>Display</TableCell>
                <TableCell width={60} />
              </TableRow>
            </TableHead>
            <TableBody>
              {subForms.map((sf) => (
                <TableRow key={sf.id}>
                  <TableCell>{sf.label}</TableCell>
                  <TableCell>
                    <code>{sf.relationCode}</code>
                  </TableCell>
                  <TableCell>
                    <code>{sf.childFormCode}</code>
                  </TableCell>
                  <TableCell>{sf.displayAs}</TableCell>
                  <TableCell>
                    <IconButton size="small" color="error" onClick={() => handleDelete(sf.id)}>
                      <Delete fontSize="small" />
                    </IconButton>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      )}

      {adding && (
        <Box sx={{ display: 'flex', gap: 1, mt: 2, flexWrap: 'wrap', alignItems: 'center' }}>
          <TextField
            select
            size="small"
            label="Relation"
            value={newSf.relationCode}
            onChange={(e) => {
              const rel = relations.find((r) => r.relationCode === e.target.value);
              setNewSf((p) => ({
                ...p,
                relationCode: e.target.value,
                childFormCode: rel?.existingFormCodes?.[0] ?? '',
                label: rel?.relationColumnLabel ?? e.target.value,
              }));
            }}
            sx={{ width: 180 }}
          >
            {relations.map((r) => (
              <MenuItem key={r.relationCode} value={r.relationCode}>
                {r.childTableLabel} ({r.relationCode})
              </MenuItem>
            ))}
          </TextField>
          <TextField
            size="small"
            label="Label"
            value={newSf.label}
            onChange={(e) => setNewSf((p) => ({ ...p, label: e.target.value }))}
            sx={{ width: 140 }}
          />
          <TextField
            select
            size="small"
            label="Display"
            value={newSf.displayAs}
            onChange={(e) => setNewSf((p) => ({ ...p, displayAs: e.target.value }))}
            sx={{ width: 100 }}
          >
            <MenuItem value="tab">Tab</MenuItem>
            <MenuItem value="grid">Grid</MenuItem>
            <MenuItem value="inline">Inline</MenuItem>
          </TextField>
          <Button
            size="small"
            variant="contained"
            onClick={handleAdd}
            disabled={!newSf.relationCode || !newSf.label}
          >
            Save
          </Button>
          <Button size="small" onClick={() => setAdding(false)}>
            Cancel
          </Button>
        </Box>
      )}

      {!adding && relations.length > 0 && (
        <Button startIcon={<Add />} size="small" onClick={() => setAdding(true)} sx={{ mt: 2 }}>
          Add Sub-Form
        </Button>
      )}
    </Box>
  );
}
