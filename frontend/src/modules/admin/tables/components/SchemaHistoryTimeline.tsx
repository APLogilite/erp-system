import { Circle } from '@mui/icons-material';
import { Box, Typography } from '@mui/material';

import type { VersionHistoryEntry } from '../types';

interface Props {
  entries: VersionHistoryEntry[] | undefined;
  isLoading: boolean;
}

export function SchemaHistoryTimeline({ entries, isLoading }: Props) {
  if (isLoading) {
    return <Typography sx={{ p: 2 }}>Loading history...</Typography>;
  }

  if (!entries || entries.length === 0) {
    return (
      <Typography color="text.secondary" sx={{ p: 2 }}>
        No schema changes recorded yet.
      </Typography>
    );
  }

  return (
    <Box sx={{ pt: 1 }}>
      {entries.map((entry, idx) => (
        <Box key={entry.id} sx={{ display: 'flex', gap: 2, pb: 2 }}>
          <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', mt: 0.5 }}>
            <Circle sx={{ fontSize: 12, color: 'primary.main' }} />
            {idx < entries.length - 1 && (
              <Box
                sx={{
                  width: 2,
                  flexGrow: 1,
                  bgcolor: 'divider',
                  mt: 0.5,
                }}
              />
            )}
          </Box>
          <Box>
            <Typography variant="body1" fontWeight={600}>
              {entry.description}
            </Typography>
            <Typography variant="caption" color="text.secondary">
              Version {entry.version}
              {entry.createdAt ? ` — ${new Date(entry.createdAt).toLocaleString()}` : ''}
            </Typography>
          </Box>
        </Box>
      ))}
    </Box>
  );
}
