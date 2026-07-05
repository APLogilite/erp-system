import { useState } from 'react';

import { PageContainer } from '@/components/layouts/PageContainer';
import type { RuntimeMetadataBundle } from '@/core/metadata/schema/RuntimeMetadataBundle';
import { businessPartnerBundle } from '@/core/metadata/schema/sample/businessPartnerSample';
import { productBundle } from '@/core/metadata/schema/sample/productSample';
import { warehouseBundle } from '@/core/metadata/schema/sample/warehouseSample';
import { RuntimeRenderer } from '@/runtime/renderer/RuntimeRenderer';

type ModuleKey = 'business_partner' | 'product' | 'warehouse';

const modules: Record<
  ModuleKey,
  { label: string; bundle: RuntimeMetadataBundle; formView: string }
> = {
  business_partner: {
    label: 'Business Partner',
    bundle: businessPartnerBundle,
    formView: 'business_partner_form',
  },
  product: {
    label: 'Product',
    bundle: productBundle,
    formView: 'product_form',
  },
  warehouse: {
    label: 'Warehouse',
    bundle: warehouseBundle,
    formView: 'warehouse_form',
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
