import React, { useState } from 'react';
import { Icon } from './Icon';

interface PrimaryButtonProps {
  label: string;
  onClick?: () => void;
  disabled?: boolean;
  loading?: boolean;
  loadingLabel?: string;
  height?: number;
  showArrow?: boolean;
  width?: string;
  className?: string;
  style?: React.CSSProperties;
  ariaLabel?: string;
  id?: string;
}

export const PrimaryButton: React.FC<PrimaryButtonProps> = ({
  label,
  onClick,
  disabled = false,
  loading = false,
  loadingLabel,
  height = 64,
  showArrow = true,
  width = '100%',
  className = '',
  style,
  ariaLabel,
  id
}) => {
  const [isPressed, setIsPressed] = useState(false);

  const handleClick = (e: React.MouseEvent) => {
    e.preventDefault();
    if (onClick) {
      onClick();
    }
  };

  return (
    <button
      id={id}
      type="button"
      onClick={handleClick}
      onMouseDown={() => setIsPressed(true)}
      onMouseUp={() => setIsPressed(false)}
      onMouseLeave={() => setIsPressed(false)}
      onTouchStart={() => setIsPressed(true)}
      onTouchEnd={() => setIsPressed(false)}
      aria-disabled={disabled}
      aria-busy={loading}
      aria-label={ariaLabel || label}
      style={{
        display: 'inline-flex',
        alignItems: 'center',
        justifyContent: 'center',
        gap: '12px',
        width,
        height: `${height}px`,
        minHeight: `${Math.max(48, height)}px`,
        borderRadius: '999px',
        border: 'none',
        outline: 'none',
        cursor: loading ? 'wait' : 'pointer',
        backgroundColor: disabled ? 'var(--color-disabled-bg)' : 'var(--color-evergreen)',
        color: disabled ? 'var(--color-disabled-text)' : 'var(--color-white)',
        boxShadow: disabled ? 'none' : 'var(--shadow-cta)',
        fontSize: '18px',
        fontWeight: 800,
        fontFamily: 'inherit',
        padding: '0 24px',
        boxSizing: 'border-box',
        transform: isPressed && !loading ? 'scale(0.97)' : 'scale(1)',
        transition: 'transform var(--motion-press-ms) ease, background-color 200ms ease, box-shadow 200ms ease',
        userSelect: 'none',
        WebkitTapHighlightColor: 'transparent',
        ...style
      }}
      className={`primary-cta-btn ${className}`}
    >
      {loading ? (
        <>
          <span
            style={{
              width: '20px',
              height: '20px',
              border: '2.5px solid rgba(255, 255, 255, 0.4)',
              borderTopColor: '#ffffff',
              borderRadius: '50%',
              display: 'inline-block',
              animation: 'spin 0.8s linear infinite'
            }}
          />
          <span>{loadingLabel || label}</span>
        </>
      ) : (
        <>
          <span>{label}</span>
          {showArrow && (
            <Icon
              name="arrow-left"
              size={20}
              color={disabled ? 'var(--color-disabled-text)' : 'var(--color-white)'}
            />
          )}
        </>
      )}
    </button>
  );
};
