import { Add, Delete } from '@mui/icons-material';
import {
  Box,
  Button,
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
import { useState } from 'react';

import {
  useAddValidation,
  useDeleteValidation,
  useFormValidations,
} from '../hooks/useFormValidations';
import type { FieldValidation } from '../hooks/useFormValidations';

const VALIDATION_TYPES = [
  'required',
  'min_length',
  'max_length',
  'min',
  'max',
  'pattern',
  'custom_expression',
];

interface Props {
  formId: string;
  fields: { fieldId: string; columnCode: string; label: string }[];
  selectedFieldId?: string;
}

export function ValidationTab({ formId, fields, selectedFieldId }: Props) {
  const [fieldId, setFieldId] = useState(selectedFieldId ?? '');
  const { data: validations } = useFormValidations(formId, fieldId || undefined);
  const addValidation = useAddValidation(formId);
  const deleteValidation = useDeleteValidation(formId, fieldId);

  const [adding, setAdding] = useState(false);
  const [newVal, setNewVal] = useState({ type: 'required', value: '', message: '' });

  const handleAdd = () => {
    addValidation.mutate(
      { fieldId, data: newVal },
      {
        onSuccess: () => {
          setAdding(false);
          setNewVal({ type: 'required', value: '', message: '' });
        },
      }
    );
  };

  return (
    <Box>
      <TextField
        select
        size="small"
        label="Select Field"
        value={fieldId}
        onChange={(e) => setFieldId(e.target.value)}
        sx={{ mb: 2, minWidth: 240 }}
      >
        {fields.map((f) => (
          <MenuItem key={f.fieldId} value={f.fieldId}>
            {f.label} ({f.columnCode})
          </MenuItem>
        ))}
      </TextField>

      {fieldId && (
        <>
          <TableContainer>
            <Table size="small">
              <TableHead>
                <TableRow>
                  <TableCell>Type</TableCell>
                  <TableCell>Value</TableCell>
                  <TableCell>Message</TableCell>
                  <TableCell width={60} />
                </TableRow>
              </TableHead>
              <TableBody>
                {validations?.map((v: FieldValidation) => (
                  <TableRow key={v.id}>
                    <TableCell>
                      <strong>{v.type}</strong>
                    </TableCell>
                    <TableCell>{v.value ?? '—'}</TableCell>
                    <TableCell>{v.message ?? '—'}</TableCell>
                    <TableCell>
                      <IconButton
                        size="small"
                        color="error"
                        onClick={() => deleteValidation.mutate(v.id)}
                      >
                        <Delete fontSize="small" />
                      </IconButton>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>

          {adding && (
            <Box sx={{ display: 'flex', gap: 1, mt: 2, flexWrap: 'wrap', alignItems: 'center' }}>
              <TextField
                select
                size="small"
                label="Type"
                value={newVal.type}
                onChange={(e) => setNewVal((p) => ({ ...p, type: e.target.value }))}
                sx={{ width: 160 }}
              >
                {VALIDATION_TYPES.map((t) => (
                  <MenuItem key={t} value={t}>
                    {t}
                  </MenuItem>
                ))}
              </TextField>
              <TextField
                size="small"
                label="Value"
                value={newVal.value}
                onChange={(e) => setNewVal((p) => ({ ...p, value: e.target.value }))}
                sx={{ width: 140 }}
              />
              <TextField
                size="small"
                label="Message"
                value={newVal.message}
                onChange={(e) => setNewVal((p) => ({ ...p, message: e.target.value }))}
                sx={{ width: 200 }}
              />
              <Button size="small" variant="contained" onClick={handleAdd}>
                Save
              </Button>
              <Button size="small" onClick={() => setAdding(false)}>
                Cancel
              </Button>
            </Box>
          )}

          {!adding && (
            <Button startIcon={<Add />} size="small" onClick={() => setAdding(true)} sx={{ mt: 2 }}>
              Add Validation
            </Button>
          )}

          {(!validations || validations.length === 0) && !adding && (
            <Typography color="text.secondary" sx={{ mt: 2 }}>
              No validations configured for this field.
            </Typography>
          )}
        </>
      )}
    </Box>
  );
}
