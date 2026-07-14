import { useQuery } from '@tanstack/react-query';

import type { RecordEntry } from './useForm.types';

interface ListResponse {
  records: RecordEntry[];
  total: number;
  page: number;
  pageSize: number;
}

export function useRecordList(
  formCode: string,
  page: number,
  pageSize: number,
  sortField?: string,
  sortDir?: string,
  search?: string
) {
  return useQuery<ListResponse>({
    queryKey: ['runtime', 'records', formCode, { page, pageSize, sortField, sortDir, search }],
    queryFn: async () => {
      const params = new URLSearchParams();
      params.set('page', String(page));
      params.set('pageSize', String(pageSize));
      if (sortField) params.set('sortField', sortField);
      if (sortDir) params.set('sortDir', sortDir);
      if (search) params.set('search', search);

      const response = await fetch(
        `/api/runtime/forms/${encodeURIComponent(formCode)}/records?${params}`,
        {
          headers: { Authorization: `Bearer ${localStorage.getItem('token') ?? ''}` },
        }
      );
      if (!response.ok) throw new Error(`Failed to load records: ${response.statusText}`);
      const json = await response.json();
      return json.data as ListResponse;
    },
    placeholderData: (prev) => prev,
  });
}
