import React from 'react';
import { t } from '../i18n/fa';
import { Icon } from './Icon';

interface OfflineBannerProps {
  show: boolean;
}

export const OfflineBanner: React.FC<OfflineBannerProps> = ({ show }) => {
  if (!show) return null;

  return (
    <div
      role="status"
      aria-live="polite"
      style={{
        width: '100%',
        backgroundColor: 'var(--color-banner-bg)',
        color: 'var(--color-amber-text)',
        padding: '10px 16px',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        gap: '8px',
        fontSize: '13px',
        fontWeight: 600,
        boxSizing: 'border-box',
        borderBottom: '1px solid rgba(227, 144, 15, 0.3)',
        animation: 'slideDown 250ms ease'
      }}
    >
      <Icon name="info" size={18} color="var(--color-amber)" />
      <span>{t('phone.offline')}</span>
    </div>
  );
};
