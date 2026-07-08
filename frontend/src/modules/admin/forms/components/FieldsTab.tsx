import { ArrowDownward, ArrowUpward, Visibility, VisibilityOff } from '@mui/icons-material';
import {
  Box,
  Checkbox,
  FormControlLabel,
  IconButton,
  Switch,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TextField,
} from '@mui/material';

import { useFormFields, useReorderFields, useUpdateField } from '../hooks/useFormFields';
import type { FormField } from '../types';

interface Props {
  formId: string;
}

export function FieldsTab({ formId }: Props) {
  const { data: fields, isLoading } = useFormFields(formId);
  const updateField = useUpdateField(formId);
  const reorderFields = useReorderFields(formId);

  const handleToggle = (field: FormField, key: 'isVisible' | 'isEditable' | 'isRequired') => {
    updateField.mutate({
      fieldId: field.id,
      data: { [key]: !field[key] },
    });
  };

  const handleLabelOverride = (field: FormField, value: string) => {
    updateField.mutate({ fieldId: field.id, data: { labelOverride: value } });
  };

  const handlePlaceholder = (field: FormField, value: string) => {
    updateField.mutate({ fieldId: field.id, data: { placeholder: value } });
  };

  const handleMove = (index: number, direction: number) => {
    if (!fields) return;
    const sorted = [...fields].sort((a, b) => a.position - b.position);
    const newIndex = index + direction;
    if (newIndex < 0 || newIndex >= sorted.length) return;
    const ids = sorted.map((f) => f.id);
    [ids[index], ids[newIndex]] = [ids[newIndex], ids[index]];
    reorderFields.mutate(ids);
  };

  const sorted = fields ? [...fields].sort((a, b) => a.position - b.position) : [];

  if (isLoading) return <Box sx={{ p: 2 }}>Loading fields...</Box>;

  return (
    <TableContainer>
      <Table size="small">
        <TableHead>
          <TableRow>
            <TableCell>#</TableCell>
            <TableCell>Column</TableCell>
            <TableCell>Label Override</TableCell>
            <TableCell>Placeholder</TableCell>
            <TableCell>Visible</TableCell>
            <TableCell>Editable</TableCell>
            <TableCell>Required</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {sorted.map((field, idx) => (
            <TableRow key={field.id} hover>
              <TableCell>
                {idx + 1}
                <IconButton size="small" onClick={() => handleMove(idx, -1)} disabled={idx === 0}>
                  <ArrowUpward fontSize="small" />
                </IconButton>
                <IconButton size="small" onClick={() => handleMove(idx, 1)}
                  disabled={idx === sorted.length - 1}>
                  <ArrowDownward fontSize="small" />
                </IconButton>
              </TableCell>
              <TableCell><code>{field.columnCode}</code></TableCell>
              <TableCell>
                <TextField size="small" variant="standard"
                  value={field.labelOverride ?? ''}
                  onChange={(e) => handleLabelOverride(field, e.target.value)}
                  sx={{ width: 140 }} />
              </TableCell>
              <TableCell>
                <TextField size="small" variant="standard"
                  value={field.placeholder ?? ''}
                  onChange={(e) => handlePlaceholder(field, e.target.value)}
                  sx={{ width: 120 }} />
              </TableCell>
              <TableCell>
                <IconButton size="small" onClick={() => handleToggle(field, 'isVisible')}>
                  {field.isVisible !== false ? <Visibility fontSize="small" color="primary" /> : <VisibilityOff fontSize="small" />}
                </IconButton>
              </TableCell>
              <TableCell>
                <FormControlLabel control={
                  <Switch size="small" checked={field.isEditable !== false}
                    onChange={() => handleToggle(field, 'isEditable')} />
                } label="" />
              </TableCell>
              <TableCell>
                <Checkbox size="small" checked={field.isRequired === true}
                  onChange={() => handleToggle(field, 'isRequired')} />
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
}
