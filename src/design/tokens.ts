import tokensData from '../../design/design_tokens.json';

export interface DesignTokens {
  color: typeof tokensData.color;
  font: typeof tokensData.font;
  space_px: number[];
  radius_px: typeof tokensData.radius_px;
  size_px: typeof tokensData.size_px;
  shadow: typeof tokensData.shadow;
  motion: typeof tokensData.motion;
  auth_mock: typeof tokensData.auth_mock;
}

export const tokens: DesignTokens = tokensData as DesignTokens;

// Initialize CSS variables on :root
export function initCssTokens() {
  if (typeof document === 'undefined') return;
  const root = document.documentElement;

  // Colors
  Object.entries(tokens.color).forEach(([key, val]) => {
    root.style.setProperty(`--color-${camelToKebab(key)}`, val);
  });

  // Radii
  Object.entries(tokens.radius_px).forEach(([key, val]) => {
    root.style.setProperty(`--radius-${camelToKebab(key)}`, `${val}px`);
  });

  // Sizes
  Object.entries(tokens.size_px).forEach(([key, val]) => {
    if (typeof val === 'number') {
      root.style.setProperty(`--size-${camelToKebab(key)}`, `${val}px`);
    }
  });

  // Shadows
  Object.entries(tokens.shadow).forEach(([key, val]) => {
    root.style.setProperty(`--shadow-${camelToKebab(key)}`, val);
  });

  // Motion
  root.style.setProperty('--motion-enter-ms', `${tokens.motion.enterMs}ms`);
  root.style.setProperty('--motion-press-ms', `${tokens.motion.pressMs}ms`);
  root.style.setProperty('--motion-shake-ms', `${tokens.motion.shakeMs}ms`);
  root.style.setProperty('--motion-float-s', `${tokens.motion.floatS}s`);
  root.style.setProperty('--motion-easing', tokens.motion.easing);
}

function camelToKebab(str: string): string {
  return str.replace(/[A-Z]/g, letter => `-${letter.toLowerCase()}`);
}
