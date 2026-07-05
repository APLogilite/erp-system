import { SwapHoriz, Business, CorporateFare, Store, AccountTree } from '@mui/icons-material';
import {
  Box,
  Chip,
  Menu,
  MenuItem,
  ListItemIcon,
  ListItemText,
  Typography,
  IconButton,
  Divider,
  Tooltip,
} from '@mui/material';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useState } from 'react';

import { apiClient } from '@/core/api/client';
import { ENDPOINTS } from '@/core/api/endpoints';

interface ContextOption {
  type: 'TENANT' | 'ORGANIZATION' | 'COMPANY' | 'BRANCH' | 'DEPARTMENT';
  id: string;
  code: string;
  name: string;
}

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
}

const iconMap: Record<string, React.ElementType> = {
  TENANT: Business,
  ORGANIZATION: CorporateFare,
  COMPANY: Store,
  BRANCH: AccountTree,
  DEPARTMENT: AccountTree,
};

export function ContextSwitcher() {
  const [anchorEl, setAnchorEl] = useState<HTMLElement | null>(null);
  const [contextType, setContextType] = useState<string | null>(null);
  const queryClient = useQueryClient();

  const { data: current } = useQuery<ContextCurrent>({
    queryKey: ['context', 'current'],
    queryFn: async () => {
      const res = await apiClient.get(ENDPOINTS.context.current);
      return res.data.data || res.data;
    },
  });

  const { data: options } = useQuery<ContextOption[]>({
    queryKey: ['context', 'options'],
    queryFn: async () => {
      const res = await apiClient.get(ENDPOINTS.context.options);
      return res.data.data || res.data;
    },
    enabled: !!anchorEl,
  });

  const switchMutation = useMutation({
    mutationFn: async (params: { type: string; id: string }) => {
      await apiClient.post(ENDPOINTS.context.switch, params);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['context'] });
      setAnchorEl(null);
    },
  });

  const activeContexts: { label: string; value?: string; type: string }[] = [
    { label: 'Tenant', value: current?.tenantName || current?.tenantCode, type: 'TENANT' },
    {
      label: 'Org',
      value: current?.organizationName || current?.organizationCode,
      type: 'ORGANIZATION',
    },
    { label: 'Company', value: current?.companyName || current?.companyCode, type: 'COMPANY' },
    { label: 'Branch', value: current?.branchName || current?.branchCode, type: 'BRANCH' },
    {
      label: 'Dept',
      value: current?.departmentName || current?.departmentCode,
      type: 'DEPARTMENT',
    },
  ];

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
        onClose={() => {
          setAnchorEl(null);
          setContextType(null);
        }}
        PaperProps={{ sx: { width: 320, maxHeight: 400, p: 1 } }}
      >
        <Typography variant="subtitle2" sx={{ px: 1, mb: 1, color: 'text.secondary' }}>
          Current Context
        </Typography>
        <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 0.5, px: 1, mb: 1 }}>
          {activeContexts.map((c) =>
            c.value ? (
              <Chip
                key={c.type}
                label={`${c.label}: ${c.value}`}
                size="small"
                variant="outlined"
                onClick={() => setContextType(contextType === c.type ? null : c.type)}
              />
            ) : null
          )}
        </Box>

        {contextType && (
          <>
            <Divider sx={{ my: 1 }} />
            <Typography variant="caption" sx={{ px: 1, color: 'text.secondary' }}>
              Select {contextType.toLowerCase()}
            </Typography>
            {options
              ?.filter((o) => o.type === contextType)
              .map((opt) => {
                const Icon = iconMap[opt.type] || Business;
                return (
                  <MenuItem
                    key={opt.id}
                    dense
                    onClick={() => switchMutation.mutate({ type: opt.type, id: opt.id })}
                  >
                    <ListItemIcon>
                      <Icon fontSize="small" />
                    </ListItemIcon>
                    <ListItemText primary={opt.name} secondary={opt.code} />
                  </MenuItem>
                );
              })}
          </>
        )}
      </Menu>
    </Box>
  );
}
