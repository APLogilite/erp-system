import { ActionDefinition, ActionType } from '@/core/metadata/schema/action/ActionDefinition';
import { LayoutType } from '@/core/metadata/schema/layout/LayoutDefinition';
import { ModelDefinition } from '@/core/metadata/schema/model/ModelDefinition';
import {
  PermissionDefinition,
  PermissionType,
} from '@/core/metadata/schema/permission/PermissionDefinition';
import { RuntimeMetadataBundle } from '@/core/metadata/schema/RuntimeMetadataBundle';
import { ViewDefinition, ViewType } from '@/core/metadata/schema/view/ViewDefinition';

export const chartOfAccountsModel: ModelDefinition = {
  id: 'model_coa_001',
  code: 'chart_of_accounts',
  name: 'Chart of Accounts',
  description: 'Financial account definitions',
  version: 1,
  active: true,
  tableName: 'accounts',
  auditable: true,
  workflowEnabled: true,
  tenantAware: false,
  fields: [
    {
      id: 'field_coa_code',
      code: 'accountCode',
      name: 'Account Code',
      type: 'TEXT',
      required: true,
      searchable: true,
      filterable: true,
      version: 1,
      active: true,
    },
    {
      id: 'field_coa_name',
      code: 'name',
      name: 'Name',
      type: 'TEXT',
      required: true,
      searchable: true,
      filterable: true,
      version: 1,
      active: true,
    },
    {
      id: 'field_coa_desc',
      code: 'description',
      name: 'Description',
      type: 'TEXT',
      required: false,
      version: 1,
      active: true,
    },
    {
      id: 'field_coa_type',
      code: 'accountType',
      name: 'Account Type',
      type: 'SELECT',
      required: true,
      filterable: true,
      version: 1,
      active: true,
      defaultValue: 'ASSET',
      properties: {
        options: [
          { label: 'Asset', value: 'ASSET' },
          { label: 'Liability', value: 'LIABILITY' },
          { label: 'Equity', value: 'EQUITY' },
          { label: 'Revenue', value: 'REVENUE' },
          { label: 'Expense', value: 'EXPENSE' },
        ],
      },
    },
    {
      id: 'field_coa_parent',
      code: 'parentId',
      name: 'Parent Account',
      type: 'MANY_TO_ONE',
      required: false,
      version: 1,
      active: true,
    },
    {
      id: 'field_coa_currency',
      code: 'currency',
      name: 'Currency',
      type: 'TEXT',
      required: false,
      version: 1,
      active: true,
      defaultValue: 'USD',
    },
    {
      id: 'field_coa_control',
      code: 'isControlAccount',
      name: 'Control Account',
      type: 'BOOLEAN',
      required: false,
      version: 1,
      active: true,
      defaultValue: false,
    },
  ],
};

export const chartOfAccountsFormView: ViewDefinition = {
  id: 'view_coa_form',
  code: 'chart_of_accounts_form',
  modelCode: 'chart_of_accounts',
  viewType: ViewType.FORM,
  name: 'Account Form',
  title: 'Account Details',
  version: 1,
  active: true,
  layout: {
    type: LayoutType.PAGE,
    children: [
      {
        type: LayoutType.SECTION,
        config: { title: 'Account Information' },
        children: [
          {
            type: LayoutType.ROW,
            children: [
              { type: LayoutType.COLUMN, config: { span: 4, fieldCode: 'accountCode' } },
              { type: LayoutType.COLUMN, config: { span: 4, fieldCode: 'name' } },
              { type: LayoutType.COLUMN, config: { span: 4, fieldCode: 'accountType' } },
            ],
          },
          {
            type: LayoutType.ROW,
            children: [
              { type: LayoutType.COLUMN, config: { span: 6, fieldCode: 'parentId' } },
              { type: LayoutType.COLUMN, config: { span: 3, fieldCode: 'currency' } },
              { type: LayoutType.COLUMN, config: { span: 3, fieldCode: 'isControlAccount' } },
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

export const chartOfAccountsGridView: ViewDefinition = {
  id: 'view_coa_grid',
  code: 'chart_of_accounts_list',
  modelCode: 'chart_of_accounts',
  viewType: ViewType.GRID,
  name: 'Chart of Accounts Grid',
  title: 'Chart of Accounts',
  version: 1,
  active: true,
  layout: {
    type: LayoutType.GRID,
    config: {
      columns: ['accountCode', 'name', 'accountType', 'parentId', 'currency', 'isControlAccount'],
    },
  },
};

export const chartOfAccountsActions: ActionDefinition[] = [
  {
    id: 'act_coa_create',
    code: 'create_account',
    name: 'Create',
    version: 1,
    active: true,
    actionType: ActionType.BUTTON,
    icon: 'add',
  },
];

export const chartOfAccountsPermissions: PermissionDefinition[] = [
  {
    id: 'perm_coa_read',
    code: 'read_coa',
    name: 'Read Chart of Accounts',
    version: 1,
    active: true,
    resource: 'chart_of_accounts',
    permissionType: PermissionType.MODULE,
  },
  {
    id: 'perm_coa_write',
    code: 'write_coa',
    name: 'Write Chart of Accounts',
    version: 1,
    active: true,
    resource: 'chart_of_accounts',
    permissionType: PermissionType.MODULE,
  },
];

export const chartOfAccountsBundle: RuntimeMetadataBundle = {
  model: chartOfAccountsModel,
  views: [chartOfAccountsFormView, chartOfAccountsGridView],
  actions: chartOfAccountsActions,
  permissions: chartOfAccountsPermissions,
};
