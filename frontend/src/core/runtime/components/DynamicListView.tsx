import { Add, Refresh } from '@mui/icons-material';
import {
  Box,
  Button,
  Card,
  CircularProgress,
  IconButton,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TableSortLabel,
  TextField,
  Typography,
} from '@mui/material';
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';

import { EmptyState } from '@/components/ui/EmptyState';
import { ErrorState } from '@/components/ui/ErrorState';

import { useRecordList } from '../hooks/useRecordList';
import type { FormDefinition } from '../hooks/useForm.types';

interface Props {
  formDefinition: FormDefinition;
}

export function DynamicListView({ formDefinition }: Props) {
  const navigate = useNavigate();
  const [page, setPage] = useState(0);
  const [pageSize] = useState(20);
  const [sortField, setSortField] = useState<string | undefined>();
  const [sortDir, setSortDir] = useState<'asc' | 'desc'>('asc');
  const [search, setSearch] = useState('');
  const [searchInput, setSearchInput] = useState('');
  const searchTimer = useState<ReturnType<typeof setTimeout> | null>(null);

  const { data, isLoading, error, refetch } = useRecordList(
    formDefinition.formCode,
    page,
    pageSize,
    sortField,
    sortDir,
    search,
  );

  // Debounced search
  const handleSearchChange = (val: string) => {
    setSearchInput(val);
    if (searchTimer[0]) clearTimeout(searchTimer[0]);
    const t = setTimeout(() => { setSearch(val); setPage(0); }, 300);
    searchTimer[1]?.(t);
  };

  const handleSort = (field: string) => {
    if (sortField === field) {
      setSortDir((d) => (d === 'asc' ? 'desc' : 'asc'));
    } else {
      setSortField(field);
      setSortDir('asc');
    }
    setPage(0);
  };

  const visibleFields = formDefinition.fields
    .filter((f) => f.visible)
    .sort((a, b) => a.position - b.position)
    .slice(0, 8);

  const records = data?.records ?? [];
  const total = data?.total ?? 0;
  const totalPages = Math.max(1, Math.ceil(total / pageSize));

  return (
    <Box sx={{ p: 3 }}>
      {/* Header */}
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h5" fontWeight={700}>
          {formDefinition.formLabel}
        </Typography>
        <Box sx={{ display: 'flex', gap: 1 }}>
          <TextField
            size="small"
            placeholder="Search..."
            value={searchInput}
            onChange={(e) => handleSearchChange(e.target.value)}
            sx={{ width: 240 }}
          />
          <IconButton onClick={() => refetch()}>
            <Refresh />
          </IconButton>
          <Button
            variant="contained"
            startIcon={<Add />}
            onClick={() => navigate(`/app/runtime?form=${formDefinition.formCode}&mode=create`)}
            sx={{ borderRadius: 2, textTransform: 'none' }}
          >
            Create New
          </Button>
        </Box>
      </Box>

      {/* Grid */}
      <Card sx={{ borderRadius: 3 }}>
        {error && <ErrorState message={(error as Error).message} onRetry={refetch} />}
        {isLoading && (
          <Box sx={{ display: 'flex', justifyContent: 'center', p: 4 }}>
            <CircularProgress />
          </Box>
        )}
        {!isLoading && !error && records.length === 0 && (
          <EmptyState title="No records" message="Click 'Create New' to add the first record." />
        )}
        {!isLoading && !error && records.length > 0 && (
          <>
            <TableContainer>
              <Table>
                <TableHead>
                  <TableRow>
                    {visibleFields.map((f) => (
                      <TableCell key={f.fieldId} sx={{ fontWeight: 600 }}>
                        <TableSortLabel
                          active={sortField === f.columnCode}
                          direction={sortField === f.columnCode ? sortDir : 'asc'}
                          onClick={() => handleSort(f.columnCode)}
                        >
                          {f.label}
                        </TableSortLabel>
                      </TableCell>
                    ))}
                  </TableRow>
                </TableHead>
                <TableBody>
                  {records.map((rec) => (
                    <TableRow
                      key={rec.id}
                      hover
                      sx={{ cursor: 'pointer' }}
                      onClick={() =>
                        navigate(
                          `/app/runtime?form=${formDefinition.formCode}&record=${rec.id}`,
                        )
                      }
                    >
                      {visibleFields.map((f) => (
                        <TableCell key={f.fieldId}>
                          {formatCellValue(rec[f.columnCode])}
                        </TableCell>
                      ))}
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>

            {/* Pagination */}
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', p: 2 }}>
              <Typography variant="body2" color="text.secondary">
                Showing {page * pageSize + 1}–{Math.min((page + 1) * pageSize, total)} of {total}
              </Typography>
              <Box sx={{ display: 'flex', gap: 1 }}>
                <Button
                  size="small"
                  disabled={page === 0}
                  onClick={() => setPage((p) => p - 1)}
                >
                  Previous
                </Button>
                <Typography variant="body2" sx={{ alignSelf: 'center' }}>
                  Page {page + 1} of {totalPages}
                </Typography>
                <Button
                  size="small"
                  disabled={page >= totalPages - 1}
                  onClick={() => setPage((p) => p + 1)}
                >
                  Next
                </Button>
              </Box>
            </Box>
          </>
        )}
      </Card>
    </Box>
  );
}

function formatCellValue(value: unknown): string {
  if (value === null || value === undefined) return '';
  if (typeof value === 'boolean') return value ? 'Yes' : 'No';
  if (typeof value === 'object') return JSON.stringify(value);
  return String(value);
}
