import { ActionDefinition, ActionType } from '@/core/metadata/schema/action/ActionDefinition';
import { LayoutType } from '@/core/metadata/schema/layout/LayoutDefinition';
import { ModelDefinition } from '@/core/metadata/schema/model/ModelDefinition';
import {
  PermissionDefinition,
  PermissionType,
} from '@/core/metadata/schema/permission/PermissionDefinition';
import { RuntimeMetadataBundle } from '@/core/metadata/schema/RuntimeMetadataBundle';
import { ViewDefinition, ViewType } from '@/core/metadata/schema/view/ViewDefinition';

export const journalEntryModel: ModelDefinition = {
  id: 'model_je_001',
  code: 'journal_entry',
  name: 'Journal Entry',
  description: 'Financial transaction document',
  version: 1,
  active: true,
  tableName: 'journal_entries',
  auditable: true,
  workflowEnabled: true,
  tenantAware: false,
  fields: [
    {
      id: 'field_je_docno',
      code: 'documentNo',
      name: 'Document No',
      type: 'TEXT',
      required: true,
      readonly: true,
      version: 1,
      active: true,
    },
    {
      id: 'field_je_date',
      code: 'documentDate',
      name: 'Document Date',
      type: 'DATE',
      required: true,
      filterable: true,
      version: 1,
      active: true,
    },
    {
      id: 'field_je_desc',
      code: 'description',
      name: 'Description',
      type: 'TEXT',
      required: false,
      version: 1,
      active: true,
    },
    {
      id: 'field_je_status',
      code: 'status',
      name: 'Status',
      type: 'SELECT',
      required: false,
      readonly: true,
      filterable: true,
      version: 1,
      active: true,
      defaultValue: 'DRAFT',
      properties: {
        options: [
          { label: 'Draft', value: 'DRAFT' },
          { label: 'Completed', value: 'COMPLETED' },
          { label: 'Posted', value: 'POSTED' },
          { label: 'Closed', value: 'CLOSED' },
        ],
      },
    },
    {
      id: 'field_je_debit',
      code: 'totalDebit',
      name: 'Total Debit',
      type: 'DECIMAL',
      required: false,
      readonly: true,
      version: 1,
      active: true,
      defaultValue: 0,
    },
    {
      id: 'field_je_credit',
      code: 'totalCredit',
      name: 'Total Credit',
      type: 'DECIMAL',
      required: false,
      readonly: true,
      version: 1,
      active: true,
      defaultValue: 0,
    },
  ],
};

export const journalEntryFormView: ViewDefinition = {
  id: 'view_je_form',
  code: 'journal_entry_form',
  modelCode: 'journal_entry',
  viewType: ViewType.FORM,
  name: 'Journal Entry Form',
  title: 'Journal Entry Details',
  version: 1,
  active: true,
  layout: {
    type: LayoutType.PAGE,
    children: [
      {
        type: LayoutType.SECTION,
        config: { title: 'Document Information' },
        children: [
          {
            type: LayoutType.ROW,
            children: [
              { type: LayoutType.COLUMN, config: { span: 3, fieldCode: 'documentNo' } },
              { type: LayoutType.COLUMN, config: { span: 3, fieldCode: 'documentDate' } },
              { type: LayoutType.COLUMN, config: { span: 3, fieldCode: 'status' } },
            ],
          },
          {
            type: LayoutType.ROW,
            children: [{ type: LayoutType.COLUMN, config: { span: 12, fieldCode: 'description' } }],
          },
        ],
      },
      {
        type: LayoutType.SECTION,
        config: { title: 'Totals' },
        children: [
          {
            type: LayoutType.ROW,
            children: [
              { type: LayoutType.COLUMN, config: { span: 3, fieldCode: 'totalDebit' } },
              { type: LayoutType.COLUMN, config: { span: 3, fieldCode: 'totalCredit' } },
            ],
          },
        ],
      },
    ],
  },
};

export const journalEntryGridView: ViewDefinition = {
  id: 'view_je_grid',
  code: 'journal_entry_list',
  modelCode: 'journal_entry',
  viewType: ViewType.GRID,
  name: 'Journal Entry Grid',
  title: 'Journal Entries',
  version: 1,
  active: true,
  layout: {
    type: LayoutType.GRID,
    config: {
      columns: ['documentNo', 'documentDate', 'description', 'status', 'totalDebit', 'totalCredit'],
    },
  },
};

export const journalEntryActions: ActionDefinition[] = [
  {
    id: 'act_je_create',
    code: 'create_journal_entry',
    name: 'Create',
    version: 1,
    active: true,
    actionType: ActionType.BUTTON,
    icon: 'add',
  },
  {
    id: 'act_je_complete',
    code: 'complete',
    name: 'Complete',
    version: 1,
    active: true,
    actionType: ActionType.CUSTOM,
    icon: 'check',
  },
  {
    id: 'act_je_post',
    code: 'post',
    name: 'Post',
    version: 1,
    active: true,
    actionType: ActionType.CUSTOM,
    icon: 'send',
  },
  {
    id: 'act_je_close',
    code: 'close',
    name: 'Close',
    version: 1,
    active: true,
    actionType: ActionType.CUSTOM,
    icon: 'lock',
  },
  {
    id: 'act_je_reverse',
    code: 'reverse',
    name: 'Reverse',
    version: 1,
    active: true,
    actionType: ActionType.CUSTOM,
    icon: 'undo',
  },
  {
    id: 'act_je_print',
    code: 'print',
    name: 'Print',
    version: 1,
    active: true,
    actionType: ActionType.CUSTOM,
    icon: 'print',
  },
];

export const journalEntryPermissions: PermissionDefinition[] = [
  {
    id: 'perm_je_read',
    code: 'read_journal_entry',
    name: 'Read Journal Entry',
    version: 1,
    active: true,
    resource: 'journal_entry',
    permissionType: PermissionType.MODULE,
  },
  {
    id: 'perm_je_write',
    code: 'write_journal_entry',
    name: 'Write Journal Entry',
    version: 1,
    active: true,
    resource: 'journal_entry',
    permissionType: PermissionType.MODULE,
  },
];

export const journalEntryBundle: RuntimeMetadataBundle = {
  model: journalEntryModel,
  views: [journalEntryFormView, journalEntryGridView],
  actions: journalEntryActions,
  permissions: journalEntryPermissions,
};
