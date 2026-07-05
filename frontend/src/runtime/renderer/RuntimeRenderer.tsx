import { ViewRenderer } from './ViewRenderer';

import type { RuntimeRenderOptions } from '@/runtime/context/runtime.types';
import { RuntimeProvider } from '@/runtime/context/RuntimeProvider';

export function RuntimeRenderer(props: RuntimeRenderOptions) {
  return (
    <RuntimeProvider options={props}>
      <ViewRenderer />
    </RuntimeProvider>
  );
}
