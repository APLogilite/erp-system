import { ActionDefinition, ActionType } from '@/core/metadata/schema/action/ActionDefinition';
import { LayoutType } from '@/core/metadata/schema/layout/LayoutDefinition';
import { ModelDefinition } from '@/core/metadata/schema/model/ModelDefinition';
import {
  PermissionDefinition,
  PermissionType,
} from '@/core/metadata/schema/permission/PermissionDefinition';
import { RuntimeMetadataBundle } from '@/core/metadata/schema/RuntimeMetadataBundle';
import { ViewDefinition, ViewType } from '@/core/metadata/schema/view/ViewDefinition';

export const opportunityModel: ModelDefinition = {
  id: 'model_opp_001',
  code: 'opportunity',
  name: 'Opportunity',
  description: 'Sales opportunity',
  version: 1,
  active: true,
  tableName: 'opportunities',
  auditable: true,
  workflowEnabled: true,
  tenantAware: false,
  fields: [
    {
      id: 'field_opp_no',
      code: 'opportunityNumber',
      name: 'Opportunity Number',
      type: 'TEXT',
      required: true,
      readonly: true,
      searchable: true,
      version: 1,
      active: true,
    },
    {
      id: 'field_opp_bp',
      code: 'businessPartnerId',
      name: 'Business Partner',
      type: 'MANY_TO_ONE',
      required: true,
      filterable: true,
      version: 1,
      active: true,
    },
    {
      id: 'field_opp_stage',
      code: 'stage',
      name: 'Stage',
      type: 'SELECT',
      required: false,
      readonly: true,
      filterable: true,
      version: 1,
      active: true,
      defaultValue: 'OPEN',
      properties: {
        options: [
          { label: 'Open', value: 'OPEN' },
          { label: 'Proposal', value: 'PROPOSAL' },
          { label: 'Negotiation', value: 'NEGOTIATION' },
          { label: 'Won', value: 'WON' },
          { label: 'Lost', value: 'LOST' },
        ],
      },
    },
    {
      id: 'field_opp_prob',
      code: 'probability',
      name: 'Probability',
      type: 'DECIMAL',
      required: false,
      version: 1,
      active: true,
      defaultValue: 0,
    },
    {
      id: 'field_opp_rev',
      code: 'expectedRevenue',
      name: 'Expected Revenue',
      type: 'DECIMAL',
      required: false,
      version: 1,
      active: true,
      defaultValue: 0,
    },
    {
      id: 'field_opp_close',
      code: 'expectedCloseDate',
      name: 'Expected Close',
      type: 'DATE',
      required: false,
      version: 1,
      active: true,
    },
  ],
};

export const opportunityFormView: ViewDefinition = {
  id: 'view_opp_form',
  code: 'opportunity_form',
  modelCode: 'opportunity',
  viewType: ViewType.FORM,
  name: 'Opportunity Form',
  title: 'Opportunity Details',
  version: 1,
  active: true,
  layout: {
    type: LayoutType.PAGE,
    children: [
      {
        type: LayoutType.SECTION,
        config: { title: 'Opportunity Information' },
        children: [
          {
            type: LayoutType.ROW,
            children: [
              { type: LayoutType.COLUMN, config: { span: 3, fieldCode: 'opportunityNumber' } },
              { type: LayoutType.COLUMN, config: { span: 3, fieldCode: 'businessPartnerId' } },
              { type: LayoutType.COLUMN, config: { span: 3, fieldCode: 'stage' } },
            ],
          },
          {
            type: LayoutType.ROW,
            children: [
              { type: LayoutType.COLUMN, config: { span: 3, fieldCode: 'probability' } },
              { type: LayoutType.COLUMN, config: { span: 3, fieldCode: 'expectedRevenue' } },
              { type: LayoutType.COLUMN, config: { span: 3, fieldCode: 'expectedCloseDate' } },
            ],
          },
        ],
      },
    ],
  },
};

export const opportunityGridView: ViewDefinition = {
  id: 'view_opp_grid',
  code: 'opportunity_list',
  modelCode: 'opportunity',
  viewType: ViewType.GRID,
  name: 'Opportunity Grid',
  title: 'Opportunities',
  version: 1,
  active: true,
  layout: {
    type: LayoutType.GRID,
    config: {
      columns: [
        'opportunityNumber',
        'businessPartnerId',
        'stage',
        'probability',
        'expectedRevenue',
        'expectedCloseDate',
      ],
    },
  },
};

export const opportunityActions: ActionDefinition[] = [
  {
    id: 'act_opp_create',
    code: 'create_opportunity',
    name: 'Create',
    version: 1,
    active: true,
    actionType: ActionType.BUTTON,
    icon: 'add',
  },
  {
    id: 'act_opp_win',
    code: 'win',
    name: 'Win',
    version: 1,
    active: true,
    actionType: ActionType.CUSTOM,
    icon: 'check',
  },
  {
    id: 'act_opp_lose',
    code: 'lose',
    name: 'Lose',
    version: 1,
    active: true,
    actionType: ActionType.CUSTOM,
    icon: 'close',
  },
];

export const opportunityPermissions: PermissionDefinition[] = [
  {
    id: 'perm_opp_read',
    code: 'read_opportunity',
    name: 'Read Opportunity',
    version: 1,
    active: true,
    resource: 'opportunity',
    permissionType: PermissionType.MODULE,
  },
  {
    id: 'perm_opp_write',
    code: 'write_opportunity',
    name: 'Write Opportunity',
    version: 1,
    active: true,
    resource: 'opportunity',
    permissionType: PermissionType.MODULE,
  },
];

export const opportunityBundle: RuntimeMetadataBundle = {
  model: opportunityModel,
  views: [opportunityFormView, opportunityGridView],
  actions: opportunityActions,
  permissions: opportunityPermissions,
};
