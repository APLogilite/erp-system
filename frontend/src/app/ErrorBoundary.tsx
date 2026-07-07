import { Component, ReactNode } from 'react';

import { useAuthStore } from '@/core/auth/authStore';

type ErrorBoundaryProps = {
  children: ReactNode;
};

type ErrorBoundaryState = {
  hasError: boolean;
  error: Error | null;
};

export class ErrorBoundary extends Component<ErrorBoundaryProps, ErrorBoundaryState> {
  constructor(props: ErrorBoundaryProps) {
    super(props);
    this.state = { hasError: false, error: null };
  }

  static getDerivedStateFromError(error: Error) {
    return { hasError: true, error };
  }

  override componentDidCatch(error: Error) {
    console.error('Unhandled error in React tree:', error);
  }

  /** Reset error state when auth state changes (e.g., logout → login redirect) */
  private unsubscribe: (() => void) | null = null;

  override componentDidMount() {
    this.unsubscribe = useAuthStore.subscribe((state, prev) => {
      if (prev.isAuthenticated && !state.isAuthenticated && this.state.hasError) {
        this.setState({ hasError: false, error: null });
      }
    });
  }

  override componentWillUnmount() {
    this.unsubscribe?.();
  }

  override render() {
    if (this.state.hasError) {
      return (
        <section className="error-boundary" style={{ padding: 48, textAlign: 'center' }}>
          <h2>Something went wrong.</h2>
          <p>We are unable to render the page. Please refresh or contact support.</p>
        </section>
      );
    }

    return this.props.children;
  }
}
