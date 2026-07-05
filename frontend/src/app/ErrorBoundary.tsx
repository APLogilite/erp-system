import { Component, ReactNode } from 'react';

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

  override render() {
    if (this.state.hasError) {
      return (
        <section className="error-boundary">
          <h2>Something went wrong.</h2>
          <p>We are unable to render the page. Please refresh or contact support.</p>
        </section>
      );
    }

    return this.props.children;
  }
}
