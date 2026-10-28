import React, { useRef, useState, useId } from 'react';
import { t, toEnDigits, toFaDigits } from '../i18n/fa';

interface PhoneFieldProps {
  value: string; // canonical or raw
  onChange: (raw: string, canonical: string, isValid: boolean) => void;
  error: string | null;
  onBlurValidate: () => void;
  disabled?: boolean;
}

/**
 * Normalizes input phone to canonical 09xxxxxxxxx format
 */
export function normalizePhone(input: string): { canonical: string; isValid: boolean } {
  // Strip non-digits after converting to English
  const enDigits = toEnDigits(input).replace(/[^\d+]/g, '');

  let stripped = enDigits;
  if (stripped.startsWith('+98')) {
    stripped = '0' + stripped.slice(3);
  } else if (stripped.startsWith('0098')) {
    stripped = '0' + stripped.slice(4);
  } else if (stripped.startsWith('9') && stripped.length <= 10) {
    stripped = '0' + stripped;
  }

  // Remove any remaining pluses or non-digits
  const cleanDigits = stripped.replace(/\D/g, '');
  const canonical = cleanDigits.slice(0, 11);
  const isValid = /^09\d{9}$/.test(canonical);

  return { canonical, isValid };
}

export const PhoneField: React.FC<PhoneFieldProps> = ({
  value,
  onChange,
  error,
  onBlurValidate,
  disabled = false
}) => {
  const [isFocused, setIsFocused] = useState(false);
  const inputRef = useRef<HTMLInputElement>(null);
  const inputId = useId();

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const rawVal = e.target.value;
    const { canonical, isValid } = normalizePhone(rawVal);
    onChange(canonical, canonical, isValid);
  };

  const handleBlur = () => {
    setIsFocused(false);
    onBlurValidate();
  };

  // Convert canonical value to Persian digits for display
  const displayValue = toFaDigits(value);

  // Border & Glow styling
  let borderColor = 'var(--color-line)';
  let boxShadow = 'none';

  if (error) {
    borderColor = 'var(--color-amber)';
    boxShadow = '0 0 0 3px rgba(227, 144, 15, 0.2)';
  } else if (isFocused) {
    borderColor = 'var(--color-apricot)';
    boxShadow = '0 0 0 4px rgba(255, 189, 89, 0.3)';
  }

  return (
    <div style={{ width: '100%', boxSizing: 'border-box' }}>
      <label
        htmlFor={inputId}
        style={{
          display: 'block',
          marginBottom: '8px',
          fontSize: '16px',
          fontWeight: 600,
          color: 'var(--color-ink)',
          textAlign: 'right'
        }}
      >
        {t('phone.label')}
      </label>

      <div
        onClick={() => inputRef.current?.focus()}
        style={{
          display: 'flex',
          alignItems: 'center',
          height: '60px',
          backgroundColor: 'var(--color-field-bg)',
          borderRadius: '20px',
          border: `2px solid ${borderColor}`,
          boxShadow,
          padding: '0 20px',
          boxSizing: 'border-box',
          cursor: disabled ? 'not-allowed' : 'text',
          opacity: disabled ? 0.7 : 1,
          transition: 'border-color 200ms ease, box-shadow 200ms ease'
        }}
      >
        <input
          id={inputId}
          ref={inputRef}
          type="tel"
          inputMode="numeric"
          autoComplete="tel"
          dir="ltr"
          value={displayValue}
          onChange={handleInputChange}
          onFocus={() => setIsFocused(true)}
          onBlur={handleBlur}
          disabled={disabled}
          placeholder={t('phone.placeholder')}
          style={{
            width: '100%',
            height: '100%',
            backgroundColor: 'transparent',
            border: 'none',
            outline: 'none',
            fontSize: '20px',
            fontWeight: 700,
            fontFamily: 'inherit',
            color: 'var(--color-evergreen-deep)',
            textAlign: 'right',
            letterSpacing: '0.5px'
          }}
          aria-invalid={!!error}
          aria-describedby={error ? 'phone-error-desc' : undefined}
        />
      </div>
    </div>
  );
};
