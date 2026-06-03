import { useMetadataStore } from './metadataStore';
import { ModelMetadata, ViewMetadata, WorkflowMetadata } from './metadataTypes';

export const metadataActions = {
  setMetadata: (payload: {
    models?: ModelMetadata[];
    views?: ViewMetadata[];
    workflows?: WorkflowMetadata[];
  }) => useMetadataStore.getState().setMetadata(payload),
  registerModel: (model: ModelMetadata) => useMetadataStore.getState().registerModel(model),
  registerView: (view: ViewMetadata) => useMetadataStore.getState().registerView(view),
  registerWorkflow: (workflow: WorkflowMetadata) =>
    useMetadataStore.getState().registerWorkflow(workflow),
  clearMetadata: () => useMetadataStore.getState().clearMetadata(),
};
