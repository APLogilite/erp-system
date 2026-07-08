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

import { FieldsTab } from './components/FieldsTab';
import { LayoutTab } from './components/LayoutTab';
import { useForm } from './hooks/useFormDesigner';

export function FormDesignerPage() {
  const { formId } = useParams<{ formId: string }>();
  const navigate = useNavigate();
  const { data: form, isLoading, error, refetch } = useForm(formId);
  const [tab, setTab] = useState(0);

  if (isLoading) return <Box sx={{ display: 'flex', justifyContent: 'center', p: 8 }}><CircularProgress /></Box>;
  if (error) return <ErrorState message={(error as Error).message} onRetry={refetch} />;
  if (!form) return null;

  return (
    <Box sx={{ p: 3 }}>
      <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 1 }}>
        <Button startIcon={<ArrowBack />} onClick={() => navigate('/app/admin/forms')}
          sx={{ textTransform: 'none' }}>Back</Button>
        <Typography variant="h5" fontWeight={700}>{form.label || form.code}</Typography>
        <IconButton onClick={() => refetch()}><Refresh /></IconButton>
      </Box>

      <Box sx={{ display: 'flex', gap: 1, mb: 3, flexWrap: 'wrap' }}>
        <Chip label={`Code: ${form.code}`} size="small" variant="outlined" />
        <Chip label={`Model: ${form.modelName}`} size="small" variant="outlined" />
        <Chip label={`Scope: ${form.scope}`} size="small"
          color={form.scope === 'global' ? 'primary' : 'secondary'} variant="outlined" />
      </Box>

      <Tabs value={tab} onChange={(_, v) => setTab(v)} sx={{ mb: 2 }}>
        <Tab label="Fields" />
        <Tab label="Layout" />
      </Tabs>

      <Card sx={{ borderRadius: 3 }}>
        <CardContent>
          {tab === 0 && formId && <FieldsTab formId={formId} />}
          {tab === 1 && formId && <LayoutTab formId={formId} />}
        </CardContent>
      </Card>
    </Box>
  );
}
