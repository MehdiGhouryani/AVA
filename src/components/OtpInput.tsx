import React, { useRef, useEffect, useState } from 'react';
import { t, toEnDigits, toFaDigits } from '../i18n/fa';

interface OtpInputProps {
  value: string; // 0 to 6 digits (ascii)
  onChange: (value: string) => void;
  status: 'idle' | 'verifying' | 'wrong' | 'expired' | 'locked' | 'success';
  disabled?: boolean;
}

export const OtpInput: React.FC<OtpInputProps> = ({
  value,
  onChange,
  status,
  disabled = false
}) => {
  const hiddenInputRef = useRef<HTMLInputElement>(null);
  const [isFocused, setIsFocused] = useState(false);

  // Auto-focus on mount if not disabled
  useEffect(() => {
    if (!disabled && status !== 'locked') {
      hiddenInputRef.current?.focus();
    }
  }, [disabled, status]);

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const raw = e.target.value;
    const clean = toEnDigits(raw).replace(/\D/g, '').slice(0, 6);
    onChange(clean);
  };

  const handleContainerClick = () => {
    if (!disabled && status !== 'locked') {
      hiddenInputRef.current?.focus();
      // Keep selection at end of input
      if (hiddenInputRef.current) {
        const len = hiddenInputRef.current.value.length;
        hiddenInputRef.current.setSelectionRange(len, len);
      }
    }
  };

  // Active index for caret: box index 0 to 5, or -1 when full
  const activeIndex = value.length < 6 ? value.length : -1;

  // Box indices in natural RTL order:
  // In a dir="rtl" flex container, child 0 is rendered at the physical RIGHT.
  // Child 5 is rendered at the physical LEFT.
  // Digit 1 (value[0]) goes into child 0 (the RIGHT-most box).
  // Each next digit moves one box to the LEFT.
  const boxIndices = [0, 1, 2, 3, 4, 5];

  return (
    <div
      onClick={handleContainerClick}
      style={{
        position: 'relative',
        width: '100%',
        display: 'flex',
        justifyContent: 'center',
        padding: '8px 0',
        cursor: disabled ? 'not-allowed' : 'pointer'
      }}
      className={status === 'wrong' ? 'otp-shake' : ''}
    >
      {/* Hidden input driving the visual boxes */}
      <input
        ref={hiddenInputRef}
        type="text"
        inputMode="numeric"
        autoComplete="one-time-code"
        maxLength={6}
        dir="ltr"
        value={value}
        onChange={handleInputChange}
        onFocus={() => setIsFocused(true)}
        onBlur={() => setIsFocused(false)}
        disabled={disabled || status === 'locked'}
        style={{
          position: 'absolute',
          top: 0,
          left: 0,
          width: '100%',
          height: '100%',
          opacity: 0.0001,
          border: 'none',
          outline: 'none',
          cursor: 'pointer',
          zIndex: 10
        }}
        aria-label={t('otp.title')}
      />

      {/* 6 visual boxes in RTL order: child 0 is on the RIGHT */}
      <div
        dir="rtl"
        style={{
          display: 'flex',
          flexDirection: 'row',
          direction: 'rtl',
          alignItems: 'center',
          justifyContent: 'center',
          gap: 'clamp(6px, 2.5vw, 10px)',
          width: '100%'
        }}
      >
        {boxIndices.map((index) => {
          const digit = value[index];
          const hasDigit = digit !== undefined;
          const isActive = isFocused && index === activeIndex && !disabled && status !== 'locked';

          // Box appearance depending on state
          let bg = 'var(--color-cream-box)';
          let border = '1px solid var(--color-line)';
          let digitColor = 'var(--color-evergreen-deep)';

          if (status === 'wrong') {
            bg = 'var(--color-amber-tint)';
            border = '2px solid var(--color-amber)';
            digitColor = 'var(--color-amber-text)';
          } else if (status === 'success') {
            bg = 'rgba(20, 118, 106, 0.12)';
            border = '2px solid var(--color-evergreen)';
            digitColor = 'var(--color-evergreen)';
          } else if (isActive) {
            border = '2.5px solid var(--color-evergreen)';
            bg = '#FFFFFF';
          }

          const boxLabel = t('otp.a11yBox', { index: toFaDigits(index + 1) });

          return (
            <div
              key={index}
              role="textbox"
              aria-label={boxLabel}
              aria-readonly="true"
              style={{
                width: 'clamp(40px, calc((100vw - 48px - 50px) / 6), 52px)',
                height: '64px',
                borderRadius: '18px',
                backgroundColor: bg,
                border,
                boxShadow: 'var(--shadow-box)',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                fontSize: '28px',
                fontWeight: 800,
                color: digitColor,
                position: 'relative',
                transition: 'background-color 200ms ease, border-color 200ms ease',
                boxSizing: 'border-box'
              }}
            >
              {hasDigit ? (
                <span>{toFaDigits(digit)}</span>
              ) : isActive ? (
                <span className="otp-caret" />
              ) : null}
            </div>
          );
        })}
      </div>
    </div>
  );
};
