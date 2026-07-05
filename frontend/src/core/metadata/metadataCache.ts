import { metadataActions } from './metadataActions';

import { queryClient } from '@/core/query/queryClient';
import { queryKeys } from '@/core/query/queryKeys';

export const metadataCache = {
  invalidateAll: async (): Promise<void> => {
    await queryClient.invalidateQueries({ queryKey: queryKeys.metadata.all });
    metadataActions.clearMetadata();
  },

  invalidateModels: async (): Promise<void> => {
    await queryClient.invalidateQueries({ queryKey: queryKeys.metadata.models() });
  },

  invalidateViews: async (): Promise<void> => {
    await queryClient.invalidateQueries({ queryKey: queryKeys.metadata.views() });
  },
};
