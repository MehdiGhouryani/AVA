import React, { useState } from 'react';
import { t } from '../i18n/fa';

interface SkipButtonProps {
  onClick: () => void;
  className?: string;
  style?: React.CSSProperties;
}

export const SkipButton: React.FC<SkipButtonProps> = ({ onClick, className = '', style }) => {
  const [isPressed, setIsPressed] = useState(false);

  return (
    <button
      type="button"
      onClick={onClick}
      onMouseDown={() => setIsPressed(true)}
      onMouseUp={() => setIsPressed(false)}
      onMouseLeave={() => setIsPressed(false)}
      onTouchStart={() => setIsPressed(true)}
      onTouchEnd={() => setIsPressed(false)}
      aria-label={t('onboarding.skip')}
      style={{
        display: 'inline-flex',
        alignItems: 'center',
        justifyContent: 'center',
        minHeight: '48px',
        minWidth: '48px',
        padding: '8px 16px',
        borderRadius: '999px',
        backgroundColor: 'rgba(254, 247, 225, 0.88)',
        backdropFilter: 'blur(4px)',
        WebkitBackdropFilter: 'blur(4px)',
        border: '1px solid rgba(199, 221, 210, 0.6)',
        color: 'var(--color-muted)',
        fontSize: '15px',
        fontWeight: 600,
        fontFamily: 'inherit',
        cursor: 'pointer',
        userSelect: 'none',
        outline: 'none',
        transform: isPressed ? 'scale(0.95)' : 'scale(1)',
        transition: 'transform var(--motion-press-ms) ease, background-color 150ms ease',
        ...style
      }}
      className={`skip-btn ${className}`}
    >
      {t('onboarding.skip')}
    </button>
  );
};
