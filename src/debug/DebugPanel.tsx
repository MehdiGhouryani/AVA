import React, { useState, useEffect } from 'react';
import { AppRoute, OtpStatus } from '../app/flow';
import { authService } from '../services/auth';

interface DebugPanelProps {
  currentRoute: AppRoute;
  onNavigate: (route: AppRoute) => void;
  isOffline: boolean;
  onToggleOffline: (offline: boolean) => void;
  isReducedMotion: boolean;
  onToggleReducedMotion: () => void;
  onForcePhoneInvalid: () => void;
  onForceProviderError: () => void;
  onForcePhoneLoading: () => void;
  onForceOtpStatus: (status: OtpStatus) => void;
}

export const DebugPanel: React.FC<DebugPanelProps> = ({
  currentRoute,
  onNavigate,
  isOffline,
  onToggleOffline,
  isReducedMotion,
  onToggleReducedMotion,
  onForcePhoneInvalid,
  onForceProviderError,
  onForcePhoneLoading,
  onForceOtpStatus
}) => {
  const [isVisible, setIsVisible] = useState(false);
  const [isExpanded, setIsExpanded] = useState(false);

  useEffect(() => {
    if (typeof window !== 'undefined') {
      const params = new URLSearchParams(window.location.search);
      if (params.get('debug') === '1') {
        setIsVisible(true);
      }
    }
  }, []);

  if (!isVisible) return null;

  return (
    <div
      style={{
        position: 'fixed',
        bottom: '16px',
        right: '16px', // physical right
        zIndex: 9999,
        fontFamily: 'system-ui, -apple-system, sans-serif',
        direction: 'ltr',
        fontSize: '12px'
      }}
    >
      {/* Floating Toggle Button */}
      {!isExpanded ? (
        <button
          type="button"
          onClick={() => setIsExpanded(true)}
          style={{
            backgroundColor: '#233D47',
            color: '#FEF7E1',
            border: '2px solid #FFBD59',
            borderRadius: '999px',
            padding: '8px 14px',
            fontSize: '12px',
            fontWeight: 700,
            cursor: 'pointer',
            boxShadow: '0 4px 14px rgba(0,0,0,0.3)',
            display: 'flex',
            alignItems: 'center',
            gap: '6px'
          }}
        >
          <span>🛠️ DEBUG PANEL</span>
        </button>
      ) : (
        /* Expanded Control Window */
        <div
          style={{
            backgroundColor: '#1C2E35',
            color: '#FFFFFF',
            border: '2px solid #FFBD59',
            borderRadius: '16px',
            padding: '16px',
            width: '280px',
            boxShadow: '0 8px 30px rgba(0,0,0,0.5)',
            maxHeight: '80vh',
            overflowY: 'auto'
          }}
        >
          {/* Header */}
          <div
            style={{
              display: 'flex',
              justifyContent: 'space-between',
              alignItems: 'center',
              borderBottom: '1px solid #395965',
              paddingBottom: '8px',
              marginBottom: '12px'
            }}
          >
            <strong style={{ color: '#FFBD59', fontSize: '13px' }}>🛠️ AVA QA Inspector</strong>
            <button
              type="button"
              onClick={() => setIsExpanded(false)}
              style={{
                background: 'none',
                border: 'none',
                color: '#FEF7E1',
                fontSize: '16px',
                cursor: 'pointer',
                padding: '2px 6px'
              }}
            >
              ✕
            </button>
          </div>

          {/* Section 1: Route Jump */}
          <div style={{ marginBottom: '14px' }}>
            <div style={{ color: '#C7DDD2', fontWeight: 600, marginBottom: '6px' }}>
              Jump to Screen:
            </div>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '6px' }}>
              {(
                [
                  ['Splash', 'splash'],
                  ['Onboard 1', 'onboarding1'],
                  ['Onboard 2', 'onboarding2'],
                  ['Phone', 'phone'],
                  ['OTP', 'otp'],
                  ['Home', 'home']
                ] as const
              ).map(([label, routeKey]) => (
                <button
                  key={routeKey}
                  type="button"
                  onClick={() => onNavigate(routeKey)}
                  style={{
                    backgroundColor: currentRoute === routeKey ? '#14766A' : '#2A4650',
                    color: '#FFFFFF',
                    border: 'none',
                    borderRadius: '6px',
                    padding: '6px 8px',
                    fontSize: '11px',
                    fontWeight: 600,
                    cursor: 'pointer'
                  }}
                >
                  {label}
                </button>
              ))}
            </div>
          </div>

          {/* Section 2: Global Toggles */}
          <div style={{ marginBottom: '14px' }}>
            <div style={{ color: '#C7DDD2', fontWeight: 600, marginBottom: '6px' }}>
              Environment:
            </div>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '6px' }}>
              <button
                type="button"
                onClick={() => onToggleOffline(!isOffline)}
                style={{
                  backgroundColor: isOffline ? '#E3900F' : '#2A4650',
                  color: isOffline ? '#000' : '#FFF',
                  border: 'none',
                  borderRadius: '6px',
                  padding: '6px 10px',
                  fontSize: '11px',
                  fontWeight: 600,
                  cursor: 'pointer',
                  textAlign: 'left'
                }}
              >
                {isOffline ? '⚡ Offline: ON' : '🌐 Offline: OFF'}
              </button>

              <button
                type="button"
                onClick={onToggleReducedMotion}
                style={{
                  backgroundColor: isReducedMotion ? '#7954B5' : '#2A4650',
                  color: '#FFFFFF',
                  border: 'none',
                  borderRadius: '6px',
                  padding: '6px 10px',
                  fontSize: '11px',
                  fontWeight: 600,
                  cursor: 'pointer',
                  textAlign: 'left'
                }}
              >
                {isReducedMotion ? '🎬 Reduced Motion: ON' : '🎬 Reduced Motion: OFF'}
              </button>
            </div>
          </div>

          {/* Section 3: Phone Test Injections */}
          <div style={{ marginBottom: '14px' }}>
            <div style={{ color: '#C7DDD2', fontWeight: 600, marginBottom: '6px' }}>
              Phone States:
            </div>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '6px' }}>
              <button
                type="button"
                onClick={onForcePhoneInvalid}
                style={{
                  backgroundColor: '#2A4650',
                  color: '#FFBD59',
                  border: '1px solid #FFBD59',
                  borderRadius: '6px',
                  padding: '6px',
                  fontSize: '11px',
                  cursor: 'pointer'
                }}
              >
                Trigger Invalid Phone Error
              </button>
              <button
                type="button"
                onClick={onForceProviderError}
                style={{
                  backgroundColor: '#2A4650',
                  color: '#FFBD59',
                  border: '1px solid #FFBD59',
                  borderRadius: '6px',
                  padding: '6px',
                  fontSize: '11px',
                  cursor: 'pointer'
                }}
              >
                Trigger Provider Error
              </button>
              <button
                type="button"
                onClick={onForcePhoneLoading}
                style={{
                  backgroundColor: '#2A4650',
                  color: '#FFF',
                  border: 'none',
                  borderRadius: '6px',
                  padding: '6px',
                  fontSize: '11px',
                  cursor: 'pointer'
                }}
              >
                Toggle Loading Spinner
              </button>
            </div>
          </div>

          {/* Section 4: OTP Test Injections */}
          <div>
            <div style={{ color: '#C7DDD2', fontWeight: 600, marginBottom: '6px' }}>
              OTP States:
            </div>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '6px' }}>
              <button
                type="button"
                onClick={() => {
                  authService.forceVerifyResult = 'wrong';
                  onForceOtpStatus('wrong');
                }}
                style={{
                  backgroundColor: '#E3900F',
                  color: '#000',
                  border: 'none',
                  borderRadius: '6px',
                  padding: '6px',
                  fontSize: '11px',
                  fontWeight: 600,
                  cursor: 'pointer'
                }}
              >
                Force Wrong
              </button>
              <button
                type="button"
                onClick={() => {
                  authService.forceVerifyResult = 'expired';
                  onForceOtpStatus('expired');
                }}
                style={{
                  backgroundColor: '#8A4F00',
                  color: '#FFF',
                  border: 'none',
                  borderRadius: '6px',
                  padding: '6px',
                  fontSize: '11px',
                  fontWeight: 600,
                  cursor: 'pointer'
                }}
              >
                Force Expired
              </button>
              <button
                type="button"
                onClick={() => {
                  authService.forceVerifyResult = 'locked';
                  onForceOtpStatus('locked');
                }}
                style={{
                  backgroundColor: '#7C3457',
                  color: '#FFF',
                  border: 'none',
                  borderRadius: '6px',
                  padding: '6px',
                  fontSize: '11px',
                  fontWeight: 600,
                  cursor: 'pointer'
                }}
              >
                Force Locked
              </button>
              <button
                type="button"
                onClick={() => {
                  authService.forceVerifyResult = 'success';
                  onForceOtpStatus('success');
                }}
                style={{
                  backgroundColor: '#14766A',
                  color: '#FFF',
                  border: 'none',
                  borderRadius: '6px',
                  padding: '6px',
                  fontSize: '11px',
                  fontWeight: 600,
                  cursor: 'pointer'
                }}
              >
                Force Success
              </button>
            </div>

            <div style={{ marginTop: '8px' }}>
              <button
                type="button"
                onClick={() => {
                  authService.forceVerifyResult = null;
                  authService.forceRequestError = null;
                  authService.resetAttempts();
                  onForceOtpStatus('idle');
                }}
                style={{
                  width: '100%',
                  backgroundColor: '#395965',
                  color: '#FFF',
                  border: 'none',
                  borderRadius: '6px',
                  padding: '6px',
                  fontSize: '11px',
                  cursor: 'pointer'
                }}
              >
                Reset Auth Mock Overrides
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
