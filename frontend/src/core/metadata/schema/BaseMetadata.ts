export interface BaseMetadata {
  id: string;
  code: string;
  name: string;
  description?: string;
  version: number;
  active: boolean;
  properties?: Record<string, unknown>;
}
