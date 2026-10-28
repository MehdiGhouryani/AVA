import React, { useState } from 'react';
import { asset } from '../assets/assets';
import { t } from '../i18n/fa';
import { tokens } from '../design/tokens';
import { LogoImage } from '../components/LogoImage';
import { PhoneField, normalizePhone } from '../components/PhoneField';
import { ConsentRow } from '../components/ConsentRow';
import { PrimaryButton } from '../components/PrimaryButton';
import { OfflineBanner } from '../components/OfflineBanner';
import { BottomSheet } from '../components/BottomSheet';
import { Icon } from '../components/Icon';
import { authService } from '../services/auth';

interface PhoneScreenProps {
  phone: string;
  consent: boolean;
  isOffline: boolean;
  providerError: boolean;
  isLoading: boolean;
  onUpdatePhone: (raw: string, canonical: string) => void;
  onUpdateConsent: (checked: boolean) => void;
  onSuccess: () => void;
  onErrorState: (error: string | null) => void;
  onSetLoading: (loading: boolean) => void;
  onSetProviderError: (error: boolean) => void;
}

export const PhoneScreen: React.FC<PhoneScreenProps> = ({
  phone,
  consent,
  isOffline,
  providerError,
  isLoading,
  onUpdatePhone,
  onUpdateConsent,
  onSuccess,
  onErrorState,
  onSetLoading,
  onSetProviderError
}) => {
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [showConsentHint, setShowConsentHint] = useState(false);
  const [pulseConsent, setPulseConsent] = useState(false);
  const [activeLegalModal, setActiveLegalModal] = useState<'privacy' | 'terms' | null>(null);

  const { isValid } = normalizePhone(phone);
  const isFormValid = isValid && consent;

  const handlePhoneChange = (raw: string, canonical: string, valid: boolean) => {
    onUpdatePhone(raw, canonical);
    if (errorMessage && valid) {
      setErrorMessage(null);
      onErrorState(null);
    }
  };

  const handleBlurValidate = () => {
    if (phone.length > 0 && !isValid) {
      setErrorMessage(t('phone.errorInvalid'));
      onErrorState(t('phone.errorInvalid'));
    }
  };

  const handleCtaClick = async () => {
    if (isLoading) return;

    if (!isValid) {
      setErrorMessage(t('phone.errorInvalid'));
      onErrorState(t('phone.errorInvalid'));
      return;
    }

    if (!consent) {
      setShowConsentHint(true);
      setPulseConsent(true);
      setTimeout(() => setPulseConsent(false), 600);
      return;
    }

    // Clear any previous error hints
    setErrorMessage(null);
    setShowConsentHint(false);
    onSetProviderError(false);
    onSetLoading(true);

    try {
      const res = await authService.requestOtp(phone);
      onSetLoading(false);

      if (res.ok) {
        onSuccess();
      } else {
        onSetProviderError(true);
      }
    } catch {
      onSetLoading(false);
      onSetProviderError(true);
    }
  };

  const bg = asset('bg.login');

  return (
    <div
      style={{
        position: 'relative',
        width: '100%',
        height: '100%',
        backgroundColor: 'var(--color-paper-login)',
        backgroundImage: `url(${bg.src})`,
        backgroundSize: 'cover',
        backgroundPosition: '50% 100%',
        backgroundRepeat: 'no-repeat',
        display: 'flex',
        flexDirection: 'column',
        boxSizing: 'border-box',
        overflowY: 'auto',
        overflowX: 'hidden'
      }}
    >
      {/* Background Corner Decorations (physical placement) */}
      <div style={{ position: 'absolute', inset: 0, pointerEvents: 'none', zIndex: 1 }}>
        {/* Top-Left Leaves */}
        <img
          src={asset('decor.login.top_left').src}
          alt=""
          style={{
            position: 'absolute',
            left: 0, // physical left
            top: 0,
            width: 'clamp(140px, 38vw, 210px)',
            height: 'auto',
            display: 'block'
          }}
        />

        {/* Bottom-Left Bush with Stone */}
        <img
          src={asset('decor.login.bottom_left').src}
          alt=""
          style={{
            position: 'absolute',
            left: 0, // physical left
            bottom: 0,
            width: 'clamp(120px, 32vw, 190px)',
            height: 'auto',
            display: 'block'
          }}
        />

        {/* Bottom-Right Flowers */}
        <img
          src={asset('decor.login.bottom_right').src}
          alt=""
          style={{
            position: 'absolute',
            right: 0, // physical right
            bottom: 0,
            width: 'clamp(110px, 30vw, 170px)',
            height: 'auto',
            display: 'block'
          }}
        />
      </div>

      {/* 1. Offline Banner */}
      <div style={{ position: 'relative', zIndex: 20 }}>
        <OfflineBanner show={isOffline} />
      </div>

      {/* Main Content Container */}
      <div
        style={{
          position: 'relative',
          zIndex: 10,
          flex: 1,
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
          padding: 'min(4dvh, 24px) 24px calc(env(safe-area-inset-bottom, 16px) + 20px) 24px',
          boxSizing: 'border-box',
          width: '100%',
          maxWidth: '380px',
          margin: '0 auto'
        }}
      >
        {/* 2. Logo and Tagline */}
        <div
          style={{
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            marginBottom: '16px'
          }}
        >
          <LogoImage height={Math.min(tokens.size_px.logoHeightForm, 90)} />

          {/* Tagline flanked by two tiny leaf glyphs */}
          <div
            style={{
              display: 'flex',
              alignItems: 'center',
              gap: '6px',
              marginTop: '8px',
              color: 'var(--color-evergreen)'
            }}
          >
            <Icon name="leaf" size={14} color="var(--color-evergreen)" />
            <span
              style={{
                fontSize: '13px',
                fontWeight: 600,
                color: 'var(--color-evergreen)',
                letterSpacing: '0.2px'
              }}
            >
              {t('brand.tagline')}
            </span>
            <span style={{ transform: 'scaleX(-1)', display: 'inline-block' }}>
              <Icon name="leaf" size={14} color="var(--color-evergreen)" />
            </span>
          </div>
        </div>

        {/* 3. Title */}
        <h2
          style={{
            margin: '0 0 16px 0',
            fontSize: 'clamp(20px, 5.5vw, 24px)',
            fontWeight: 800,
            color: 'var(--color-evergreen-deep)',
            textAlign: 'center'
          }}
        >
          {t('phone.title')}
        </h2>

        {/* 4. Phone Input */}
        <div style={{ width: '100%' }}>
          <PhoneField
            value={phone}
            onChange={handlePhoneChange}
            error={errorMessage}
            onBlurValidate={handleBlurValidate}
            disabled={isLoading}
          />
        </div>

        {/* 5. Reserved Helper/Error Line */}
        <div
          id="phone-error-desc"
          role="status"
          aria-live="polite"
          style={{
            width: '100%',
            minHeight: '26px',
            display: 'flex',
            alignItems: 'center',
            gap: '6px',
            padding: '4px 4px 0 4px',
            boxSizing: 'border-box'
          }}
        >
          {errorMessage && (
            <>
              <Icon name="info" size={16} color="var(--color-amber)" />
              <span
                style={{
                  fontSize: '13px',
                  fontWeight: 600,
                  color: 'var(--color-amber-text)'
                }}
              >
                {errorMessage}
              </span>
            </>
          )}
          {!errorMessage && showConsentHint && !consent && (
            <>
              <Icon name="info" size={16} color="var(--color-amber)" />
              <span
                style={{
                  fontSize: '13px',
                  fontWeight: 600,
                  color: 'var(--color-amber-text)'
                }}
              >
                {t('phone.consentHint')}
              </span>
            </>
          )}
        </div>

        {/* 6. Consent Checkbox & Legal Links */}
        <div style={{ width: '100%', marginTop: '4px' }}>
          <ConsentRow
            checked={consent}
            onChange={(checked) => {
              onUpdateConsent(checked);
              if (checked) setShowConsentHint(false);
            }}
            onOpenPrivacy={() => setActiveLegalModal('privacy')}
            onOpenTerms={() => setActiveLegalModal('terms')}
            pulseGuidance={pulseConsent}
          />
        </div>

        {/* Provider Error Banner (if triggered) */}
        {providerError && (
          <div
            role="status"
            aria-live="polite"
            style={{
              width: '100%',
              backgroundColor: 'var(--color-amber-tint)',
              color: 'var(--color-amber-text)',
              padding: '8px 12px',
              borderRadius: '12px',
              fontSize: '13px',
              fontWeight: 600,
              display: 'flex',
              alignItems: 'center',
              gap: '6px',
              marginBottom: '10px',
              boxSizing: 'border-box'
            }}
          >
            <Icon name="info" size={16} color="var(--color-amber)" />
            <span>{t('phone.providerError')}</span>
          </div>
        )}

        {/* 7. CTA Button */}
        <div style={{ width: '100%', marginTop: 'auto', paddingTop: '16px' }}>
          <PrimaryButton
            label={t('phone.cta')}
            onClick={handleCtaClick}
            disabled={!isFormValid && !errorMessage && !showConsentHint}
            loading={isLoading}
            loadingLabel={t('phone.sending')}
            height={tokens.size_px.ctaHeightForm}
          />
        </div>
      </div>

      {/* Legal Bottom Sheet */}
      <BottomSheet
        isOpen={activeLegalModal !== null}
        onClose={() => setActiveLegalModal(null)}
        title={activeLegalModal === 'privacy' ? t('phone.linkPrivacy') : t('phone.linkTerms')}
        content={
          activeLegalModal === 'privacy'
            ? t('phone.legalPlaceholderPrivacy')
            : t('phone.legalPlaceholderTerms')
        }
      />
    </div>
  );
};
