import { Add, Delete, Edit, Refresh } from '@mui/icons-material';
import {
  Box,
  Button,
  Card,
  IconButton,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Typography,
  CircularProgress,
} from '@mui/material';

import { EmptyState } from '@/components/ui/EmptyState';
import { ErrorState } from '@/components/ui/ErrorState';

export interface ColumnDef<T> {
  key: string;
  label: string;
  render?: (item: T) => React.ReactNode;
  width?: number | string;
}

interface AdminListPageProps<T extends { id: string }> {
  title: string;
  columns: ColumnDef<T>[];
  data: T[] | undefined;
  isLoading: boolean;
  error: Error | null;
  onRefresh: () => void;
  onCreate?: () => void;
  onEdit?: (item: T) => void;
  onDelete?: (item: T) => void;
  renderActions?: (item: T) => React.ReactNode;
}

export function AdminListPage<T extends { id: string }>({
  title,
  columns,
  data,
  isLoading,
  error,
  onRefresh,
  onCreate,
  onEdit,
  onDelete,
  renderActions,
}: AdminListPageProps<T>) {
  return (
    <Box sx={{ p: 3 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h5" fontWeight={700}>
          {title}
        </Typography>
        <Box sx={{ display: 'flex', gap: 1 }}>
          <IconButton onClick={onRefresh}>
            <Refresh />
          </IconButton>
          {onCreate && (
            <Button
              variant="contained"
              startIcon={<Add />}
              onClick={onCreate}
              sx={{ borderRadius: 2, textTransform: 'none' }}
            >
              Create
            </Button>
          )}
        </Box>
      </Box>

      <Card sx={{ borderRadius: 3 }}>
        {error && <ErrorState message={error.message} onRetry={onRefresh} />}
        {isLoading && (
          <Box sx={{ display: 'flex', justifyContent: 'center', p: 4 }}>
            <CircularProgress />
          </Box>
        )}
        {data && data.length === 0 && <EmptyState title="No records found" />}
        {data && data.length > 0 && (
          <TableContainer>
            <Table>
              <TableHead>
                <TableRow>
                  {columns.map((col) => (
                    <TableCell key={col.key} sx={{ fontWeight: 600, width: col.width }}>
                      {col.label}
                    </TableCell>
                  ))}
                  {(onEdit || onDelete || renderActions) && (
                    <TableCell sx={{ fontWeight: 600 }} width={100}>
                      Actions
                    </TableCell>
                  )}
                </TableRow>
              </TableHead>
              <TableBody>
                {data.map((item) => (
                  <TableRow key={item.id} hover>
                    {columns.map((col) => (
                      <TableCell key={col.key}>
                        {col.render
                          ? col.render(item)
                          : String((item as Record<string, unknown>)[col.key] ?? '')}
                      </TableCell>
                    ))}
                    {(onEdit || onDelete || renderActions) && (
                      <TableCell>
                        <Box sx={{ display: 'flex', gap: 0.5 }}>
                          {renderActions ? (
                            renderActions(item)
                          ) : (
                            <>
                              {onEdit && (
                                <IconButton size="small" onClick={() => onEdit(item)}>
                                  <Edit fontSize="small" />
                                </IconButton>
                              )}
                              {onDelete && (
                                <IconButton
                                  size="small"
                                  onClick={() => onDelete(item)}
                                  color="error"
                                >
                                  <Delete fontSize="small" />
                                </IconButton>
                              )}
                            </>
                          )}
                        </Box>
                      </TableCell>
                    )}
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        )}
      </Card>
    </Box>
  );
}
