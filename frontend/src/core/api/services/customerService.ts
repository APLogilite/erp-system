import { apiClient } from '../client';
import { ENDPOINTS } from '../endpoints';

export interface Customer {
  id: string;
  name: string;
  email: string;
  phone?: string;
  status: 'active' | 'inactive';
}

export const customerService = {
  getCustomers: async (params?: Record<string, unknown>): Promise<Customer[]> => {
    const response = await apiClient.get<Customer[]>(ENDPOINTS.customers.base, { params });
    return response.data;
  },

  getCustomerById: async (id: string | number): Promise<Customer> => {
    const response = await apiClient.get<Customer>(ENDPOINTS.customers.detail(id));
    return response.data;
  },

  createCustomer: async (customer: Omit<Customer, 'id'>): Promise<Customer> => {
    const response = await apiClient.post<Customer>(ENDPOINTS.customers.base, customer);
    return response.data;
  },

  updateCustomer: async (id: string | number, customer: Partial<Customer>): Promise<Customer> => {
    const response = await apiClient.put<Customer>(ENDPOINTS.customers.detail(id), customer);
    return response.data;
  },

  deleteCustomer: async (id: string | number): Promise<void> => {
    await apiClient.delete(ENDPOINTS.customers.detail(id));
  },
};
