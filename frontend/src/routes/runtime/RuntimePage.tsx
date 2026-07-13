import { Box, CircularProgress, Typography } from '@mui/material';
import { useQuery } from '@tanstack/react-query';
import { useSearchParams } from 'react-router-dom';

import { PageContainer } from '@/components/layouts/PageContainer';
import { fetchFormDefinition } from '@/core/runtime/api/runtimeApi';
import { formDefinitionToBundle } from '@/core/runtime/api/formToBundleMapper';
import { RuntimeRenderer } from '@/runtime/renderer/RuntimeRenderer';

export function RuntimePage() {
  const [searchParams] = useSearchParams();
  const formCode = searchParams.get('form');

  const {
    data: formDefinition,
    isLoading,
    error,
  } = useQuery({
    queryKey: ['runtime-form-definition', formCode],
    queryFn: () => fetchFormDefinition(formCode!),
    enabled: !!formCode,
  });

  if (!formCode) {
    return (
      <PageContainer title="Dynamic Form" subtitle="No form selected">
        <Typography color="text.secondary">
          Select a form from the sidebar or use Ctrl+K to search.
        </Typography>
      </PageContainer>
    );
  }

  if (isLoading) {
    return (
      <PageContainer title="Loading..." subtitle={`Loading ${formCode}...`}>
        <Box sx={{ display: 'flex', justifyContent: 'center', py: 8 }}>
          <CircularProgress />
        </Box>
      </PageContainer>
    );
  }

  if (error || !formDefinition) {
    return (
      <PageContainer title="Error" subtitle={`Could not load "${formCode}"`}>
        <Typography color="error">
          {error instanceof Error ? error.message : 'Failed to load form definition.'}
        </Typography>
      </PageContainer>
    );
  }

  const bundle = formDefinitionToBundle(formDefinition);
  const viewCode = `${formCode}_form`;

  return (
    <PageContainer
      title={formDefinition.formLabel ?? formCode}
      subtitle={formDefinition.modelLabel ?? formDefinition.modelName}
    >
      <RuntimeRenderer metadataBundle={bundle} viewCode={viewCode} />
    </PageContainer>
  );
}
