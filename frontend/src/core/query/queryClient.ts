import { QueryClient } from '@tanstack/react-query';

import { queryConfig } from './queryConfig';

export const queryClient = new QueryClient({
  defaultOptions: queryConfig,
});
