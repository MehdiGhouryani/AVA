import React from 'react';
import { asset } from '../assets/assets';

interface LogoImageProps {
  height: number;
  className?: string;
  style?: React.CSSProperties;
}

export const LogoImage: React.FC<LogoImageProps> = ({ height, className = '', style }) => {
  const meta = asset('brand.logo');
  // Aspect ratio is 328 / 486 ≈ 0.675
  const width = Math.round(height * (meta.width / meta.height));

  return (
    <img
      src={meta.src}
      alt="آوا"
      width={width}
      height={height}
      className={className}
      style={{
        height: `${height}px`,
        width: 'auto',
        maxWidth: '100%',
        objectFit: 'contain',
        display: 'block',
        ...style
      }}
      loading="eager"
    />
  );
};
