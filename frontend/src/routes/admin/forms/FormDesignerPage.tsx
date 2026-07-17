import { ArrowBack, Refresh } from '@mui/icons-material';
import {
  Box,
  Button,
  Card,
  CardContent,
  Chip,
  CircularProgress,
  IconButton,
  Tab,
  Tabs,
  Typography,
} from '@mui/material';
import { useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';

import { ErrorState } from '@/components/ui/ErrorState';
import { useAuthStore } from '@/core/auth/authStore';

import { FieldsTab } from '@/modules/admin/forms/components/FieldsTab';
import { GlobalFormTenantAccessTable } from '@/modules/admin/forms/components/GlobalFormTenantAccessTable';
import { LayoutTab } from '@/modules/admin/forms/components/LayoutTab';
import { RulesTab } from '@/modules/admin/forms/components/RulesTab';
import { SubFormsTab } from '@/modules/admin/forms/components/SubFormsTab';
import { ValidationTab } from '@/modules/admin/forms/components/ValidationTab';
import { useForm } from '@/modules/admin/forms/hooks/useFormDesigner';
import { useFormFields } from '@/modules/admin/forms/hooks/useFormFields';

export function FormDesignerPage() {
  const { formId } = useParams<{ formId: string }>();
  const navigate = useNavigate();
  const { data: form, isLoading, error, refetch } = useForm(formId);
  const { data: fields } = useFormFields(formId);
  const [tab, setTab] = useState(0);
  const user = useAuthStore((s) => s.user);
  const isSystemAdmin = user?.roles?.includes('SYSTEM_ADMIN') ?? false;
  const showTenantAccess = form?.scope === 'global' && isSystemAdmin;

  if (isLoading)
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', p: 8 }}>
        <CircularProgress />
      </Box>
    );
  if (error) return <ErrorState message={(error as Error).message} onRetry={refetch} />;
  if (!form) return null;

  const fieldOptions = (fields ?? []).map((f) => ({
    fieldId: f.id,
    columnCode: f.columnCode,
    label: f.labelOverride || f.columnCode,
  }));

  return (
    <Box sx={{ p: 3 }}>
      <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 1 }}>
        <Button
          startIcon={<ArrowBack />}
          onClick={() => navigate('/app/admin/forms')}
          sx={{ textTransform: 'none' }}
        >
          Back
        </Button>
        <Typography variant="h5" fontWeight={700}>
          {form.label || form.code}
        </Typography>
        <IconButton onClick={() => refetch()}>
          <Refresh />
        </IconButton>
      </Box>

      <Box sx={{ display: 'flex', gap: 1, mb: 3, flexWrap: 'wrap' }}>
        <Chip label={`Code: ${form.code}`} size="small" variant="outlined" />
        <Chip label={`Model: ${form.modelName}`} size="small" variant="outlined" />
        <Chip
          label={`Scope: ${form.scope}`}
          size="small"
          color={form.scope === 'global' ? 'primary' : 'secondary'}
          variant="outlined"
        />
      </Box>

      <Tabs value={tab} onChange={(_, v) => setTab(v)} sx={{ mb: 2 }} variant="scrollable">
        <Tab label="Fields" />
        <Tab label="Layout" />
        <Tab label="Rules" />
        <Tab label="Validations" />
        <Tab label="Sub-Forms" />
        {showTenantAccess && <Tab label="Tenant Access" />}
      </Tabs>

      <Card sx={{ borderRadius: 3 }}>
        <CardContent>
          {tab === 0 && formId && <FieldsTab formId={formId} />}
          {tab === 1 && formId && <LayoutTab formId={formId} />}
          {tab === 2 && formId && <RulesTab formId={formId} fields={fieldOptions} />}
          {tab === 3 && formId && <ValidationTab formId={formId} fields={fieldOptions} />}
          {tab === 4 && formId && <SubFormsTab formId={formId} />}
          {showTenantAccess && tab === 5 && formId && (
            <GlobalFormTenantAccessTable formId={formId} />
          )}
        </CardContent>
      </Card>
    </Box>
  );
}
