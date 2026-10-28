import React, { useEffect } from 'react';
import { asset } from '../assets/assets';
import { t } from '../i18n/fa';
import { tokens } from '../design/tokens';
import { LogoImage } from '../components/LogoImage';

interface SplashScreenProps {
  onAdvance: () => void;
  isReducedMotion?: boolean;
}

export const SplashScreen: React.FC<SplashScreenProps> = ({ onAdvance, isReducedMotion = false }) => {
  const bg = asset('bg.splash');

  useEffect(() => {
    const timer = setTimeout(() => {
      onAdvance();
    }, tokens.motion.splashMinMs);

    return () => clearTimeout(timer);
  }, [onAdvance]);

  return (
    <div
      onClick={onAdvance}
      style={{
        position: 'relative',
        width: '100%',
        height: '100%',
        backgroundColor: 'var(--color-paper)',
        backgroundImage: `url(${bg.src})`,
        backgroundSize: 'cover',
        backgroundPosition: '50% 100%',
        backgroundRepeat: 'no-repeat',
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'space-between',
        cursor: 'pointer',
        userSelect: 'none',
        padding: 'env(safe-area-inset-top, 24px) 20px env(safe-area-inset-bottom, 24px) 20px',
        boxSizing: 'border-box'
      }}
    >
      {/* Top Logo Container */}
      <div
        style={{
          marginTop: 'min(12dvh, 90px)',
          animation: isReducedMotion ? 'none' : 'fadeInUp 450ms ease forwards',
          display: 'flex',
          justifyContent: 'center',
          alignItems: 'center'
        }}
      >
        <LogoImage height={Math.min(tokens.size_px.logoHeightSplash, 120)} />
      </div>

      {/* Bottom Loader Container */}
      <div
        role="status"
        aria-label={t('splash.a11yLoading')}
        style={{
          marginBottom: 'min(12dvh, 90px)',
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
          gap: '12px'
        }}
      >
        {/* Pulsing Dots */}
        <div style={{ display: 'flex', gap: '8px', alignItems: 'center' }}>
          {[0, 1, 2].map((i) => (
            <span
              key={i}
              style={{
                width: '10px',
                height: '10px',
                borderRadius: '50%',
                backgroundColor: 'var(--color-evergreen)',
                display: 'inline-block',
                animation: isReducedMotion
                  ? 'none'
                  : `pulseLoader 1.2s ease-in-out ${i * 0.2}s infinite`
              }}
            />
          ))}
        </div>

        {/* Loading Caption */}
        <p
          style={{
            margin: 0,
            fontSize: '16px',
            fontWeight: 600,
            color: 'var(--color-muted)',
            textAlign: 'center',
            letterSpacing: '0.2px'
          }}
        >
          {t('splash.loading')}
        </p>
      </div>
    </div>
  );
};
