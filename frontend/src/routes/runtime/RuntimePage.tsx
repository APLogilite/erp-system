import { Box, CircularProgress, Typography } from '@mui/material';
import { useQuery } from '@tanstack/react-query';
import { useSearchParams } from 'react-router-dom';

import { PageContainer } from '@/components/layouts/PageContainer';
import { fetchFormBundle } from '@/core/runtime/api/runtimeApi';
import { RuntimeRenderer } from '@/runtime/renderer/RuntimeRenderer';

export function RuntimePage() {
  const [searchParams] = useSearchParams();
  const formCode = searchParams.get('form');

  const {
    data: bundle,
    isLoading,
    error,
  } = useQuery({
    queryKey: ['runtime-form-bundle', formCode],
    queryFn: () => fetchFormBundle(formCode!),
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

  if (error || !bundle) {
    return (
      <PageContainer title="Error" subtitle={`Could not load "${formCode}"`}>
        <Typography color="error">
          {error instanceof Error ? error.message : 'Failed to load form definition.'}
        </Typography>
      </PageContainer>
    );
  }

  const viewCode = `${formCode}_form`;

  return (
    <PageContainer
      title={(bundle.model as Record<string, unknown>)?.name as string ?? formCode}
    >
      <RuntimeRenderer metadataBundle={bundle as never} viewCode={viewCode} />
    </PageContainer>
  );
}
