import { DuplicateRegistrationError, RegistryNotFoundError } from './registry.errors';
import {
  isLazyRegistryValue,
  Registry,
  RegistryEntry,
  RegistryValue,
  RegistryPriority,
} from './registry.types';

export class BaseRegistry<T> implements Registry<T> {
  private entries = new Map<string, RegistryEntry<T>>();

  register(key: string, value: RegistryValue<T>, priority: RegistryPriority = 0): void {
    const normalizedKey = key.trim().toUpperCase();
    const existing = this.entries.get(normalizedKey);
    if (existing && existing.priority === priority) {
      throw new DuplicateRegistrationError(normalizedKey, priority);
    }

    if (!existing || priority >= existing.priority) {
      this.entries.set(normalizedKey, {
        key: normalizedKey,
        value,
        priority,
      });
    }
  }

  unregister(key: string): void {
    this.entries.delete(key.trim().toUpperCase());
  }

  async resolve(key: string): Promise<T> {
    const normalizedKey = key.trim().toUpperCase();
    const entry = this.entries.get(normalizedKey);
    if (!entry) {
      throw new RegistryNotFoundError(normalizedKey);
    }

    if (isLazyRegistryValue(entry.value)) {
      return entry.value.load();
    }

    return entry.value;
  }

  has(key: string): boolean {
    return this.entries.has(key.trim().toUpperCase());
  }

  getAll(): Record<string, RegistryEntry<T>> {
    return Object.fromEntries(this.entries.entries()) as Record<string, RegistryEntry<T>>;
  }

  clear(): void {
    this.entries.clear();
  }
}
