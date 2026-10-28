import React, { useReducer, useEffect } from 'react';
import { flowReducer, initialFlowState, AppRoute, OtpStatus } from './flow';
import { AppFrame } from '../components/AppFrame';
import { SplashScreen } from '../screens/SplashScreen';
import { OnboardingScreen } from '../screens/OnboardingScreen';
import { PhoneScreen } from '../screens/PhoneScreen';
import { OtpScreen } from '../screens/OtpScreen';
import { HomeScreen } from '../screens/HomeScreen';
import { DebugPanel } from '../debug/DebugPanel';
import { t } from '../i18n/fa';

export const App: React.FC = () => {
  const [state, dispatch] = useReducer(flowReducer, initialFlowState);

  // Monitor network status
  useEffect(() => {
    const handleOnline = () => dispatch({ type: 'SET_OFFLINE', isOffline: false });
    const handleOffline = () => dispatch({ type: 'SET_OFFLINE', isOffline: true });

    window.addEventListener('online', handleOnline);
    window.addEventListener('offline', handleOffline);

    return () => {
      window.removeEventListener('online', handleOnline);
      window.removeEventListener('offline', handleOffline);
    };
  }, []);

  // Toast auto-clear
  useEffect(() => {
    if (state.toastMessage) {
      const timer = setTimeout(() => {
        dispatch({ type: 'SHOW_TOAST', message: null });
      }, 3000);
      return () => clearTimeout(timer);
    }
  }, [state.toastMessage]);

  const navigate = (route: AppRoute) => {
    dispatch({ type: 'NAVIGATE', route });
  };

  return (
    <>
      <AppFrame toastMessage={state.toastMessage}>
        {state.route === 'splash' && (
          <SplashScreen
            onAdvance={() => navigate('onboarding1')}
            isReducedMotion={state.isReducedMotion}
          />
        )}

        {state.route === 'onboarding1' && (
          <OnboardingScreen
            step={1}
            onNext={() => navigate('onboarding2')}
            onSkip={() => navigate('phone')}
            isReducedMotion={state.isReducedMotion}
          />
        )}

        {state.route === 'onboarding2' && (
          <OnboardingScreen
            step={2}
            onNext={() => navigate('phone')}
            onPrev={() => navigate('onboarding1')}
            onSkip={() => navigate('phone')}
            isReducedMotion={state.isReducedMotion}
          />
        )}

        {state.route === 'phone' && (
          <PhoneScreen
            phone={state.phone}
            consent={state.consent}
            isOffline={state.isOffline}
            providerError={state.providerError}
            isLoading={state.isPhoneLoading}
            onUpdatePhone={(raw, canonical) =>
              dispatch({ type: 'SET_PHONE_INPUT', raw, canonical })
            }
            onUpdateConsent={(value) => dispatch({ type: 'SET_CONSENT', value })}
            onSuccess={() => navigate('otp')}
            onErrorState={(error) => dispatch({ type: 'SET_PHONE_ERROR', error })}
            onSetLoading={(loading) => dispatch({ type: 'SET_PHONE_LOADING', loading })}
            onSetProviderError={(error) => dispatch({ type: 'SET_PROVIDER_ERROR', error })}
          />
        )}

        {state.route === 'otp' && (
          <OtpScreen
            phone={state.phone}
            code={state.otpCode}
            status={state.otpStatus}
            resendCountdown={state.resendCountdown}
            lockCountdown={state.lockCountdown}
            onCodeChange={(code) => dispatch({ type: 'SET_OTP_CODE', code })}
            onStatusChange={(status: OtpStatus) => dispatch({ type: 'SET_OTP_STATUS', status })}
            onTickResend={() => dispatch({ type: 'TICK_RESEND' })}
            onTickLock={() => dispatch({ type: 'TICK_LOCK' })}
            onResetResendTimer={(seconds) => dispatch({ type: 'SET_RESEND_COUNTDOWN', seconds })}
            onEditPhone={() => navigate('phone')}
            onSuccess={() => navigate('home')}
            onShowToast={(message) => dispatch({ type: 'SHOW_TOAST', message })}
            onIncrementWrongAttempts={() => dispatch({ type: 'INCREMENT_WRONG_ATTEMPTS' })}
            onSetLockCountdown={(seconds) => dispatch({ type: 'SET_LOCK_COUNTDOWN', seconds })}
          />
        )}

        {state.route === 'home' && (
          <HomeScreen onRestart={() => dispatch({ type: 'RESET_FLOW' })} />
        )}
      </AppFrame>

      {/* Debug Panel (rendered when URL contains ?debug=1) */}
      <DebugPanel
        currentRoute={state.route}
        onNavigate={navigate}
        isOffline={state.isOffline}
        onToggleOffline={(isOffline) => dispatch({ type: 'SET_OFFLINE', isOffline })}
        isReducedMotion={state.isReducedMotion}
        onToggleReducedMotion={() => dispatch({ type: 'TOGGLE_REDUCED_MOTION' })}
        onForcePhoneInvalid={() => {
          navigate('phone');
          dispatch({ type: 'SET_PHONE_ERROR', error: t('phone.errorInvalid') });
        }}
        onForceProviderError={() => {
          navigate('phone');
          dispatch({ type: 'SET_PROVIDER_ERROR', error: true });
        }}
        onForcePhoneLoading={() =>
          dispatch({ type: 'SET_PHONE_LOADING', loading: !state.isPhoneLoading })
        }
        onForceOtpStatus={(status) => {
          navigate('otp');
          dispatch({ type: 'SET_OTP_STATUS', status });
        }}
      />
    </>
  );
};
