import { ActionDefinition, ActionType } from '@/core/metadata/schema/action/ActionDefinition';
import { LayoutType } from '@/core/metadata/schema/layout/LayoutDefinition';
import { ModelDefinition } from '@/core/metadata/schema/model/ModelDefinition';
import {
  PermissionDefinition,
  PermissionType,
} from '@/core/metadata/schema/permission/PermissionDefinition';
import { RuntimeMetadataBundle } from '@/core/metadata/schema/RuntimeMetadataBundle';
import { ViewDefinition, ViewType } from '@/core/metadata/schema/view/ViewDefinition';

export const documentModel: ModelDefinition = {
  id: 'model_document_001',
  code: 'document',
  name: 'Document',
  description: 'Document management',
  version: 1,
  active: true,
  tableName: 'documents',
  auditable: true,
  workflowEnabled: false,
  tenantAware: false,
  fields: [
    {
      id: 'field_doc_filename',
      code: 'fileName',
      name: 'File Name',
      type: 'TEXT',
      required: true,
      searchable: true,
      filterable: true,
      version: 1,
      active: true,
    },
    {
      id: 'field_doc_mime',
      code: 'mimeType',
      name: 'MIME Type',
      type: 'TEXT',
      required: false,
      version: 1,
      active: true,
    },
    {
      id: 'field_doc_size',
      code: 'fileSize',
      name: 'File Size',
      type: 'NUMBER',
      required: false,
      version: 1,
      active: true,
    },
    {
      id: 'field_doc_owner',
      code: 'owner',
      name: 'Owner',
      type: 'TEXT',
      required: false,
      filterable: true,
      version: 1,
      active: true,
    },
    {
      id: 'field_doc_module',
      code: 'module',
      name: 'Module',
      type: 'TEXT',
      required: false,
      filterable: true,
      version: 1,
      active: true,
    },
    {
      id: 'field_doc_version',
      code: 'version',
      name: 'Version',
      type: 'NUMBER',
      required: false,
      readonly: true,
      version: 1,
      active: true,
    },
    {
      id: 'field_doc_category',
      code: 'category',
      name: 'Category',
      type: 'TEXT',
      required: false,
      filterable: true,
      version: 1,
      active: true,
    },
  ],
};

export const documentFormView: ViewDefinition = {
  id: 'view_document_form',
  code: 'document_form',
  modelCode: 'document',
  viewType: ViewType.FORM,
  name: 'Document Form',
  title: 'Document Details',
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
              { type: LayoutType.COLUMN, config: { span: 6, fieldCode: 'fileName' } },
              { type: LayoutType.COLUMN, config: { span: 3, fieldCode: 'mimeType' } },
              { type: LayoutType.COLUMN, config: { span: 3, fieldCode: 'version' } },
            ],
          },
          {
            type: LayoutType.ROW,
            children: [
              { type: LayoutType.COLUMN, config: { span: 3, fieldCode: 'owner' } },
              { type: LayoutType.COLUMN, config: { span: 3, fieldCode: 'module' } },
              { type: LayoutType.COLUMN, config: { span: 3, fieldCode: 'category' } },
              { type: LayoutType.COLUMN, config: { span: 3, fieldCode: 'fileSize' } },
            ],
          },
        ],
      },
    ],
  },
};

export const documentGridView: ViewDefinition = {
  id: 'view_document_grid',
  code: 'document_list',
  modelCode: 'document',
  viewType: ViewType.GRID,
  name: 'Document Grid',
  title: 'Documents',
  version: 1,
  active: true,
  layout: {
    type: LayoutType.GRID,
    config: {
      columns: ['fileName', 'mimeType', 'version', 'owner', 'module', 'category'],
    },
  },
};

export const documentActions: ActionDefinition[] = [
  {
    id: 'act_doc_upload',
    code: 'upload_document',
    name: 'Upload',
    version: 1,
    active: true,
    actionType: ActionType.BUTTON,
    icon: 'upload',
  },
];

export const documentPermissions: PermissionDefinition[] = [
  {
    id: 'perm_doc_read',
    code: 'read_document',
    name: 'Read Document',
    version: 1,
    active: true,
    resource: 'document',
    permissionType: PermissionType.MODULE,
  },
  {
    id: 'perm_doc_admin',
    code: 'admin_document',
    name: 'Admin Document',
    version: 1,
    active: true,
    resource: 'document',
    permissionType: PermissionType.MODULE,
  },
];

export const documentBundle: RuntimeMetadataBundle = {
  model: documentModel,
  views: [documentFormView, documentGridView],
  actions: documentActions,
  permissions: documentPermissions,
};
