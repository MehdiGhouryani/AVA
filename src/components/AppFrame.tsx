import React from 'react';

interface AppFrameProps {
  children: React.ReactNode;
  toastMessage?: string | null;
}

export const AppFrame: React.FC<AppFrameProps> = ({ children, toastMessage }) => {
  return (
    <div
      style={{
        width: '100vw',
        height: '100dvh',
        backgroundColor: 'var(--color-paper)',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        overflow: 'hidden',
        position: 'relative'
      }}
    >
      <main
        id="app-frame"
        style={{
          width: '100%',
          maxWidth: '430px',
          height: '100%',
          maxHeight: '100dvh',
          backgroundColor: '#FFFFFF',
          position: 'relative',
          overflow: 'hidden',
          display: 'flex',
          flexDirection: 'column',
          boxSizing: 'border-box'
        }}
        className="app-container"
      >
        {children}

        {/* Global Toast Notification */}
        {toastMessage && (
          <div
            role="status"
            aria-live="polite"
            style={{
              position: 'absolute',
              top: '24px',
              left: '50%',
              transform: 'translateX(-50%)',
              backgroundColor: 'var(--color-evergreen-deep)',
              color: '#FFFFFF',
              padding: '10px 20px',
              borderRadius: '999px',
              fontSize: '14px',
              fontWeight: 600,
              boxShadow: '0 4px 16px rgba(0,0,0,0.18)',
              zIndex: 1000,
              animation: 'fadeInDown 250ms ease',
              whiteSpace: 'nowrap'
            }}
          >
            {toastMessage}
          </div>
        )}
      </main>
    </div>
  );
};
