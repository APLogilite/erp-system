import { SwapHoriz, Badge, Settings } from '@mui/icons-material';
import { Box, Chip, Menu, Typography, IconButton, Divider, Tooltip, Button } from '@mui/material';
import { useQuery } from '@tanstack/react-query';
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';

import { apiClient } from '@/core/api/client';
import { ENDPOINTS } from '@/core/api/endpoints';

interface ContextCurrent {
  tenantCode?: string;
  tenantName?: string;
  organizationCode?: string;
  organizationName?: string;
  companyCode?: string;
  companyName?: string;
  branchCode?: string;
  branchName?: string;
  departmentCode?: string;
  departmentName?: string;
  roles?: string[];
}

export function ContextSwitcher() {
  const [anchorEl, setAnchorEl] = useState<HTMLElement | null>(null);
  const navigate = useNavigate();

  const { data: current } = useQuery<ContextCurrent>({
    queryKey: ['context', 'current'],
    queryFn: async () => {
      const res = await apiClient.get(ENDPOINTS.context.current);
      return res.data.data || res.data;
    },
  });

  const activeRole = current?.roles?.[0];

  const activeContexts: { label: string; value?: string }[] = [
    { label: 'Tenant', value: current?.tenantName || current?.tenantCode },
    { label: 'Org', value: current?.organizationName || current?.organizationCode },
    { label: 'Company', value: current?.companyName || current?.companyCode },
    { label: 'Branch', value: current?.branchName || current?.branchCode },
  ];

  const hasContext = activeContexts.some((c) => c.value);

  return (
    <Box>
      <Tooltip title="Switch Context">
        <IconButton onClick={(e) => setAnchorEl(e.currentTarget)} size="small" sx={{ ml: 1 }}>
          <SwapHoriz />
        </IconButton>
      </Tooltip>

      <Menu
        anchorEl={anchorEl}
        open={!!anchorEl}
        onClose={() => setAnchorEl(null)}
        PaperProps={{ sx: { width: 340, p: 1.5 } }}
      >
        {hasContext && activeRole && (
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 1, px: 0.5 }}>
            <Badge color="primary" sx={{ fontSize: 28 }} />
            <Box>
              <Typography variant="subtitle1" fontWeight={700} lineHeight={1.2}>
                {activeRole}
              </Typography>
              <Typography variant="caption" color="text.secondary">
                Current Role
              </Typography>
            </Box>
          </Box>
        )}

        <Typography variant="subtitle2" sx={{ color: 'text.secondary', mb: 0.5, px: 0.5 }}>
          Current Context
        </Typography>
        <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 0.5, mb: 0.5 }}>
          {hasContext ? (
            activeContexts.map(
              (c) =>
                c.value && (
                  <Chip
                    key={c.label}
                    label={`${c.label}: ${c.value}`}
                    size="small"
                    variant="outlined"
                  />
                )
            )
          ) : (
            <Chip label="No workspace selected" size="small" color="warning" />
          )}
        </Box>
        {current?.roles && current.roles.length > 1 && (
          <Typography variant="caption" color="text.secondary" sx={{ px: 0.5, display: 'block' }}>
            Other roles: {current.roles.slice(1).join(', ')}
          </Typography>
        )}

        <Divider sx={{ my: 1.5 }} />

        <Button
          variant="outlined"
          fullWidth
          size="small"
          startIcon={<Settings />}
          onClick={() => {
            setAnchorEl(null);
            navigate('/select-context');
          }}
          sx={{ textTransform: 'none' }}
        >
          Change Workspace
        </Button>
      </Menu>
    </Box>
  );
}
