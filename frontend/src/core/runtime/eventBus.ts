import { RuntimeEvent, RuntimeEventListener, RuntimeEventType } from './runtimeTypes';

class EventBus {
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  private listeners: Map<RuntimeEventType, Set<RuntimeEventListener<any>>> = new Map();

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  subscribe<T = unknown>(type: RuntimeEventType, listener: RuntimeEventListener<T>): () => void {
    if (!this.listeners.has(type)) {
      this.listeners.set(type, new Set());
    }
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    this.listeners.get(type)!.add(listener as RuntimeEventListener<any>);

    // Return unsubscribe function
    return () => {
      const set = this.listeners.get(type);
      if (set) {
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
        set.delete(listener as RuntimeEventListener<any>);
        if (set.size === 0) {
          this.listeners.delete(type);
        }
      }
    };
  }

  publish<T = unknown>(type: RuntimeEventType, payload: T): void {
    const event: RuntimeEvent<T> = {
      type,
      payload,
      timestamp: Date.now(),
    };

    const set = this.listeners.get(type);
    if (set) {
      set.forEach((listener) => {
        try {
          // eslint-disable-next-line @typescript-eslint/no-explicit-any
          (listener as RuntimeEventListener<any>)(event);
        } catch (error) {
          console.error(`Error executing runtime event listener for ${type}:`, error);
        }
      });
    }
  }
}

export const eventBus = new EventBus();
