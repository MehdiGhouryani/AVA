import React from 'react';
import { asset } from '../assets/assets';
import { t } from '../i18n/fa';
import { LogoImage } from '../components/LogoImage';

interface HomeScreenProps {
  onRestart: () => void;
}

export const HomeScreen: React.FC<HomeScreenProps> = ({ onRestart }) => {
  const bg = asset('bg.garden');
  const ava = asset('char.ava.wave_book');

  return (
    <div
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
        padding: 'max(env(safe-area-inset-top, 24px), 24px) 24px calc(env(safe-area-inset-bottom, 24px) + 20px) 24px',
        boxSizing: 'border-box',
        overflow: 'hidden'
      }}
    >
      {/* Top Logo */}
      <div style={{ marginTop: '10px' }}>
        <LogoImage height={76} />
      </div>

      {/* Middle Welcome & Ava */}
      <div
        style={{
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
          textAlign: 'center',
          gap: '16px',
          width: '100%',
          maxWidth: '320px'
        }}
      >
        {/* Waving Ava */}
        <div style={{ width: '160px', height: 'auto' }}>
          <img
            src={ava.src}
            alt="آوا"
            style={{ width: '100%', height: 'auto', display: 'block' }}
          />
        </div>

        {/* Welcome Card */}
        <div
          style={{
            backgroundColor: 'rgba(254, 247, 225, 0.92)',
            backdropFilter: 'blur(8px)',
            borderRadius: '24px',
            padding: '20px 24px',
            border: '1px solid rgba(199, 221, 210, 0.7)',
            boxShadow: '0 8px 24px rgba(20, 60, 50, 0.12)'
          }}
        >
          <h1
            style={{
              margin: '0 0 8px 0',
              fontSize: '24px',
              fontWeight: 800,
              color: 'var(--color-evergreen-deep)'
            }}
          >
            {t('home.title')}
          </h1>

          <p
            style={{
              margin: 0,
              fontSize: '15px',
              lineHeight: 1.6,
              color: 'var(--color-text-soft)'
            }}
          >
            {t('home.placeholderNote')}
          </p>
        </div>
      </div>

      {/* Bottom Restart Button */}
      <div>
        <button
          type="button"
          onClick={onRestart}
          style={{
            minHeight: '48px',
            padding: '10px 24px',
            borderRadius: '999px',
            backgroundColor: 'var(--color-evergreen)',
            color: '#FFFFFF',
            fontSize: '15px',
            fontWeight: 700,
            border: 'none',
            cursor: 'pointer',
            boxShadow: 'var(--shadow-cta)',
            fontFamily: 'inherit'
          }}
        >
          {t('home.restart')}
        </button>
      </div>
    </div>
  );
};
