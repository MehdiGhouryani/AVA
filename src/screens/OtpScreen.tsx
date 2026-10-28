import React, { useEffect, useRef } from 'react';
import { asset } from '../assets/assets';
import { t, toFaDigits, formatCountdownFa } from '../i18n/fa';
import { tokens } from '../design/tokens';
import { LogoImage } from '../components/LogoImage';
import { OtpInput } from '../components/OtpInput';
import { PrimaryButton } from '../components/PrimaryButton';
import { Icon } from '../components/Icon';
import { authService } from '../services/auth';
import { OtpStatus } from '../app/flow';

interface OtpScreenProps {
  phone: string;
  code: string;
  status: OtpStatus;
  resendCountdown: number;
  lockCountdown: number;
  onCodeChange: (code: string) => void;
  onStatusChange: (status: OtpStatus) => void;
  onTickResend: () => void;
  onTickLock: () => void;
  onResetResendTimer: (sec: number) => void;
  onEditPhone: () => void;
  onSuccess: () => void;
  onShowToast: (message: string) => void;
  onIncrementWrongAttempts: () => void;
  onSetLockCountdown: (sec: number) => void;
}

export const OtpScreen: React.FC<OtpScreenProps> = ({
  phone,
  code,
  status,
  resendCountdown,
  lockCountdown,
  onCodeChange,
  onStatusChange,
  onTickResend,
  onTickLock,
  onResetResendTimer,
  onEditPhone,
  onSuccess,
  onShowToast,
  onIncrementWrongAttempts,
  onSetLockCountdown
}) => {
  const autoVerifyTimeoutRef = useRef<any>(null);

  // Timer intervals
  useEffect(() => {
    const timer = setInterval(() => {
      if (resendCountdown > 0) {
        onTickResend();
      }
      if (lockCountdown > 0) {
        onTickLock();
      }
    }, 1000);

    return () => clearInterval(timer);
  }, [resendCountdown, lockCountdown, onTickResend, onTickLock]);

  // Auto-verify trigger when 6 digits are typed
  useEffect(() => {
    if (code.length === 6 && status === 'idle') {
      if (autoVerifyTimeoutRef.current) {
        clearTimeout(autoVerifyTimeoutRef.current);
      }
      autoVerifyTimeoutRef.current = setTimeout(() => {
        handleVerify(code);
      }, 250);
    }

    return () => {
      if (autoVerifyTimeoutRef.current) {
        clearTimeout(autoVerifyTimeoutRef.current);
      }
    };
  }, [code, status]);

  const handleVerify = async (otpToVerify: string) => {
    if (status === 'verifying' || status === 'locked') return;

    onStatusChange('verifying');

    try {
      const res = await authService.verifyOtp(phone, otpToVerify);

      if (res.ok) {
        onStatusChange('success');
        setTimeout(() => {
          onSuccess();
        }, 600);
      } else {
        if (res.reason === 'wrong') {
          onIncrementWrongAttempts();
          onStatusChange('wrong');
        } else if (res.reason === 'expired') {
          onStatusChange('expired');
        } else if (res.reason === 'locked') {
          onSetLockCountdown(tokens.auth_mock.lockSeconds);
          onStatusChange('locked');
        } else {
          onStatusChange('wrong');
        }
      }
    } catch {
      onStatusChange('wrong');
    }
  };

  const handleResend = async () => {
    if (resendCountdown > 0 || status === 'locked') return;

    onCodeChange('');
    onStatusChange('idle');
    onResetResendTimer(tokens.auth_mock.resendSeconds);
    onShowToast(t('otp.resent'));

    try {
      await authService.requestOtp(phone);
    } catch {
      // Mock failure handled quietly
    }
  };

  const formattedPhone = toFaDigits(phone);
  const subtitleText = t('otp.subtitle', { phone: formattedPhone });

  const bg = asset('bg.garden');

  return (
    <div
      style={{
        position: 'relative',
        width: '100%',
        height: '100%',
        backgroundColor: 'var(--color-paper)',
        display: 'flex',
        flexDirection: 'column',
        boxSizing: 'border-box',
        overflow: 'hidden'
      }}
    >
      {/* Top 36% Scene */}
      <div
        style={{
          height: '36%',
          minHeight: '160px',
          width: '100%',
          backgroundImage: `url(${bg.src})`,
          backgroundSize: 'cover',
          backgroundPosition: '50% 0%',
          backgroundRepeat: 'no-repeat',
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
          justifyContent: 'center',
          padding: '16px 20px',
          boxSizing: 'border-box',
          position: 'relative'
        }}
      >
        <LogoImage height={Math.min(tokens.size_px.logoHeightOtp, 76)} />

        {/* Tagline (hidden on short screens < 640px) */}
        <span
          className="otp-tagline"
          style={{
            fontSize: '13px',
            fontWeight: 600,
            color: 'var(--color-evergreen-deep)',
            marginTop: '8px',
            backgroundColor: 'rgba(254, 247, 225, 0.85)',
            padding: '3px 12px',
            borderRadius: '999px',
            backdropFilter: 'blur(4px)'
          }}
        >
          {t('brand.tagline')}
        </span>
      </div>

      {/* Bottom Sheet */}
      <div
        style={{
          flex: 1,
          width: '100%',
          backgroundColor: 'var(--color-cream)',
          borderTopLeftRadius: `${tokens.radius_px.sheetTop}px`,
          borderTopRightRadius: `${tokens.radius_px.sheetTop}px`,
          boxShadow: 'var(--shadow-sheet)',
          position: 'relative',
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
          padding: '24px 20px calc(env(safe-area-inset-bottom, 16px) + 16px) 20px',
          boxSizing: 'border-box',
          overflowY: 'auto'
        }}
      >
        {/* Leaf-sprig corner ornaments in top corners */}
        <div
          style={{
            position: 'absolute',
            top: '16px',
            right: '20px',
            opacity: 0.45,
            pointerEvents: 'none'
          }}
        >
          <Icon name="leaf" size={24} color="var(--color-evergreen)" />
        </div>
        <div
          style={{
            position: 'absolute',
            top: '16px',
            left: '20px',
            opacity: 0.45,
            transform: 'scaleX(-1)',
            pointerEvents: 'none'
          }}
        >
          <Icon name="leaf" size={24} color="var(--color-evergreen)" />
        </div>

        <div
          style={{
            width: '100%',
            maxWidth: '360px',
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center'
          }}
        >
          {/* 1. Title */}
          <h2
            style={{
              margin: '0 0 6px 0',
              fontSize: 'clamp(20px, 5.5vw, 24px)',
              fontWeight: 800,
              color: 'var(--color-evergreen-deep)',
              textAlign: 'center'
            }}
          >
            {t('otp.title')}
          </h2>

          {/* 2. Subtitle with phone number */}
          <p
            style={{
              margin: '0 0 12px 0',
              fontSize: '15px',
              fontWeight: 500,
              color: 'var(--color-text-soft)',
              textAlign: 'center',
              lineHeight: 1.5
            }}
          >
            {subtitleText.split(formattedPhone).map((segment, idx, arr) => (
              <React.Fragment key={idx}>
                {segment}
                {idx < arr.length - 1 && (
                  <span
                    dir="ltr"
                    style={{
                      color: 'var(--color-berry)',
                      fontWeight: 700,
                      margin: '0 4px',
                      display: 'inline-block'
                    }}
                  >
                    {formattedPhone}
                  </span>
                )}
              </React.Fragment>
            ))}
          </p>

          {/* 3. Edit Phone Button */}
          <button
            type="button"
            onClick={onEditPhone}
            style={{
              display: 'inline-flex',
              alignItems: 'center',
              justifyContent: 'center',
              gap: '6px',
              height: '42px',
              minHeight: '42px',
              padding: '0 16px',
              borderRadius: '999px',
              border: '2px solid var(--color-evergreen)',
              backgroundColor: 'transparent',
              color: 'var(--color-evergreen)',
              fontSize: '14px',
              fontWeight: 600,
              fontFamily: 'inherit',
              cursor: 'pointer',
              marginBottom: '16px'
            }}
          >
            <Icon name="pencil" size={16} color="var(--color-evergreen)" />
            <span>{t('otp.editPhone')}</span>
          </button>

          {/* 4. OtpInput (6 visual boxes) */}
          <div style={{ width: '100%', marginBottom: '6px' }}>
            <OtpInput
              value={code}
              onChange={(newCode) => {
                onCodeChange(newCode);
                if (status === 'wrong') {
                  onStatusChange('idle');
                }
              }}
              status={status}
              disabled={status === 'verifying' || status === 'locked'}
            />
          </div>

          {/* 5. Message Line (Reserved Height) */}
          <div
            role="status"
            aria-live="polite"
            style={{
              minHeight: '26px',
              width: '100%',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              gap: '6px',
              margin: '4px 0 10px 0',
              textAlign: 'center'
            }}
          >
            {status === 'verifying' && (
              <span style={{ fontSize: '14px', fontWeight: 600, color: 'var(--color-evergreen)' }}>
                {t('otp.verifying')}
              </span>
            )}
            {status === 'wrong' && (
              <>
                <Icon name="info" size={16} color="var(--color-amber)" />
                <span
                  style={{
                    fontSize: '13px',
                    fontWeight: 600,
                    color: 'var(--color-amber-text)'
                  }}
                >
                  {t('otp.wrong')}
                </span>
              </>
            )}
            {status === 'expired' && (
              <>
                <Icon name="info" size={16} color="var(--color-amber)" />
                <span
                  style={{
                    fontSize: '13px',
                    fontWeight: 600,
                    color: 'var(--color-amber-text)'
                  }}
                >
                  {t('otp.expired')}
                </span>
              </>
            )}
            {status === 'locked' && (
              <>
                <Icon name="info" size={16} color="var(--color-amber)" />
                <span
                  style={{
                    fontSize: '13px',
                    fontWeight: 600,
                    color: 'var(--color-amber-text)'
                  }}
                >
                  {t('otp.locked')}{' '}
                  {lockCountdown > 0 && `(${toFaDigits(lockCountdown)} ثانیه)`}
                </span>
              </>
            )}
            {status === 'success' && (
              <span
                style={{
                  fontSize: '15px',
                  fontWeight: 700,
                  color: 'var(--color-evergreen)'
                }}
              >
                {t('otp.success')}
              </span>
            )}
          </div>

          {/* 6. Timer / Resend Row */}
          <div
            style={{
              width: '100%',
              display: 'flex',
              justifyContent: 'center',
              alignItems: 'center',
              minHeight: '48px',
              marginBottom: '16px'
            }}
          >
            {resendCountdown > 0 ? (
              <span
                style={{
                  fontSize: '14px',
                  fontWeight: 600,
                  color: 'var(--color-muted)'
                }}
              >
                {t('otp.resendIn').split('{time}').map((segment, idx, arr) => (
                  <React.Fragment key={idx}>
                    {segment}
                    {idx < arr.length - 1 && (
                      <span
                        dir="ltr"
                        style={{
                          color: 'var(--color-berry)',
                          fontWeight: 700,
                          margin: '0 4px',
                          display: 'inline-block'
                        }}
                      >
                        {formatCountdownFa(resendCountdown)}
                      </span>
                    )}
                  </React.Fragment>
                ))}
              </span>
            ) : (
              <button
                type="button"
                onClick={handleResend}
                disabled={status === 'locked'}
                style={{
                  display: 'inline-flex',
                  alignItems: 'center',
                  gap: '6px',
                  background: 'none',
                  border: 'none',
                  color: status === 'locked' ? 'var(--color-muted)' : 'var(--color-evergreen)',
                  fontSize: '15px',
                  fontWeight: 700,
                  fontFamily: 'inherit',
                  cursor: status === 'locked' ? 'not-allowed' : 'pointer',
                  minHeight: '48px',
                  padding: '8px 16px',
                  borderRadius: '999px',
                  backgroundColor: 'rgba(20, 118, 106, 0.08)'
                }}
              >
                <Icon name="refresh" size={18} color="currentColor" />
                <span>{t('otp.resend')}</span>
              </button>
            )}
          </div>

          {/* 7. CTA Button */}
          <div style={{ width: '100%', marginTop: 'auto' }}>
            <PrimaryButton
              label={t('otp.cta')}
              onClick={() => handleVerify(code)}
              disabled={code.length < 6 || status === 'verifying' || status === 'locked'}
              loading={status === 'verifying'}
              loadingLabel={t('otp.verifying')}
              height={tokens.size_px.ctaHeightForm}
            />
          </div>
        </div>
      </div>
    </div>
  );
};
