export interface ApiError {
  message: string;
  status: number;
  code?: string;
  details?: unknown;
}

export function parseApiError(error: unknown): ApiError {
  if (error && typeof error === 'object') {
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    const err = error as any;
    if (err.response) {
      // Server returned an error response
      const data = err.response.data;
      return {
        message: data?.message || data?.error || err.response.statusText || 'Server Error',
        status: err.response.status,
        code: data?.code || `HTTP_${err.response.status}`,
        details: data?.details,
      };
    } else if (err.request) {
      // Request was made but no response was received
      if (err.code === 'ECONNABORTED' || err.message?.includes('timeout')) {
        return {
          message: 'Request timeout. Please check your network connection.',
          status: 408,
          code: 'TIMEOUT',
        };
      }
      return {
        message: 'No response from server. Please check your internet connection.',
        status: 0,
        code: 'NETWORK_ERROR',
      };
    }
  }

  const fallbackError = error as Error | undefined;
  return {
    message: fallbackError?.message || 'An unexpected error occurred.',
    status: 500,
    code: 'UNKNOWN_ERROR',
  };
}

export function getUserFriendlyErrorMessage(error: ApiError): string {
  switch (error.status) {
    case 401:
      return 'Session expired. Please log in again.';
    case 403:
      return 'You do not have permission to perform this action.';
    case 404:
      return 'The requested resource was not found.';
    case 408:
      return 'The request timed out. Please try again.';
    case 500:
    case 502:
    case 503:
    case 504:
      return 'Server error. Please try again later or contact support.';
    case 0:
      return 'Unable to connect to the server. Please verify your internet connection.';
    default:
      return error.message || 'An error occurred. Please try again.';
  }
}
