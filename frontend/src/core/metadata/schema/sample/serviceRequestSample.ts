import { ActionDefinition, ActionType } from '@/core/metadata/schema/action/ActionDefinition';
import { LayoutType } from '@/core/metadata/schema/layout/LayoutDefinition';
import { ModelDefinition } from '@/core/metadata/schema/model/ModelDefinition';
import {
  PermissionDefinition,
  PermissionType,
} from '@/core/metadata/schema/permission/PermissionDefinition';
import { RuntimeMetadataBundle } from '@/core/metadata/schema/RuntimeMetadataBundle';
import { ViewDefinition, ViewType } from '@/core/metadata/schema/view/ViewDefinition';

export const serviceRequestModel: ModelDefinition = {
  id: 'model_sr_001',
  code: 'service_request',
  name: 'Service Request',
  description: 'Customer support ticket',
  version: 1,
  active: true,
  tableName: 'service_requests',
  auditable: true,
  workflowEnabled: true,
  tenantAware: false,
  fields: [
    {
      id: 'field_sr_ticket',
      code: 'ticketNumber',
      name: 'Ticket Number',
      type: 'TEXT',
      required: true,
      readonly: true,
      searchable: true,
      version: 1,
      active: true,
    },
    {
      id: 'field_sr_cust',
      code: 'customerId',
      name: 'Customer',
      type: 'MANY_TO_ONE',
      required: true,
      filterable: true,
      version: 1,
      active: true,
    },
    {
      id: 'field_sr_pri',
      code: 'priority',
      name: 'Priority',
      type: 'SELECT',
      required: false,
      filterable: true,
      version: 1,
      active: true,
      defaultValue: 'MEDIUM',
      properties: {
        options: [
          { label: 'Low', value: 'LOW' },
          { label: 'Medium', value: 'MEDIUM' },
          { label: 'High', value: 'HIGH' },
          { label: 'Critical', value: 'CRITICAL' },
        ],
      },
    },
    {
      id: 'field_sr_cat',
      code: 'category',
      name: 'Category',
      type: 'SELECT',
      required: false,
      version: 1,
      active: true,
      properties: {
        options: [
          { label: 'Hardware', value: 'HARDWARE' },
          { label: 'Software', value: 'SOFTWARE' },
          { label: 'Network', value: 'NETWORK' },
          { label: 'Other', value: 'OTHER' },
        ],
      },
    },
    {
      id: 'field_sr_eng',
      code: 'assignedEngineerId',
      name: 'Engineer',
      type: 'MANY_TO_ONE',
      required: false,
      version: 1,
      active: true,
    },
    {
      id: 'field_sr_st',
      code: 'status',
      name: 'Status',
      type: 'SELECT',
      required: false,
      readonly: true,
      filterable: true,
      version: 1,
      active: true,
      defaultValue: 'NEW',
      properties: {
        options: [
          { label: 'New', value: 'NEW' },
          { label: 'Assigned', value: 'ASSIGNED' },
          { label: 'In Progress', value: 'IN_PROGRESS' },
          { label: 'Resolved', value: 'RESOLVED' },
          { label: 'Closed', value: 'CLOSED' },
        ],
      },
    },
    {
      id: 'field_sr_desc',
      code: 'description',
      name: 'Description',
      type: 'TEXT',
      required: false,
      version: 1,
      active: true,
    },
  ],
};

export const serviceRequestFormView: ViewDefinition = {
  id: 'view_sr_form',
  code: 'service_request_form',
  modelCode: 'service_request',
  viewType: ViewType.FORM,
  name: 'Service Request Form',
  title: 'Ticket Details',
  version: 1,
  active: true,
  layout: {
    type: LayoutType.PAGE,
    children: [
      {
        type: LayoutType.SECTION,
        config: { title: 'Ticket Information' },
        children: [
          {
            type: LayoutType.ROW,
            children: [
              { type: LayoutType.COLUMN, config: { span: 3, fieldCode: 'ticketNumber' } },
              { type: LayoutType.COLUMN, config: { span: 3, fieldCode: 'customerId' } },
              { type: LayoutType.COLUMN, config: { span: 3, fieldCode: 'status' } },
              { type: LayoutType.COLUMN, config: { span: 3, fieldCode: 'priority' } },
            ],
          },
          {
            type: LayoutType.ROW,
            children: [
              { type: LayoutType.COLUMN, config: { span: 4, fieldCode: 'category' } },
              { type: LayoutType.COLUMN, config: { span: 4, fieldCode: 'assignedEngineerId' } },
            ],
          },
          {
            type: LayoutType.ROW,
            children: [{ type: LayoutType.COLUMN, config: { span: 12, fieldCode: 'description' } }],
          },
        ],
      },
    ],
  },
};

export const serviceRequestGridView: ViewDefinition = {
  id: 'view_sr_grid',
  code: 'service_request_list',
  modelCode: 'service_request',
  viewType: ViewType.GRID,
  name: 'Service Request Grid',
  title: 'Service Requests',
  version: 1,
  active: true,
  layout: {
    type: LayoutType.GRID,
    config: {
      columns: [
        'ticketNumber',
        'customerId',
        'priority',
        'category',
        'assignedEngineerId',
        'status',
      ],
    },
  },
};

export const serviceRequestActions: ActionDefinition[] = [
  {
    id: 'act_sr_create',
    code: 'create_ticket',
    name: 'Create',
    version: 1,
    active: true,
    actionType: ActionType.BUTTON,
    icon: 'add',
  },
  {
    id: 'act_sr_assign',
    code: 'assign',
    name: 'Assign',
    version: 1,
    active: true,
    actionType: ActionType.CUSTOM,
    icon: 'user',
  },
  {
    id: 'act_sr_start',
    code: 'start',
    name: 'Start',
    version: 1,
    active: true,
    actionType: ActionType.CUSTOM,
    icon: 'play',
  },
  {
    id: 'act_sr_resolve',
    code: 'resolve',
    name: 'Resolve',
    version: 1,
    active: true,
    actionType: ActionType.CUSTOM,
    icon: 'check',
  },
];

export const serviceRequestPermissions: PermissionDefinition[] = [
  {
    id: 'perm_sr_read',
    code: 'read_sr',
    name: 'Read Service Request',
    version: 1,
    active: true,
    resource: 'service_request',
    permissionType: PermissionType.MODULE,
  },
  {
    id: 'perm_sr_write',
    code: 'write_sr',
    name: 'Write Service Request',
    version: 1,
    active: true,
    resource: 'service_request',
    permissionType: PermissionType.MODULE,
  },
];

export const serviceRequestBundle: RuntimeMetadataBundle = {
  model: serviceRequestModel,
  views: [serviceRequestFormView, serviceRequestGridView],
  actions: serviceRequestActions,
  permissions: serviceRequestPermissions,
};
