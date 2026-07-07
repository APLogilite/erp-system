import {
  Business,
  CorporateFare,
  Store,
  AccountTree,
  Badge,
  ArrowForward,
} from '@mui/icons-material';
import {
  Box,
  Button,
  Card,
  CardContent,
  Typography,
  MenuItem,
  TextField,
  useTheme,
  Avatar,
  Alert,
  CircularProgress,
} from '@mui/material';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';

import { apiClient } from '@/core/api/client';
import { ENDPOINTS } from '@/core/api/endpoints';
import { useAuthStore } from '@/core/auth/authStore';

interface ContextOption {
  id: string;
  type: string;
  code: string;
  name: string;
  parentId?: string;
}

interface ContextOptionsResponse {
  tenants: ContextOption[];
  organizations: ContextOption[];
  companies: ContextOption[];
  branches: ContextOption[];
  departments: ContextOption[];
  roles: string[];
}

interface ContextProfile {
  tenantId: string;
  tenantName: string;
  organizationId: string;
  organizationName: string;
  companyId: string;
  companyName: string;
  branchId: string;
  branchName: string;
  roleCode: string;
}

export function ContextSelectPage() {
  const theme = useTheme();
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const user = useAuthStore((s) => s.user);

  const [selectedTenant, setSelectedTenant] = useState('');
  const [selectedOrg, setSelectedOrg] = useState('');
  const [selectedCompany, setSelectedCompany] = useState('');
  const [selectedBranch, setSelectedBranch] = useState('');
  const [selectedRole, setSelectedRole] = useState('');

  const {
    data: options,
    isLoading,
    isError,
  } = useQuery<ContextOptionsResponse>({
    queryKey: ['context', 'options-for-select'],
    queryFn: async () => {
      const res = await apiClient.get(ENDPOINTS.context.options);
      return res.data.data || res.data;
    },
    retry: 1,
  });

  const filteredOrgs = useMemo(() => {
    if (!options?.organizations || !selectedTenant) return [];
    return options.organizations.filter((o) => o.parentId === selectedTenant);
  }, [options, selectedTenant]);

  const filteredCompanies = useMemo(() => {
    if (!options?.companies || !selectedOrg) return [];
    return options.companies.filter((c) => c.parentId === selectedOrg);
  }, [options, selectedOrg]);

  const filteredBranches = useMemo(() => {
    if (!options?.branches || !selectedCompany) return [];
    return options.branches.filter((b) => b.parentId === selectedCompany);
  }, [options, selectedCompany]);

  // Auto-select single-option levels as they become available
  useEffect(() => {
    if (options?.tenants?.length === 1 && !selectedTenant) setSelectedTenant(options.tenants[0].id);
  }, [options?.tenants, selectedTenant]);

  useEffect(() => {
    if (filteredOrgs.length === 1 && !selectedOrg) setSelectedOrg(filteredOrgs[0].id);
  }, [filteredOrgs, selectedOrg]);

  useEffect(() => {
    if (filteredCompanies.length === 1 && !selectedCompany)
      setSelectedCompany(filteredCompanies[0].id);
  }, [filteredCompanies, selectedCompany]);

  useEffect(() => {
    if (filteredBranches.length === 1 && !selectedBranch) setSelectedBranch(filteredBranches[0].id);
  }, [filteredBranches, selectedBranch]);

  useEffect(() => {
    if (options?.roles?.length === 1 && !selectedRole) setSelectedRole(options.roles[0]);
  }, [options?.roles, selectedRole]);

  const profiles = useMemo(() => {
    if (!options) return [];
    const result: ContextProfile[] = [];
    const roles = options.roles ?? [];
    const branches = options.branches ?? [];
    const companies = options.companies ?? [];
    const organizations = options.organizations ?? [];
    const tenants = options.tenants ?? [];

    for (const role of roles) {
      for (const branch of branches) {
        const company = companies.find((c) => c.id === branch.parentId);
        if (!company) continue;
        const org = organizations.find((o) => o.id === company.parentId);
        if (!org) continue;
        const tenant = tenants.find((t) => t.id === org.parentId);
        if (!tenant) continue;

        result.push({
          tenantId: tenant.id,
          tenantName: tenant.name,
          organizationId: org.id,
          organizationName: org.name,
          companyId: company.id,
          companyName: company.name,
          branchId: branch.id,
          branchName: branch.name,
          roleCode: role,
        });
      }
    }
    return result;
  }, [options]);

  const switchMutation = useMutation({
    mutationFn: async (profile: ContextProfile) => {
      await apiClient.post(ENDPOINTS.context.switch, {
        tenantId: profile.tenantId,
        organizationId: profile.organizationId,
        companyId: profile.companyId,
        branchId: profile.branchId,
        roleCode: profile.roleCode,
      });
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['context'] });
      navigate('/app/dashboard', { replace: true });
    },
  });

  useEffect(() => {
    if (profiles.length === 1 && !switchMutation.isPending) {
      switchMutation.mutate(profiles[0]);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [profiles.length]);

  const cascadingSwitch = useMutation({
    mutationFn: async () => {
      await apiClient.post(ENDPOINTS.context.switch, {
        tenantId: selectedTenant || undefined,
        organizationId: selectedOrg || undefined,
        companyId: selectedCompany || undefined,
        branchId: selectedBranch || undefined,
        roleCode: selectedRole || undefined,
      });
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['context'] });
      navigate('/app/dashboard', { replace: true });
    },
  });

  const isLoadingAuto = profiles.length === 1 && switchMutation.isPending;

  if (isLoading || isLoadingAuto) {
    return (
      <Box
        sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', minHeight: '100vh' }}
      >
        <CircularProgress />
      </Box>
    );
  }

  if (isError) {
    return (
      <Box
        sx={{
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          minHeight: '100vh',
          p: 2,
        }}
      >
        <Card sx={{ maxWidth: 480, p: 3, borderRadius: 4 }}>
          <Alert severity="error" sx={{ mb: 2 }}>
            Failed to load workspace options. Please try refreshing.
          </Alert>
          <Button variant="contained" onClick={() => navigate('/app/dashboard', { replace: true })}>
            Go to Dashboard
          </Button>
        </Card>
      </Box>
    );
  }

  const missingLevels: string[] = [];
  if (!selectedTenant && (options?.tenants?.length ?? 0) > 0) missingLevels.push('Tenant');
  if (selectedTenant && filteredOrgs.length > 0 && !selectedOrg) missingLevels.push('Organization');
  if (selectedOrg && filteredCompanies.length > 0 && !selectedCompany)
    missingLevels.push('Company');
  if (selectedCompany && filteredBranches.length > 0 && !selectedBranch)
    missingLevels.push('Branch');
  if ((options?.roles?.length ?? 0) > 0 && !selectedRole) missingLevels.push('Role');

  const tenantSingle = (options?.tenants?.length ?? 0) === 1;
  const orgSingle = filteredOrgs.length === 1;
  const companySingle = filteredCompanies.length === 1;
  const branchSingle = filteredBranches.length === 1;
  const roleSingle = (options?.roles?.length ?? 0) === 1;

  return (
    <Box
      sx={{
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        minHeight: '100vh',
        background:
          theme.palette.mode === 'dark'
            ? 'radial-gradient(circle at 50% 50%, #151a30 0%, #0b0e1a 100%)'
            : 'radial-gradient(circle at 50% 50%, #f4f6fb 0%, #e2e8f0 100%)',
        p: 2,
      }}
    >
      <Card sx={{ width: '100%', maxWidth: 520, borderRadius: 4, overflow: 'hidden' }}>
        <Box sx={{ height: 6, background: 'linear-gradient(90deg, #1976d2 0%, #82b1ff 100%)' }} />
        <CardContent sx={{ p: 4 }}>
          <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', mb: 3 }}>
            <Avatar sx={{ width: 56, height: 56, bgcolor: 'primary.main', mb: 2, fontSize: 24 }}>
              {user?.displayName?.charAt(0)?.toUpperCase() ?? 'U'}
            </Avatar>
            <Typography variant="h5" component="h1" fontWeight={700} gutterBottom>
              Select Your Workspace
            </Typography>
            <Typography variant="body2" color="text.secondary" textAlign="center">
              Welcome, <strong>{user?.displayName ?? user?.username}</strong>. Choose where you want
              to work.
            </Typography>
          </Box>

          {(cascadingSwitch.isError || switchMutation.isError) && (
            <Alert severity="error" sx={{ mb: 3, borderRadius: 2 }}>
              Failed to set workspace. Please try again.
            </Alert>
          )}

          {missingLevels.length > 0 && (
            <Alert severity="warning" sx={{ mb: 3, borderRadius: 2 }}>
              Please select: <strong>{missingLevels.join(', ')}</strong>
            </Alert>
          )}

          <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2.5, mb: 3 }}>
            <SelectField
              label="Tenant"
              icon={<Business fontSize="small" color="action" />}
              value={selectedTenant}
              onChange={setSelectedTenant}
              options={options?.tenants ?? []}
              disabled={tenantSingle && !!selectedTenant}
            />

            {selectedTenant && filteredOrgs.length > 0 && (
              <SelectField
                label="Organization"
                icon={<CorporateFare fontSize="small" color="action" />}
                value={selectedOrg}
                onChange={(v) => {
                  setSelectedOrg(v);
                  setSelectedCompany('');
                  setSelectedBranch('');
                }}
                options={filteredOrgs}
                disabled={orgSingle && !!selectedOrg}
              />
            )}

            {selectedOrg && filteredCompanies.length > 0 && (
              <SelectField
                label="Company"
                icon={<Store fontSize="small" color="action" />}
                value={selectedCompany}
                onChange={(v) => {
                  setSelectedCompany(v);
                  setSelectedBranch('');
                }}
                options={filteredCompanies}
                disabled={companySingle && !!selectedCompany}
              />
            )}

            {selectedCompany && filteredBranches.length > 0 && (
              <SelectField
                label="Branch"
                icon={<AccountTree fontSize="small" color="action" />}
                value={selectedBranch}
                onChange={setSelectedBranch}
                options={filteredBranches}
                disabled={branchSingle && !!selectedBranch}
              />
            )}

            {options?.roles && options.roles.length > 0 && (
              <SelectField
                label="Role"
                icon={<Badge fontSize="small" color="action" />}
                value={selectedRole}
                onChange={setSelectedRole}
                options={options.roles.map((r) => ({ id: r, type: 'role', code: r, name: r }))}
                disabled={roleSingle && !!selectedRole}
              />
            )}
          </Box>

          <Box sx={{ display: 'flex', justifyContent: 'flex-end' }}>
            <Button
              variant="contained"
              size="large"
              onClick={() => cascadingSwitch.mutate()}
              disabled={cascadingSwitch.isPending || !selectedTenant || missingLevels.length > 0}
              endIcon={
                cascadingSwitch.isPending ? (
                  <CircularProgress size={18} color="inherit" />
                ) : (
                  <ArrowForward />
                )
              }
              sx={{ py: 1.2, px: 4, borderRadius: 2, fontWeight: 600, textTransform: 'none' }}
            >
              {cascadingSwitch.isPending ? 'Setting workspace...' : 'Enter Workspace'}
            </Button>
          </Box>
        </CardContent>
      </Card>
    </Box>
  );
}

function SelectField({
  label,
  icon,
  value,
  onChange,
  options,
  disabled,
}: {
  label: string;
  icon: React.ReactNode;
  value: string;
  onChange: (v: string) => void;
  options: { id: string; name: string }[];
  disabled?: boolean;
}) {
  return (
    <Box>
      <Typography
        variant="subtitle2"
        sx={{ mb: 0.5, display: 'flex', alignItems: 'center', gap: 0.5 }}
      >
        {icon} {label}
      </Typography>
      <TextField
        select
        fullWidth
        value={value}
        onChange={(e) => onChange(e.target.value)}
        disabled={disabled}
      >
        {options.map((opt) => (
          <MenuItem key={opt.id} value={opt.id}>
            {opt.name}
          </MenuItem>
        ))}
      </TextField>
    </Box>
  );
}
