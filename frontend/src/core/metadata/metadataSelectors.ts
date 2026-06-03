import { MetadataStore, ModelMetadata, ViewMetadata, WorkflowMetadata } from './metadataTypes';

export const selectIsMetadataInitialized = (state: MetadataStore): boolean => state.isInitialized;

export const selectModelMetadata =
  (modelName: string) =>
  (state: MetadataStore): ModelMetadata | undefined =>
    state.models[modelName];

export const selectViewMetadata =
  (viewId: string) =>
  (state: MetadataStore): ViewMetadata | undefined =>
    state.views[viewId];

export const selectWorkflowMetadata =
  (modelName: string) =>
  (state: MetadataStore): WorkflowMetadata | undefined =>
    state.workflows[modelName];

export const selectAllModels = (state: MetadataStore): ModelMetadata[] =>
  Object.values(state.models);
