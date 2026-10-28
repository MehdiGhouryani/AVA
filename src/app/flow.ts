import { tokens } from '../design/tokens';

export type AppRoute = 'splash' | 'onboarding1' | 'onboarding2' | 'phone' | 'otp' | 'home';

export type OtpStatus = 'idle' | 'verifying' | 'wrong' | 'expired' | 'locked' | 'success';

export interface FlowState {
  route: AppRoute;
  phone: string; // canonical 09xxxxxxxxx
  rawPhoneInput: string; // what the user typed or formatted
  consent: boolean;
  phoneError: string | null;
  phoneGuidancePulse: boolean;
  isPhoneLoading: boolean;
  providerError: boolean;
  otpCode: string; // current 6-digit entered
  otpStatus: OtpStatus;
  resendCountdown: number; // in seconds
  lockCountdown: number; // in seconds
  wrongAttemptsCount: number;
  isOffline: boolean;
  isReducedMotion: boolean;
  toastMessage: string | null;
}

export type FlowAction =
  | { type: 'NAVIGATE'; route: AppRoute }
  | { type: 'SET_PHONE_INPUT'; raw: string; canonical: string }
  | { type: 'SET_CONSENT'; value: boolean }
  | { type: 'SET_PHONE_ERROR'; error: string | null }
  | { type: 'SET_PHONE_GUIDANCE_PULSE'; pulse: boolean }
  | { type: 'SET_PHONE_LOADING'; loading: boolean }
  | { type: 'SET_PROVIDER_ERROR'; error: boolean }
  | { type: 'SET_OTP_CODE'; code: string }
  | { type: 'SET_OTP_STATUS'; status: OtpStatus }
  | { type: 'SET_RESEND_COUNTDOWN'; seconds: number }
  | { type: 'TICK_RESEND' }
  | { type: 'SET_LOCK_COUNTDOWN'; seconds: number }
  | { type: 'TICK_LOCK' }
  | { type: 'INCREMENT_WRONG_ATTEMPTS' }
  | { type: 'SET_OFFLINE'; isOffline: boolean }
  | { type: 'TOGGLE_REDUCED_MOTION'; value?: boolean }
  | { type: 'SHOW_TOAST'; message: string | null }
  | { type: 'RESET_FLOW' };

export const initialFlowState: FlowState = {
  route: 'splash',
  phone: '',
  rawPhoneInput: '',
  consent: false,
  phoneError: null,
  phoneGuidancePulse: false,
  isPhoneLoading: false,
  providerError: false,
  otpCode: '',
  otpStatus: 'idle',
  resendCountdown: tokens.auth_mock.resendSeconds,
  lockCountdown: 0,
  wrongAttemptsCount: 0,
  isOffline: typeof navigator !== 'undefined' ? !navigator.onLine : false,
  isReducedMotion: typeof window !== 'undefined' && window.matchMedia ? window.matchMedia('(prefers-reduced-motion: reduce)').matches : false,
  toastMessage: null
};

export function flowReducer(state: FlowState, action: FlowAction): FlowState {
  switch (action.type) {
    case 'NAVIGATE':
      return { ...state, route: action.route };

    case 'SET_PHONE_INPUT':
      return {
        ...state,
        rawPhoneInput: action.raw,
        phone: action.canonical,
        phoneError: null,
        providerError: false
      };

    case 'SET_CONSENT':
      return { ...state, consent: action.value };

    case 'SET_PHONE_ERROR':
      return { ...state, phoneError: action.error };

    case 'SET_PHONE_GUIDANCE_PULSE':
      return { ...state, phoneGuidancePulse: action.pulse };

    case 'SET_PHONE_LOADING':
      return { ...state, isPhoneLoading: action.loading };

    case 'SET_PROVIDER_ERROR':
      return { ...state, providerError: action.error };

    case 'SET_OTP_CODE':
      return {
        ...state,
        otpCode: action.code,
        // Reset wrong status when user edits code
        otpStatus: state.otpStatus === 'wrong' ? 'idle' : state.otpStatus
      };

    case 'SET_OTP_STATUS':
      return { ...state, otpStatus: action.status };

    case 'SET_RESEND_COUNTDOWN':
      return { ...state, resendCountdown: action.seconds };

    case 'TICK_RESEND':
      return {
        ...state,
        resendCountdown: Math.max(0, state.resendCountdown - 1)
      };

    case 'SET_LOCK_COUNTDOWN':
      return { ...state, lockCountdown: action.seconds };

    case 'TICK_LOCK': {
      const next = Math.max(0, state.lockCountdown - 1);
      return {
        ...state,
        lockCountdown: next,
        otpStatus: next === 0 && state.otpStatus === 'locked' ? 'idle' : state.otpStatus
      };
    }

    case 'INCREMENT_WRONG_ATTEMPTS':
      return { ...state, wrongAttemptsCount: state.wrongAttemptsCount + 1 };

    case 'SET_OFFLINE':
      return { ...state, isOffline: action.isOffline };

    case 'TOGGLE_REDUCED_MOTION':
      return {
        ...state,
        isReducedMotion: action.value !== undefined ? action.value : !state.isReducedMotion
      };

    case 'SHOW_TOAST':
      return { ...state, toastMessage: action.message };

    case 'RESET_FLOW':
      return {
        ...initialFlowState,
        isOffline: state.isOffline,
        isReducedMotion: state.isReducedMotion
      };

    default:
      return state;
  }
}
