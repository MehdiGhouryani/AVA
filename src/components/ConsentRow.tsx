import React from 'react';
import { t } from '../i18n/fa';
import { Icon } from './Icon';

interface ConsentRowProps {
  checked: boolean;
  onChange: (checked: boolean) => void;
  onOpenPrivacy: () => void;
  onOpenTerms: () => void;
  pulseGuidance?: boolean;
}

export const ConsentRow: React.FC<ConsentRowProps> = ({
  checked,
  onChange,
  onOpenPrivacy,
  onOpenTerms,
  pulseGuidance = false
}) => {
  return (
    <div
      style={{
        width: '100%',
        boxSizing: 'border-box',
        transition: 'transform 300ms ease'
      }}
      className={pulseGuidance ? 'pulse-attention' : ''}
    >
      {/* Checkbox and consent label row */}
      <label
        style={{
          display: 'flex',
          alignItems: 'center',
          minHeight: '48px',
          gap: '12px',
          cursor: 'pointer',
          userSelect: 'none',
          padding: '4px 0'
        }}
      >
        <input
          type="checkbox"
          checked={checked}
          onChange={(e) => onChange(e.target.checked)}
          style={{
            position: 'absolute',
            opacity: 0,
            width: 0,
            height: 0,
            margin: 0
          }}
          aria-label={t('phone.consent')}
        />

        <div
          style={{
            width: '28px',
            height: '28px',
            minWidth: '28px',
            borderRadius: '8px',
            border: checked ? '2px solid var(--color-evergreen)' : '2px solid var(--color-line)',
            backgroundColor: checked ? 'var(--color-evergreen)' : 'var(--color-field-bg)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            transition: 'background-color 200ms ease, border-color 200ms ease'
          }}
        >
          {checked && <Icon name="check" size={18} color="white" />}
        </div>

        <span
          style={{
            fontSize: '15px',
            fontWeight: 500,
            color: 'var(--color-ink)',
            lineHeight: 1.5
          }}
        >
          {t('phone.consent')}
        </span>
      </label>

      {/* Hairline divider */}
      <div
        style={{
          height: '1px',
          backgroundColor: 'var(--color-line)',
          opacity: 0.6,
          margin: '8px 0 10px 0'
        }}
      />

      {/* Legal links */}
      <div
        style={{
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          gap: '8px',
          fontSize: '14px',
          color: 'var(--color-evergreen)',
          paddingBottom: '4px'
        }}
      >
        <button
          type="button"
          onClick={onOpenPrivacy}
          style={{
            background: 'none',
            border: 'none',
            padding: '6px 8px',
            color: 'var(--color-evergreen)',
            textDecoration: 'underline',
            fontFamily: 'inherit',
            fontSize: '14px',
            fontWeight: 600,
            cursor: 'pointer'
          }}
        >
          {t('phone.linkPrivacy')}
        </button>

        <span style={{ color: 'var(--color-line)' }}>•</span>

        <button
          type="button"
          onClick={onOpenTerms}
          style={{
            background: 'none',
            border: 'none',
            padding: '6px 8px',
            color: 'var(--color-evergreen)',
            textDecoration: 'underline',
            fontFamily: 'inherit',
            fontSize: '14px',
            fontWeight: 600,
            cursor: 'pointer'
          }}
        >
          {t('phone.linkTerms')}
        </button>
      </div>
    </div>
  );
};
