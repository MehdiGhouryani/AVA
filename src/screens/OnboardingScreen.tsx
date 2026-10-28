import React, { useRef } from 'react';
import { asset } from '../assets/assets';
import { t } from '../i18n/fa';
import { tokens } from '../design/tokens';
import { LogoImage } from '../components/LogoImage';
import { PrimaryButton } from '../components/PrimaryButton';
import { SkipButton } from '../components/SkipButton';
import { PageDots } from '../components/PageDots';
import { Icon } from '../components/Icon';

interface OnboardingScreenProps {
  step: 1 | 2;
  onNext: () => void;
  onPrev?: () => void;
  onSkip: () => void;
  isReducedMotion?: boolean;
}

export const OnboardingScreen: React.FC<OnboardingScreenProps> = ({
  step,
  onNext,
  onPrev,
  onSkip,
  isReducedMotion = false
}) => {
  // Swipe detection in RTL logic:
  // In RTL, forward progress is left-to-right swipe (diffX > 0)
  // backward progress is right-to-left swipe (diffX < 0)
  const touchStartXRef = useRef<number | null>(null);
  const touchStartYRef = useRef<number | null>(null);

  const handleTouchStart = (e: React.TouchEvent) => {
    if (e.touches.length === 1) {
      touchStartXRef.current = e.touches[0].clientX;
      touchStartYRef.current = e.touches[0].clientY;
    }
  };

  const handleTouchEnd = (e: React.TouchEvent) => {
    if (touchStartXRef.current === null || touchStartYRef.current === null) return;
    const endX = e.changedTouches[0].clientX;
    const endY = e.changedTouches[0].clientY;
    const diffX = endX - touchStartXRef.current;
    const diffY = endY - touchStartYRef.current;

    // Only if horizontal movement is dominant
    if (Math.abs(diffX) > Math.abs(diffY) && Math.abs(diffX) >= 60) {
      if (diffX > 0) {
        onNext();
      } else if (diffX < 0 && onPrev) {
        onPrev();
      }
    }

    touchStartXRef.current = null;
    touchStartYRef.current = null;
  };

  const bgMeta = step === 1 ? asset('bg.onboarding1') : asset('bg.garden');
  const bgPosition = step === 1 ? '50% 30%' : '50% 100%';

  return (
    <div
      onTouchStart={handleTouchStart}
      onTouchEnd={handleTouchEnd}
      style={{
        position: 'relative',
        width: '100%',
        height: '100%',
        backgroundColor: 'var(--color-paper)',
        backgroundImage: `url(${bgMeta.src})`,
        backgroundSize: 'cover',
        backgroundPosition: bgPosition,
        backgroundRepeat: 'no-repeat',
        boxSizing: 'border-box',
        overflow: 'hidden',
        userSelect: 'none'
      }}
    >
      {/* C. Logo: Top edge top: 4%, horizontally centered */}
      <div
        style={{
          position: 'absolute',
          top: '4%',
          left: '50%',
          transform: 'translateX(-50%)',
          height: `${tokens.size_px.logoHeightOnboarding}px`,
          display: 'flex',
          justifyContent: 'center',
          alignItems: 'flex-start',
          zIndex: 5
        }}
      >
        <LogoImage height={tokens.size_px.logoHeightOnboarding} />
      </div>

      {/* C. Text Block: Top edge top: 17%, horizontally centered */}
      <div
        style={{
          position: 'absolute',
          top: '17%',
          left: '50%',
          transform: 'translateX(-50%)',
          width: '100%',
          maxWidth: step === 1 ? '310px' : '290px',
          padding: '4px 16px',
          textAlign: 'center',
          zIndex: 6,
          boxSizing: 'border-box'
        }}
      >
        {/* Soft Cream Scrim */}
        <div
          style={{
            position: 'absolute',
            inset: '-10px -18px',
            background:
              'radial-gradient(ellipse, rgba(254,247,225,0.85), rgba(254,247,225,0.60) 68%, transparent)',
            filter: 'blur(8px)',
            borderRadius: '24px',
            pointerEvents: 'none',
            zIndex: -1
          }}
        />

        {/* Title */}
        <h1
          style={{
            margin: '0 0 6px 0',
            fontSize: 'clamp(20px, 5.5vw, 26px)',
            fontWeight: 800,
            lineHeight: 1.45,
            color: 'var(--color-evergreen-deep)',
            textWrap: 'balance',
            textShadow: '0 1px 2px rgba(254, 247, 225, 0.9)'
          }}
        >
          {step === 1 ? t('onboarding.step1.title') : t('onboarding.step2.title')}
        </h1>

        {/* Subtitle / Body */}
        <p
          style={{
            margin: 0,
            fontSize: 'clamp(14px, 3.8vw, 16px)',
            fontWeight: 500,
            lineHeight: 1.6,
            color: 'var(--color-text-soft)',
            textWrap: 'balance',
            textShadow: '0 1px 2px rgba(254, 247, 225, 0.9)'
          }}
        >
          {step === 1 ? t('onboarding.step1.body') : t('onboarding.step2.body')}
        </p>
      </div>

      {/* Artwork Area (Step 1 or Step 2) */}
      {step === 1 ? (
        /* STEP 1: Ava Reading + Glow Bubble */
        <>
          {/* D. Ava: height: min(32dvh, 300px); width: auto; left: 3%; bottom: calc(11.5% + 80px) */}
          <div
            style={{
              position: 'absolute',
              left: '3%', // physical left
              bottom: 'calc(11.5% + 80px)',
              height: 'min(32dvh, 300px)',
              width: 'auto',
              pointerEvents: 'none',
              zIndex: 4,
              display: 'flex',
              alignItems: 'flex-end'
            }}
          >
            <img
              src={asset('char.ava.reading').src}
              alt="آوا در حال مطالعه"
              style={{
                height: 'min(32dvh, 300px)',
                width: 'auto',
                display: 'block',
                transformOrigin: 'bottom center',
                animation: isReducedMotion ? 'none' : 'avaBreathe 3.5s ease-in-out infinite'
              }}
            />
          </div>

          {/* E. Glow Bubble: circle min(150px, 19dvh); right: 3%; top: calc(47% - min(75px, 9.5dvh)) */}
          <div
            className="onboarding-glow-bubble"
            style={{
              position: 'absolute',
              right: '3%', // physical right
              top: 'calc(47% - min(75px, 9.5dvh))',
              width: 'min(150px, 19dvh)',
              height: 'min(150px, 19dvh)',
              borderRadius: '50%',
              background:
                'radial-gradient(circle, rgba(255, 250, 230, 0.95) 0%, rgba(254, 238, 201, 0.75) 60%, rgba(255, 255, 255, 0.2) 100%)',
              border: '1.5px solid rgba(255, 255, 255, 0.85)',
              boxShadow:
                '0 0 24px rgba(255, 225, 140, 0.6), inset 0 0 16px rgba(255, 255, 255, 0.7)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              pointerEvents: 'none',
              zIndex: 3,
              animation: isReducedMotion ? 'none' : 'bubbleFloat 4s ease-in-out infinite'
            }}
          >
            {/* Story Tree inside bubble */}
            <img
              src={asset('decor.tree').src}
              alt=""
              style={{
                width: '65%',
                height: '65%',
                objectFit: 'contain'
              }}
            />

            {/* Twinkling Gold Stars */}
            <div
              style={{
                position: 'absolute',
                top: '12%',
                right: '18%',
                animation: isReducedMotion ? 'none' : 'starTwinkle 1.8s ease-in-out infinite'
              }}
            >
              <Icon name="star" size={16} color="var(--color-amber)" />
            </div>

            <div
              style={{
                position: 'absolute',
                bottom: '22%',
                left: '14%',
                animation: isReducedMotion ? 'none' : 'starTwinkle 2.2s ease-in-out 0.6s infinite'
              }}
            >
              <Icon name="star" size={13} color="var(--color-apricot)" />
            </div>

            {/* Floating Music Notes */}
            <div
              style={{
                position: 'absolute',
                top: '22%',
                left: '12%',
                animation: isReducedMotion ? 'none' : 'noteFloat 2.6s ease-in-out infinite'
              }}
            >
              <Icon name="music-note" size={16} color="var(--color-plum)" />
            </div>

            <div
              style={{
                position: 'absolute',
                bottom: '16%',
                right: '15%',
                animation: isReducedMotion ? 'none' : 'noteFloat 3s ease-in-out 0.8s infinite'
              }}
            >
              <Icon name="music-note" size={14} color="var(--color-berry)" />
            </div>
          </div>
        </>
      ) : (
        /* STEP 2: Characters on Onboarding 2 */
        <>
          {/* F. Squirrel: behind, height min(173px, 20.5dvh), bottom calc(11.5% + 80px + 9dvh) */}
          <div
            style={{
              position: 'absolute',
              left: 'calc(50% + 32px)',
              transform: 'translateX(-50%)',
              bottom: 'calc(11.5% + 80px + 9dvh)',
              height: 'min(173px, 20.5dvh)',
              width: 'auto',
              zIndex: 1,
              pointerEvents: 'none'
            }}
          >
            <div
              style={{
                height: '100%',
                animation: isReducedMotion ? 'none' : 'popIn 400ms ease forwards'
              }}
            >
              <img
                src={asset('char.friend.squirrel').src}
                alt=""
                style={{ height: '100%', width: 'auto', display: 'block' }}
              />
            </div>
          </div>

          {/* F. Bunny: physical left 1%, height min(181px, 21.5dvh), bottom calc(11.5% + 80px) */}
          <div
            style={{
              position: 'absolute',
              left: '1%',
              bottom: 'calc(11.5% + 80px)',
              height: 'min(181px, 21.5dvh)',
              width: 'auto',
              zIndex: 2,
              pointerEvents: 'none',
              animation: isReducedMotion ? 'none' : 'popIn 400ms ease 80ms forwards'
            }}
          >
            <img
              src={asset('char.friend.bunny').src}
              alt=""
              style={{ height: '100%', width: 'auto', display: 'block' }}
            />
          </div>

          {/* F. Fox: physical right 1%, height min(198px, 23.5dvh), bottom calc(11.5% + 80px) */}
          <div
            style={{
              position: 'absolute',
              right: '1%',
              bottom: 'calc(11.5% + 80px)',
              height: 'min(198px, 23.5dvh)',
              width: 'auto',
              zIndex: 2,
              pointerEvents: 'none',
              animation: isReducedMotion ? 'none' : 'popIn 400ms ease 160ms forwards'
            }}
          >
            <img
              src={asset('char.friend.fox').src}
              alt=""
              style={{ height: '100%', width: 'auto', display: 'block' }}
            />
          </div>

          {/* F. Ava: center-front, height min(231px, 27.4dvh), bottom calc(11.5% + 80px) */}
          <div
            style={{
              position: 'absolute',
              left: 'calc(50% - 30px)',
              transform: 'translateX(-50%)',
              bottom: 'calc(11.5% + 80px)',
              height: 'min(231px, 27.4dvh)',
              width: 'auto',
              zIndex: 3,
              pointerEvents: 'none'
            }}
          >
            <div
              style={{
                height: '100%',
                animation: isReducedMotion ? 'none' : 'popIn 400ms ease 240ms forwards'
              }}
            >
              <div
                style={{
                  height: '100%',
                  transformOrigin: 'bottom center',
                  animation: isReducedMotion ? 'none' : 'avaWave 2.4s ease-in-out infinite'
                }}
              >
                <img
                  src={asset('char.ava.wave_book').src}
                  alt="آوا و دوستان"
                  style={{ height: '100%', width: 'auto', display: 'block' }}
                />
              </div>
            </div>
          </div>
        </>
      )}

      {/* B. Primary CTA: bottom: 11.5%, height: 64px, width: min(320px, calc(100% - 64px)), horizontally centered */}
      <div
        style={{
          position: 'absolute',
          bottom: '11.5%',
          left: '50%',
          transform: 'translateX(-50%)',
          width: 'min(320px, calc(100% - 64px))',
          height: '64px',
          zIndex: 10
        }}
      >
        <PrimaryButton
          label={t('onboarding.next')}
          onClick={onNext}
          height={64}
        />
      </div>

      {/* B. Page Dots: bottom: 6.2%, horizontally centered */}
      <div
        style={{
          position: 'absolute',
          bottom: '6.2%',
          left: '50%',
          transform: 'translateX(-50%)',
          zIndex: 10
        }}
      >
        <PageDots currentStep={step} />
      </div>

      {/* B. Skip: PHYSICAL bottom-left, left: 16px; bottom: 2% */}
      <div
        style={{
          position: 'absolute',
          left: '16px', // physical left
          bottom: '2%',
          zIndex: 10
        }}
      >
        <SkipButton onClick={onSkip} />
      </div>
    </div>
  );
};
