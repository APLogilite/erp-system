import { ChevronRight } from '@mui/icons-material';
import { Breadcrumbs, Link, Typography } from '@mui/material';
import { useNavigate } from 'react-router-dom';

import type { BreadcrumbEntry } from '../hooks/useForm.types';

interface Props {
  breadcrumb: BreadcrumbEntry[] | undefined;
}

export function FormBreadcrumb({ breadcrumb }: Props) {
  const navigate = useNavigate();

  if (!breadcrumb || breadcrumb.length === 0) return null;

  return (
    <Breadcrumbs separator={<ChevronRight fontSize="small" />} sx={{ mb: 2 }}>
      {breadcrumb.map((entry, idx) => {
        const isLast = idx === breadcrumb.length - 1;
        return isLast ? (
          <Typography key={idx} color="text.primary" variant="body2">
            {entry.label}
          </Typography>
        ) : (
          <Link
            key={idx}
            underline="hover"
            color="inherit"
            variant="body2"
            sx={{ cursor: 'pointer' }}
            onClick={() =>
              navigate(
                `/app/runtime?form=${encodeURIComponent(entry.formCode)}&record=${entry.recordId}`
              )
            }
          >
            {entry.label}
          </Link>
        );
      })}
    </Breadcrumbs>
  );
}
