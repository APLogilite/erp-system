import axios from 'axios';

import { apiConfig } from './apiConfig';
import { requestInterceptor, responseErrorInterceptor, responseInterceptor } from './interceptors';

export const apiClient = axios.create(apiConfig);

apiClient.interceptors.request.use(requestInterceptor, (error) => Promise.reject(error));
apiClient.interceptors.response.use(responseInterceptor, responseErrorInterceptor);
