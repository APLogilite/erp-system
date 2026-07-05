import { apiClient } from '../client';
import { ENDPOINTS } from '../endpoints';

import { ModelMetadata, ViewMetadata, WorkflowMetadata } from '@/core/metadata/metadataTypes';

export const metadataService = {
  getModels: async (): Promise<ModelMetadata[]> => {
    const response = await apiClient.get<ModelMetadata[]>(ENDPOINTS.metadata.models);
    return response.data;
  },

  getViews: async (): Promise<ViewMetadata[]> => {
    const response = await apiClient.get<ViewMetadata[]>(ENDPOINTS.metadata.views);
    return response.data;
  },

  getWorkflows: async (): Promise<WorkflowMetadata[]> => {
    const response = await apiClient.get<WorkflowMetadata[]>(ENDPOINTS.metadata.workflows);
    return response.data;
  },
};
