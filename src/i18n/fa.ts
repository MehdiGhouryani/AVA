import stringsData from '../../design/strings.fa.json';

const persianDigits = ['۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹'];
const arabicDigits = ['٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩'];

/**
 * Converts English or Arabic digits in a string/number to Persian digits.
 */
export function toFaDigits(val: string | number): string {
  const str = String(val);
  return str.replace(/\d/g, (d) => persianDigits[parseInt(d, 10)] ?? d);
}

/**
 * Normalizes Persian or Arabic digits to standard ASCII English digits.
 */
export function toEnDigits(str: string): string {
  let res = str;
  for (let i = 0; i < 10; i++) {
    res = res.split(persianDigits[i]).join(String(i));
    res = res.split(arabicDigits[i]).join(String(i));
  }
  return res;
}

/**
 * Formats seconds into mm:ss with Persian numerals, e.g. 90 -> ۰۱:۳۰
 */
export function formatCountdownFa(totalSeconds: number): string {
  const sec = Math.max(0, Math.floor(totalSeconds));
  const m = Math.floor(sec / 60);
  const s = sec % 60;
  const mm = m < 10 ? `0${m}` : `${m}`;
  const ss = s < 10 ? `0${s}` : `${s}`;
  return toFaDigits(`${mm}:${ss}`);
}

/**
 * Resolves a dot-separated path from strings.fa.json and replaces variables.
 * e.g. t('brand.tagline')
 * e.g. t('otp.subtitle', { phone: '۰۹۱۲۳۴۵۶۷۸۹' })
 */
export function t(path: string, vars?: Record<string, string | number>): string {
  const parts = path.split('.');
  let current: any = stringsData;

  for (const part of parts) {
    if (current && typeof current === 'object' && part in current) {
      current = current[part];
    } else {
      return path; // Fallback to path key if missing
    }
  }

  let text = '';
  if (typeof current === 'string') {
    text = current;
  } else if (current && typeof current === 'object' && 'fa' in current) {
    text = current.fa;
  } else {
    return path;
  }

  if (vars) {
    Object.entries(vars).forEach(([k, v]) => {
      text = text.replace(new RegExp(`\\{${k}\\}`, 'g'), String(v));
    });
  }

  return text;
}
