import { Add, Delete } from '@mui/icons-material';
import {
  Box,
  Button,
  IconButton,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TextField,
  Typography,
} from '@mui/material';
import { useCallback, useState } from 'react';

import type { RecordEntry } from '../hooks/useForm.types';

export interface ColumnInfo {
  key: string;
  label: string;
  editable?: boolean;
}

interface Props {
  columns: ColumnInfo[];
  records: RecordEntry[];
  onAdd: (record: Omit<RecordEntry, 'id'>) => void;
  onUpdate: (recordId: string, data: Partial<RecordEntry>) => void;
  onDelete: (recordId: string) => void;
  onRowClick?: (recordId: string) => void;
}

export function InlineEditableGrid({
  columns,
  records,
  onAdd,
  onUpdate,
  onDelete,
  onRowClick,
}: Props) {
  const [editingCell, setEditingCell] = useState<{ row: number; col: string } | null>(null);
  const [editValue, setEditValue] = useState('');
  const [adding, setAdding] = useState(false);
  const [newRow, setNewRow] = useState<Record<string, string>>({});

  const startEdit = (rowIdx: number, col: string, currentValue: unknown) => {
    setEditingCell({ row: rowIdx, col });
    setEditValue(String(currentValue ?? ''));
  };

  const commitEdit = useCallback(() => {
    if (!editingCell) return;
    const record = records[editingCell.row];
    if (record) {
      onUpdate(record.id, { [editingCell.col]: editValue });
    }
    setEditingCell(null);
  }, [editingCell, editValue, records, onUpdate]);

  const handleAdd = () => {
    const entry: Record<string, string> = {};
    columns.forEach((c) => {
      entry[c.key] = newRow[c.key] ?? '';
    });
    onAdd(entry as unknown as Omit<RecordEntry, 'id'>);
    setNewRow({});
    setAdding(false);
  };

  return (
    <Box>
      <TableContainer>
        <Table size="small">
          <TableHead>
            <TableRow>
              {columns.map((c) => (
                <TableCell key={c.key} sx={{ fontWeight: 600 }}>
                  {c.label}
                </TableCell>
              ))}
              <TableCell width={80} />
            </TableRow>
          </TableHead>
          <TableBody>
            {records.length === 0 && !adding && (
              <TableRow>
                <TableCell colSpan={columns.length + 1}>
                  <Typography color="text.secondary" sx={{ py: 2, textAlign: 'center' }}>
                    No records yet. Click Add to create one.
                  </Typography>
                </TableCell>
              </TableRow>
            )}
            {records.map((rec, rowIdx) => (
              <TableRow
                key={rec.id}
                hover
                sx={{ cursor: onRowClick ? 'pointer' : undefined }}
                onClick={() => onRowClick?.(rec.id)}
              >
                {columns.map((c) => (
                  <TableCell
                    key={c.key}
                    onDoubleClick={() =>
                      c.editable !== false && startEdit(rowIdx, c.key, rec[c.key])
                    }
                  >
                    {editingCell?.row === rowIdx && editingCell?.col === c.key ? (
                      <TextField
                        size="small"
                        variant="standard"
                        autoFocus
                        value={editValue}
                        onChange={(e) => setEditValue(e.target.value)}
                        onBlur={commitEdit}
                        onKeyDown={(e) => {
                          if (e.key === 'Enter') commitEdit();
                          if (e.key === 'Escape') setEditingCell(null);
                        }}
                        sx={{ width: '100%' }}
                      />
                    ) : (
                      String(rec[c.key] ?? '')
                    )}
                  </TableCell>
                ))}
                <TableCell>
                  <IconButton
                    size="small"
                    color="error"
                    onClick={(e) => {
                      e.stopPropagation();
                      onDelete(rec.id);
                    }}
                  >
                    <Delete fontSize="small" />
                  </IconButton>
                </TableCell>
              </TableRow>
            ))}
            {adding && (
              <TableRow>
                {columns.map((c) => (
                  <TableCell key={c.key}>
                    <TextField
                      size="small"
                      variant="standard"
                      value={newRow[c.key] ?? ''}
                      onChange={(e) => setNewRow((p) => ({ ...p, [c.key]: e.target.value }))}
                      placeholder={c.label}
                    />
                  </TableCell>
                ))}
                <TableCell>
                  <Button size="small" variant="contained" onClick={handleAdd}>
                    Save
                  </Button>
                  <Button size="small" onClick={() => setAdding(false)} sx={{ ml: 0.5 }}>
                    Cancel
                  </Button>
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </TableContainer>
      {!adding && (
        <Button size="small" startIcon={<Add />} onClick={() => setAdding(true)} sx={{ mt: 1 }}>
          Add Row
        </Button>
      )}
    </Box>
  );
}
