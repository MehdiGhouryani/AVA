import React from 'react';
import { t } from '../i18n/fa';
import { Icon } from './Icon';

interface BottomSheetProps {
  isOpen: boolean;
  onClose: () => void;
  title: string;
  content: string;
}

export const BottomSheet: React.FC<BottomSheetProps> = ({ isOpen, onClose, title, content }) => {
  if (!isOpen) return null;

  return (
    <div
      style={{
        position: 'fixed',
        inset: 0,
        backgroundColor: 'rgba(35, 61, 71, 0.45)',
        backdropFilter: 'blur(4px)',
        WebkitBackdropFilter: 'blur(4px)',
        zIndex: 999,
        display: 'flex',
        alignItems: 'flex-end',
        justifyContent: 'center',
        animation: 'fadeIn 200ms ease'
      }}
      onClick={onClose}
    >
      <div
        onClick={(e) => e.stopPropagation()}
        style={{
          width: '100%',
          maxWidth: '430px',
          backgroundColor: 'var(--color-cream)',
          borderTopLeftRadius: '32px',
          borderTopRightRadius: '32px',
          boxShadow: 'var(--shadow-sheet)',
          padding: '24px 24px 36px 24px',
          boxSizing: 'border-box',
          animation: 'slideUp 250ms ease',
          maxHeight: '75vh',
          display: 'flex',
          flexDirection: 'column',
          gap: '16px'
        }}
      >
        {/* Header with handle, title and close */}
        <div style={{ display: 'flex', justifyContent: 'center', marginBottom: '-8px' }}>
          <div
            style={{
              width: '40px',
              height: '4px',
              backgroundColor: 'var(--color-line)',
              borderRadius: '2px'
            }}
          />
        </div>

        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
          <h3
            style={{
              margin: 0,
              fontSize: '20px',
              fontWeight: 800,
              color: 'var(--color-evergreen-deep)'
            }}
          >
            {title}
          </h3>
          <button
            type="button"
            onClick={onClose}
            style={{
              background: 'none',
              border: 'none',
              cursor: 'pointer',
              padding: '6px',
              borderRadius: '50%',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              color: 'var(--color-muted)'
            }}
            aria-label={t('phone.close')}
          >
            <Icon name="close" size={20} />
          </button>
        </div>

        {/* Content body */}
        <div
          style={{
            fontSize: '16px',
            lineHeight: 1.8,
            color: 'var(--color-text-soft)',
            overflowY: 'auto'
          }}
        >
          <p style={{ margin: 0 }}>{content}</p>
        </div>

        {/* Close Button */}
        <button
          type="button"
          onClick={onClose}
          style={{
            marginTop: '8px',
            width: '100%',
            height: '48px',
            borderRadius: '999px',
            backgroundColor: 'var(--color-evergreen)',
            color: '#FFFFFF',
            border: 'none',
            fontSize: '16px',
            fontWeight: 700,
            cursor: 'pointer'
          }}
        >
          {t('phone.close')}
        </button>
      </div>
    </div>
  );
};
