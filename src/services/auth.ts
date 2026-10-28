import { tokens } from '../design/tokens';

export type RequestOtpResult =
  | { ok: true; resendAfterSec: number }
  | { ok: false; reason: 'network' | 'provider' };

export type VerifyOtpResult =
  | { ok: true }
  | { ok: false; reason: 'wrong' | 'expired' | 'locked' | 'network' };

export interface AuthService {
  requestOtp(phone: string): Promise<RequestOtpResult>;
  verifyOtp(phone: string, code: string): Promise<VerifyOtpResult>;
}

export class MockAuthService implements AuthService {
  private wrongAttempts: number = 0;
  private codeGeneratedAt: number = 0;
  private lockedUntil: number = 0;

  // Debug injection overrides
  public forceRequestError: 'network' | 'provider' | null = null;
  public forceVerifyResult: 'wrong' | 'expired' | 'locked' | 'network' | 'success' | null = null;

  async requestOtp(_phone: string): Promise<RequestOtpResult> {
    await new Promise((resolve) => setTimeout(resolve, 700));

    if (this.forceRequestError) {
      const err = this.forceRequestError;
      return { ok: false, reason: err };
    }

    const now = Date.now();
    if (this.lockedUntil > now) {
      return { ok: false, reason: 'provider' };
    }

    this.wrongAttempts = 0;
    this.codeGeneratedAt = Date.now();

    return {
      ok: true,
      resendAfterSec: tokens.auth_mock.resendSeconds // 90s
    };
  }

  async verifyOtp(_phone: string, code: string): Promise<VerifyOtpResult> {
    await new Promise((resolve) => setTimeout(resolve, 600));

    if (this.forceVerifyResult) {
      const force = this.forceVerifyResult;
      if (force === 'success') {
        this.wrongAttempts = 0;
        return { ok: true };
      }
      return { ok: false, reason: force };
    }

    const now = Date.now();

    // Check lock
    if (this.lockedUntil > now) {
      return { ok: false, reason: 'locked' };
    }

    // Check expiration
    const elapsedSeconds = (now - this.codeGeneratedAt) / 1000;
    if (this.codeGeneratedAt > 0 && elapsedSeconds > tokens.auth_mock.expirySeconds) {
      return { ok: false, reason: 'expired' };
    }

    // Verify code
    const validCode = tokens.auth_mock.debugValidCode; // "123456"
    if (code === validCode) {
      this.wrongAttempts = 0;
      return { ok: true };
    }

    // Wrong attempt
    this.wrongAttempts += 1;
    if (this.wrongAttempts >= tokens.auth_mock.maxWrongAttempts) {
      this.lockedUntil = Date.now() + tokens.auth_mock.lockSeconds * 1000;
      return { ok: false, reason: 'locked' };
    }

    return { ok: false, reason: 'wrong' };
  }

  resetAttempts() {
    this.wrongAttempts = 0;
    this.lockedUntil = 0;
  }
}

export const authService = new MockAuthService();
