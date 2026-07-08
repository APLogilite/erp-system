import { ChevronLeft, ChevronRight } from '@mui/icons-material';
import { Box, IconButton, Typography } from '@mui/material';

interface Props {
  recordIndex?: number;
  totalRecords?: number;
  hasPrevious: boolean;
  hasNext: boolean;
  onPrevious: () => void;
  onNext: () => void;
}

export function RecordNavigator({
  recordIndex,
  totalRecords,
  hasPrevious,
  hasNext,
  onPrevious,
  onNext,
}: Props) {
  if (recordIndex === undefined || totalRecords === undefined) return null;

  return (
    <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
      <IconButton size="small" onClick={onPrevious} disabled={!hasPrevious}
        title="Previous record (Alt+Left)">
        <ChevronLeft />
      </IconButton>
      <Typography variant="body2" sx={{ minWidth: 80, textAlign: 'center', whiteSpace: 'nowrap' }}>
        {recordIndex} of {totalRecords}
      </Typography>
      <IconButton size="small" onClick={onNext} disabled={!hasNext}
        title="Next record (Alt+Right)">
        <ChevronRight />
      </IconButton>
    </Box>
  );
}
