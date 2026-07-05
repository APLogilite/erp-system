export class RegistryError extends Error {
  constructor(message: string) {
    super(message);
    this.name = 'RegistryError';
  }
}

export class RegistryNotFoundError extends RegistryError {
  constructor(key: string) {
    super(`Registry key not found: ${key}`);
    this.name = 'RegistryNotFoundError';
  }
}

export class DuplicateRegistrationError extends RegistryError {
  constructor(key: string, priority: number) {
    super(`Duplicate registration for key '${key}' at priority ${priority}`);
    this.name = 'DuplicateRegistrationError';
  }
}
