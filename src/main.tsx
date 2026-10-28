import React, { Component, ErrorInfo, ReactNode } from 'react';
import { createRoot } from 'react-dom/client';
import { App } from './app/App';
import { initCssTokens } from './design/tokens';
import './index.css';

// Initialize tokens as CSS variables on :root
initCssTokens();

// Suppress unhandled extension errors (like MetaMask in sandboxed iframe)
if (typeof window !== 'undefined') {
  window.addEventListener('unhandledrejection', (event) => {
    const reason = event.reason;
    const msg = String(reason?.message || reason?.stack || reason || '');
    if (msg.includes('MetaMask') || msg.includes('ethereum') || msg.includes('chrome-extension://')) {
      event.preventDefault();
      event.stopImmediatePropagation();
    }
  });

  window.addEventListener('error', (event) => {
    if (
      event.filename?.includes('chrome-extension://') ||
      event.message?.includes('MetaMask') ||
      event.message?.includes('ethereum')
    ) {
      event.preventDefault();
      event.stopImmediatePropagation();
    }
  });
}

interface Props {
  children: ReactNode;
}

interface State {
  hasError: boolean;
  error?: Error;
}

class ErrorBoundary extends Component<Props, State> {
  public state: State = {
    hasError: false
  };

  public static getDerivedStateFromError(error: Error): State {
    // If the error came from external chrome extension, ignore it
    if (error?.stack?.includes('chrome-extension://') || error?.message?.includes('MetaMask')) {
      return { hasError: false };
    }
    return { hasError: true, error };
  }

  public componentDidCatch(error: Error, errorInfo: ErrorInfo) {
    if (!error?.stack?.includes('chrome-extension://') && !error?.message?.includes('MetaMask')) {
      console.error('Unhandled app error:', error, errorInfo);
    }
  }

  public render() {
    if (this.state.hasError) {
      return (
        <div
          dir="rtl"
          style={{
            minHeight: '100dvh',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            padding: '24px',
            backgroundColor: '#EDF6F2',
            fontFamily: 'Vazirmatn, sans-serif'
          }}
        >
          <div
            style={{
              backgroundColor: '#FEF7E1',
              padding: '28px',
              borderRadius: '24px',
              maxWidth: '380px',
              textAlign: 'center',
              boxShadow: '0 8px 24px rgba(20,60,50,0.1)'
            }}
          >
            <h2 style={{ color: '#0A5C54', margin: '0 0 12px 0' }}>خطایی رخ داد</h2>
            <p style={{ color: '#395965', fontSize: '15px', lineHeight: 1.6, margin: '0 0 20px 0' }}>
              می‌توانید با دکمهٔ زیر برنامه را مجدداً بارگذاری کنید.
            </p>
            <button
              onClick={() => window.location.reload()}
              style={{
                backgroundColor: '#14766A',
                color: '#FFFFFF',
                border: 'none',
                borderRadius: '999px',
                padding: '12px 24px',
                fontSize: '15px',
                fontWeight: 700,
                cursor: 'pointer'
              }}
            >
              بارگذاری مجدد
            </button>
          </div>
        </div>
      );
    }

    return this.props.children;
  }
}

const rootElement = document.getElementById('root');
if (rootElement) {
  createRoot(rootElement).render(
    <React.StrictMode>
      <ErrorBoundary>
        <App />
      </ErrorBoundary>
    </React.StrictMode>
  );
}
