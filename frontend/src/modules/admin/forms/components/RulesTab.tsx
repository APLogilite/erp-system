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

import { useAddRule, useDeleteRule, useFormRules } from '../hooks/useFormRules';
import type { FieldRule } from '../hooks/useFormRules';

const OPERATORS = [
  'equals',
  'not_equals',
  'greater_than',
  'less_than',
  'contains',
  'is_empty',
  'is_not_empty',
  'in',
];
const ACTIONS = ['show', 'hide', 'read_only', 'editable', 'required', 'optional'];

interface Props {
  formId: string;
  fields: { fieldId: string; columnCode: string; label: string }[];
  selectedFieldId?: string;
}

export function RulesTab({ formId, fields, selectedFieldId }: Props) {
  const [fieldId, setFieldId] = useState(selectedFieldId ?? '');
  const { data: rules } = useFormRules(formId, fieldId || undefined);
  const addRule = useAddRule(formId);
  const deleteRule = useDeleteRule(formId, fieldId);

  const [adding, setAdding] = useState(false);
  const [newRule, setNewRule] = useState({
    conditionField: '',
    conditionOperator: 'equals',
    conditionValue: '',
    action: 'show',
    logicGroup: 0,
  });

  const handleAdd = () => {
    addRule.mutate(
      { fieldId, data: newRule },
      {
        onSuccess: () => {
          setAdding(false);
          setNewRule({
            conditionField: '',
            conditionOperator: 'equals',
            conditionValue: '',
            action: 'show',
            logicGroup: 0,
          });
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
                  <TableCell>When</TableCell>
                  <TableCell>Operator</TableCell>
                  <TableCell>Value</TableCell>
                  <TableCell>Action</TableCell>
                  <TableCell>Group</TableCell>
                  <TableCell width={60} />
                </TableRow>
              </TableHead>
              <TableBody>
                {rules?.map((r: FieldRule) => (
                  <TableRow key={r.id}>
                    <TableCell>
                      <code>{r.conditionField}</code>
                    </TableCell>
                    <TableCell>{r.conditionOperator}</TableCell>
                    <TableCell>{r.conditionValue ?? '—'}</TableCell>
                    <TableCell>
                      <strong>{r.action}</strong>
                    </TableCell>
                    <TableCell>{r.logicGroup}</TableCell>
                    <TableCell>
                      <IconButton
                        size="small"
                        color="error"
                        onClick={() => deleteRule.mutate(r.id)}
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
                size="small"
                label="Field"
                value={newRule.conditionField}
                onChange={(e) => setNewRule((p) => ({ ...p, conditionField: e.target.value }))}
                sx={{ width: 120 }}
              />
              <TextField
                select
                size="small"
                label="Operator"
                value={newRule.conditionOperator}
                onChange={(e) => setNewRule((p) => ({ ...p, conditionOperator: e.target.value }))}
                sx={{ width: 140 }}
              >
                {OPERATORS.map((op) => (
                  <MenuItem key={op} value={op}>
                    {op}
                  </MenuItem>
                ))}
              </TextField>
              <TextField
                size="small"
                label="Value"
                value={newRule.conditionValue}
                onChange={(e) => setNewRule((p) => ({ ...p, conditionValue: e.target.value }))}
                sx={{ width: 100 }}
              />
              <TextField
                select
                size="small"
                label="Action"
                value={newRule.action}
                onChange={(e) => setNewRule((p) => ({ ...p, action: e.target.value }))}
                sx={{ width: 120 }}
              >
                {ACTIONS.map((a) => (
                  <MenuItem key={a} value={a}>
                    {a}
                  </MenuItem>
                ))}
              </TextField>
              <TextField
                size="small"
                label="Group"
                type="number"
                value={newRule.logicGroup}
                onChange={(e) =>
                  setNewRule((p) => ({ ...p, logicGroup: parseInt(e.target.value, 10) || 0 }))
                }
                sx={{ width: 80 }}
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
              Add Rule
            </Button>
          )}

          {(!rules || rules.length === 0) && !adding && (
            <Typography color="text.secondary" sx={{ mt: 2 }}>
              No rules configured for this field.
            </Typography>
          )}
        </>
      )}
    </Box>
  );
}
