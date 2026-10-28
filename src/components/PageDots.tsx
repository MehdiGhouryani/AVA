import React from 'react';
import { t, toFaDigits } from '../i18n/fa';

interface PageDotsProps {
  currentStep: 1 | 2;
  totalSteps?: number;
  className?: string;
  style?: React.CSSProperties;
}

export const PageDots: React.FC<PageDotsProps> = ({
  currentStep,
  totalSteps = 2,
  className = '',
  style
}) => {
  const label = t('onboarding.a11yDots', {
    current: toFaDigits(currentStep),
    total: toFaDigits(totalSteps)
  });

  return (
    <div
      role="img"
      aria-label={label}
      dir="rtl"
      style={{
        display: 'flex',
        flexDirection: 'row',
        alignItems: 'center',
        justifyContent: 'center',
        gap: '8px',
        height: '24px',
        padding: '0 8px',
        ...style
      }}
      className={`page-dots ${className}`}
    >
      {/* 
        In RTL flex layout:
        Child 0 (first in DOM) is rendered on the physical RIGHT.
        Step 1 corresponds to child 0 (active pill on the right).
        Step 2 corresponds to child 1 (active pill on the left).
      */}
      {[1, 2].map((step) => {
        const isActive = step === currentStep;
        return (
          <span
            key={step}
            style={{
              display: 'inline-block',
              height: '10px',
              width: isActive ? '24px' : '10px',
              borderRadius: '999px',
              backgroundColor: isActive ? 'var(--color-evergreen)' : 'var(--color-dot-idle)',
              boxShadow: isActive ? 'none' : '0 0 0 2px rgba(254, 247, 225, 0.9)',
              transition: 'width 250ms ease, background-color 250ms ease, box-shadow 250ms ease'
            }}
          />
        );
      })}
    </div>
  );
};
