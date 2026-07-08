import { ArrowDownward, ArrowUpward, Delete, Edit } from '@mui/icons-material';
import {
  Button,
  IconButton,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
} from '@mui/material';

import type { TableColumn } from '../types';
import { COLUMN_TYPE_LABELS } from '../types';

interface Props {
  columns: TableColumn[];
  onAdd: () => void;
  onEdit: (col: TableColumn) => void;
  onDelete: (col: TableColumn) => void;
  onMoveUp: (col: TableColumn, index: number) => void;
  onMoveDown: (col: TableColumn, index: number) => void;
}

export function ColumnList({ columns, onAdd, onEdit, onDelete, onMoveUp, onMoveDown }: Props) {
  const activeColumns = columns.filter((c) => c.isActive);

  return (
    <>
      <TableContainer>
        <Table size="small">
          <TableHead>
            <TableRow>
              <TableCell>#</TableCell>
              <TableCell>Code</TableCell>
              <TableCell>Label</TableCell>
              <TableCell>Type</TableCell>
              <TableCell>Required</TableCell>
              <TableCell align="right">Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {activeColumns.map((col, idx) => (
              <TableRow key={col.id} hover>
                <TableCell>
                  {idx + 1}
                  <IconButton size="small" onClick={() => onMoveUp(col, idx)} disabled={idx === 0}>
                    <ArrowUpward fontSize="small" />
                  </IconButton>
                  <IconButton
                    size="small"
                    onClick={() => onMoveDown(col, idx)}
                    disabled={idx === activeColumns.length - 1}
                  >
                    <ArrowDownward fontSize="small" />
                  </IconButton>
                </TableCell>
                <TableCell>
                  <code>{col.code}</code>
                </TableCell>
                <TableCell>{col.label}</TableCell>
                <TableCell>{COLUMN_TYPE_LABELS[col.type] ?? col.type}</TableCell>
                <TableCell>{col.required ? 'Yes' : 'No'}</TableCell>
                <TableCell align="right">
                  <IconButton size="small" onClick={() => onEdit(col)}>
                    <Edit fontSize="small" />
                  </IconButton>
                  <IconButton size="small" color="error" onClick={() => onDelete(col)}>
                    <Delete fontSize="small" />
                  </IconButton>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
      <Button variant="outlined" onClick={onAdd} sx={{ mt: 2 }}>
        + Add Column
      </Button>
    </>
  );
}
