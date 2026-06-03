import { eventBus } from './eventBus';

export const runtimeEvents = {
  emitFormChanged: (formId: string, data: Record<string, unknown>) => {
    eventBus.publish('FORM_CHANGED', { formId, data });
  },

  emitWorkflowTransitioned: (workflowId: string, fromState: string, toState: string) => {
    eventBus.publish('WORKFLOW_TRANSITIONED', { workflowId, fromState, toState });
  },

  emitMetadataUpdated: () => {
    eventBus.publish('METADATA_UPDATED', {});
  },

  emitRelationSelected: (relationModel: string, recordId: string | number) => {
    eventBus.publish('RELATION_SELECTED', { relationModel, recordId });
  },
};
