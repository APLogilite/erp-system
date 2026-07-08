import { Badge, Box, Typography } from '@mui/material';

interface Props {
  label: string;
  count: number;
}

export function SubFormTabBadge({ label, count }: Props) {
  return (
    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
      <Typography variant="body2">{label}</Typography>
      <Badge badgeContent={count} color="primary" max={999}
        sx={{ '& .MuiBadge-badge': { position: 'static', transform: 'none' } }} />
    </Box>
  );
}
