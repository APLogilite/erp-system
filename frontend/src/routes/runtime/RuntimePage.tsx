import { useState } from 'react';

import { PageContainer } from '@/components/layouts/PageContainer';
import type { RuntimeMetadataBundle } from '@/core/metadata/schema/RuntimeMetadataBundle';
import { assetBundle } from '@/core/metadata/schema/sample/assetSample';
import { bomBundle } from '@/core/metadata/schema/sample/bomSample';
import { businessPartnerBundle } from '@/core/metadata/schema/sample/businessPartnerSample';
import { chartOfAccountsBundle } from '@/core/metadata/schema/sample/chartOfAccountsSample';
import { dashboardBundle } from '@/core/metadata/schema/sample/dashboardSample';
import { departmentBundle } from '@/core/metadata/schema/sample/departmentSample';
import { documentBundle } from '@/core/metadata/schema/sample/documentSample';
import { employeeBundle } from '@/core/metadata/schema/sample/employeeSample';
import { inventoryTransactionBundle } from '@/core/metadata/schema/sample/inventoryTransactionSample';
import { journalEntryBundle } from '@/core/metadata/schema/sample/journalEntrySample';
import { leadBundle } from '@/core/metadata/schema/sample/leadSample';
import { manufacturingOrderBundle } from '@/core/metadata/schema/sample/manufacturingOrderSample';
import { notificationBundle } from '@/core/metadata/schema/sample/notificationSample';
import { opportunityBundle } from '@/core/metadata/schema/sample/opportunitySample';
import { productBundle } from '@/core/metadata/schema/sample/productSample';
import { projectBundle } from '@/core/metadata/schema/sample/projectSample';
import { purchaseOrderBundle } from '@/core/metadata/schema/sample/purchaseOrderSample';
import { reportBundle } from '@/core/metadata/schema/sample/reportSample';
import { reservationBundle } from '@/core/metadata/schema/sample/reservationSample';
import { routingBundle } from '@/core/metadata/schema/sample/routingSample';
import { salesOrderBundle } from '@/core/metadata/schema/sample/salesOrderSample';
import { serviceRequestBundle } from '@/core/metadata/schema/sample/serviceRequestSample';
import { taskBundle } from '@/core/metadata/schema/sample/taskSample';
import { warehouseBundle } from '@/core/metadata/schema/sample/warehouseSample';
import { workCenterBundle } from '@/core/metadata/schema/sample/workCenterSample';
import { RuntimeRenderer } from '@/runtime/renderer/RuntimeRenderer';

type ModuleKey =
  | 'asset'
  | 'bill_of_material'
  | 'business_partner'
  | 'chart_of_accounts'
  | 'department'
  | 'employee'
  | 'journal_entry'
  | 'lead'
  | 'manufacturing_order'
  | 'opportunity'
  | 'product'
  | 'project'
  | 'routing'
  | 'service_request'
  | 'task'
  | 'warehouse'
  | 'sales_order'
  | 'inventory_transaction'
  | 'purchase_order'
  | 'reservation'
  | 'work_center'
  | 'dashboard'
  | 'document'
  | 'notification'
  | 'report_definition';

const modules: Record<
  ModuleKey,
  { label: string; bundle: RuntimeMetadataBundle; formView: string }
> = {
  asset: {
    label: 'Asset',
    bundle: assetBundle,
    formView: 'asset_form',
  },
  bill_of_material: {
    label: 'Bill of Material',
    bundle: bomBundle,
    formView: 'bom_form',
  },
  business_partner: {
    label: 'Business Partner',
    bundle: businessPartnerBundle,
    formView: 'business_partner_form',
  },
  chart_of_accounts: {
    label: 'Chart of Accounts',
    bundle: chartOfAccountsBundle,
    formView: 'chart_of_accounts_form',
  },
  department: {
    label: 'Department',
    bundle: departmentBundle,
    formView: 'department_form',
  },
  employee: {
    label: 'Employee',
    bundle: employeeBundle,
    formView: 'employee_form',
  },
  journal_entry: {
    label: 'Journal Entry',
    bundle: journalEntryBundle,
    formView: 'journal_entry_form',
  },
  lead: {
    label: 'Lead',
    bundle: leadBundle,
    formView: 'lead_form',
  },
  manufacturing_order: {
    label: 'Manufacturing Order',
    bundle: manufacturingOrderBundle,
    formView: 'mo_form',
  },
  opportunity: {
    label: 'Opportunity',
    bundle: opportunityBundle,
    formView: 'opportunity_form',
  },
  routing: {
    label: 'Routing',
    bundle: routingBundle,
    formView: 'routing_form',
  },
  product: {
    label: 'Product',
    bundle: productBundle,
    formView: 'product_form',
  },
  project: {
    label: 'Project',
    bundle: projectBundle,
    formView: 'project_form',
  },
  service_request: {
    label: 'Service Request',
    bundle: serviceRequestBundle,
    formView: 'service_request_form',
  },
  task: {
    label: 'Task',
    bundle: taskBundle,
    formView: 'task_form',
  },
  warehouse: {
    label: 'Warehouse',
    bundle: warehouseBundle,
    formView: 'warehouse_form',
  },
  sales_order: {
    label: 'Sales Order',
    bundle: salesOrderBundle,
    formView: 'sales_order_form',
  },
  inventory_transaction: {
    label: 'Inventory Transaction',
    bundle: inventoryTransactionBundle,
    formView: 'inventory_transaction_form',
  },
  purchase_order: {
    label: 'Purchase Order',
    bundle: purchaseOrderBundle,
    formView: 'purchase_order_form',
  },
  reservation: {
    label: 'Reservation',
    bundle: reservationBundle,
    formView: 'reservation_form',
  },
  work_center: {
    label: 'Work Center',
    bundle: workCenterBundle,
    formView: 'work_center_form',
  },
  dashboard: {
    label: 'Dashboard',
    bundle: dashboardBundle,
    formView: 'dashboard_form',
  },
  document: {
    label: 'Document',
    bundle: documentBundle,
    formView: 'document_form',
  },
  notification: {
    label: 'Notification',
    bundle: notificationBundle,
    formView: 'notification_form',
  },
  report_definition: {
    label: 'Report Definition',
    bundle: reportBundle,
    formView: 'report_form',
  },
};

export function RuntimePage() {
  const [activeModule, setActiveModule] = useState<ModuleKey>('business_partner');
  const current = modules[activeModule];

  return (
    <PageContainer title="Runtime Demo" subtitle="Metadata-driven runtime renderer">
      <div style={{ marginBottom: 16, display: 'flex', gap: 8 }}>
        {(Object.keys(modules) as ModuleKey[]).map((key) => (
          <button
            key={key}
            onClick={() => setActiveModule(key)}
            style={{
              padding: '8px 16px',
              fontWeight: activeModule === key ? 700 : 400,
              background: activeModule === key ? '#1976d2' : '#e0e0e0',
              color: activeModule === key ? '#fff' : '#000',
              border: 'none',
              borderRadius: 4,
              cursor: 'pointer',
            }}
          >
            {modules[key].label}
          </button>
        ))}
      </div>
      <RuntimeRenderer metadataBundle={current.bundle} viewCode={current.formView} />
    </PageContainer>
  );
}
