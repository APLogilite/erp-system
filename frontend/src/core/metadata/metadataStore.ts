import { create } from 'zustand';

import { MetadataStore } from './metadataTypes';

export const useMetadataStore = create<MetadataStore>((set) => ({
  models: {},
  views: {},
  layouts: {},
  workflows: {},
  permissions: {},
  isInitialized: false,

  setMetadata: ({ models, views, workflows }) =>
    set((state) => {
      const modelsMap = { ...state.models };
      const viewsMap = { ...state.views };
      const workflowsMap = { ...state.workflows };

      models?.forEach((m) => {
        modelsMap[m.name] = m;
      });

      views?.forEach((v) => {
        viewsMap[v.id] = v;
      });

      workflows?.forEach((w) => {
        workflowsMap[w.model] = w;
      });

      return {
        models: modelsMap,
        views: viewsMap,
        workflows: workflowsMap,
        isInitialized: true,
      };
    }),

  registerModel: (model) =>
    set((state) => ({
      models: { ...state.models, [model.name]: model },
    })),

  registerView: (view) =>
    set((state) => ({
      views: { ...state.views, [view.id]: view },
    })),

  registerWorkflow: (workflow) =>
    set((state) => ({
      workflows: { ...state.workflows, [workflow.model]: workflow },
    })),

  clearMetadata: () =>
    set({
      models: {},
      views: {},
      layouts: {},
      workflows: {},
      permissions: {},
      isInitialized: false,
    }),
}));
