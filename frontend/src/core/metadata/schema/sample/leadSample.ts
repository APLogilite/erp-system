import { ActionDefinition, ActionType } from '@/core/metadata/schema/action/ActionDefinition';
import { LayoutType } from '@/core/metadata/schema/layout/LayoutDefinition';
import { ModelDefinition } from '@/core/metadata/schema/model/ModelDefinition';
import {
  PermissionDefinition,
  PermissionType,
} from '@/core/metadata/schema/permission/PermissionDefinition';
import { RuntimeMetadataBundle } from '@/core/metadata/schema/RuntimeMetadataBundle';
import { ViewDefinition, ViewType } from '@/core/metadata/schema/view/ViewDefinition';

export const leadModel: ModelDefinition = {
  id: 'model_lead_001',
  code: 'lead',
  name: 'Lead',
  description: 'Customer acquisition record',
  version: 1,
  active: true,
  tableName: 'leads',
  auditable: true,
  workflowEnabled: true,
  tenantAware: false,
  fields: [
    {
      id: 'field_lead_no',
      code: 'leadNumber',
      name: 'Lead Number',
      type: 'TEXT',
      required: true,
      readonly: true,
      searchable: true,
      version: 1,
      active: true,
    },
    {
      id: 'field_lead_co',
      code: 'company',
      name: 'Company',
      type: 'TEXT',
      required: true,
      searchable: true,
      filterable: true,
      version: 1,
      active: true,
    },
    {
      id: 'field_lead_cn',
      code: 'contactName',
      name: 'Contact Name',
      type: 'TEXT',
      required: true,
      searchable: true,
      version: 1,
      active: true,
    },
    {
      id: 'field_lead_em',
      code: 'email',
      name: 'Email',
      type: 'TEXT',
      required: false,
      version: 1,
      active: true,
    },
    {
      id: 'field_lead_ph',
      code: 'phone',
      name: 'Phone',
      type: 'TEXT',
      required: false,
      version: 1,
      active: true,
    },
    {
      id: 'field_lead_src',
      code: 'source',
      name: 'Source',
      type: 'SELECT',
      required: false,
      version: 1,
      active: true,
      properties: {
        options: [
          { label: 'Website', value: 'WEBSITE' },
          { label: 'Referral', value: 'REFERRAL' },
          { label: 'Cold Call', value: 'COLD_CALL' },
          { label: 'Event', value: 'EVENT' },
          { label: 'Other', value: 'OTHER' },
        ],
      },
    },
    {
      id: 'field_lead_st',
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
          { label: 'Qualified', value: 'QUALIFIED' },
          { label: 'Converted', value: 'CONVERTED' },
          { label: 'Closed', value: 'CLOSED' },
        ],
      },
    },
    {
      id: 'field_lead_ev',
      code: 'expectedValue',
      name: 'Expected Value',
      type: 'DECIMAL',
      required: false,
      version: 1,
      active: true,
      defaultValue: 0,
    },
  ],
};

export const leadFormView: ViewDefinition = {
  id: 'view_lead_form',
  code: 'lead_form',
  modelCode: 'lead',
  viewType: ViewType.FORM,
  name: 'Lead Form',
  title: 'Lead Details',
  version: 1,
  active: true,
  layout: {
    type: LayoutType.PAGE,
    children: [
      {
        type: LayoutType.SECTION,
        config: { title: 'Contact Information' },
        children: [
          {
            type: LayoutType.ROW,
            children: [
              { type: LayoutType.COLUMN, config: { span: 3, fieldCode: 'leadNumber' } },
              { type: LayoutType.COLUMN, config: { span: 3, fieldCode: 'company' } },
              { type: LayoutType.COLUMN, config: { span: 3, fieldCode: 'contactName' } },
              { type: LayoutType.COLUMN, config: { span: 3, fieldCode: 'status' } },
            ],
          },
          {
            type: LayoutType.ROW,
            children: [
              { type: LayoutType.COLUMN, config: { span: 3, fieldCode: 'email' } },
              { type: LayoutType.COLUMN, config: { span: 3, fieldCode: 'phone' } },
              { type: LayoutType.COLUMN, config: { span: 3, fieldCode: 'source' } },
              { type: LayoutType.COLUMN, config: { span: 3, fieldCode: 'expectedValue' } },
            ],
          },
        ],
      },
    ],
  },
};

export const leadGridView: ViewDefinition = {
  id: 'view_lead_grid',
  code: 'lead_list',
  modelCode: 'lead',
  viewType: ViewType.GRID,
  name: 'Lead Grid',
  title: 'Leads',
  version: 1,
  active: true,
  layout: {
    type: LayoutType.GRID,
    config: {
      columns: [
        'leadNumber',
        'company',
        'contactName',
        'email',
        'source',
        'status',
        'expectedValue',
      ],
    },
  },
};

export const leadActions: ActionDefinition[] = [
  {
    id: 'act_lead_create',
    code: 'create_lead',
    name: 'Create',
    version: 1,
    active: true,
    actionType: ActionType.BUTTON,
    icon: 'add',
  },
  {
    id: 'act_lead_qualify',
    code: 'qualify',
    name: 'Qualify',
    version: 1,
    active: true,
    actionType: ActionType.CUSTOM,
    icon: 'check',
  },
  {
    id: 'act_lead_convert',
    code: 'convert',
    name: 'Convert',
    version: 1,
    active: true,
    actionType: ActionType.CUSTOM,
    icon: 'swap',
  },
];

export const leadPermissions: PermissionDefinition[] = [
  {
    id: 'perm_lead_read',
    code: 'read_lead',
    name: 'Read Lead',
    version: 1,
    active: true,
    resource: 'lead',
    permissionType: PermissionType.MODULE,
  },
  {
    id: 'perm_lead_write',
    code: 'write_lead',
    name: 'Write Lead',
    version: 1,
    active: true,
    resource: 'lead',
    permissionType: PermissionType.MODULE,
  },
];

export const leadBundle: RuntimeMetadataBundle = {
  model: leadModel,
  views: [leadFormView, leadGridView],
  actions: leadActions,
  permissions: leadPermissions,
};
