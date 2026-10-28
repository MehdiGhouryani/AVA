# Playbook فاز ۰٫۷٫۱ — offline-first

## معماری اجباری

Android فقط یک endpoint انتشار یا manifest مستقل را از طریق adapter می‌بیند؛ CMS API و object storage contract وارد domain نمی‌شوند. مسیر داده:

```text
RemoteReleaseSource
  → bounded download
  → staging directory
  → hash/signature/schema/index verification
  → atomic ContentStore commit
  → active snapshot
  → previous snapshot for rollback
```

## layout پیشنهادی store

```text
content-store/
  active.json
  previous.json
  staging/<candidate-id>/
  bundles/<release-id>/
```

pointerها کوچک، نسخه‌دار و اتمیک نوشته می‌شوند. bundle تغییرناپذیر است. فایل موقت در همان filesystem نوشته، flush/close و سپس rename می‌شود.

## invariantها

- download یا verify شکست‌خورده active را تغییر نمی‌دهد.
- active خراب ابتدا previous معتبر و سپس seed را امتحان می‌کند و خطا را پنهان نمی‌کند.
- process death در هر مرز state نتیجه تعریف‌شده دارد.
- rollback idempotent است.
- UI فقط state بارگذاری/retry را می‌بیند، نه path یا exception خام.
- cache پاک‌شدنی با محتوای active حیاتی یکی نیست.

## تست‌های fault injection

- قطع stream در هر chunk؛
- disk full و permission error؛
- hash/signature/schema mismatch؛
- crash قبل و بعد rename pointer؛
- previous خراب؛
- نسخه قدیمی/ناشناخته؛
- startup بدون شبکه و بدون voice.

## مجوز شبکه

افزودن INTERNET permission تغییر معماری است: ابتدا ADR، flavor/endpoint policy، HTTPS، timeout، pinning decision و privacy review لازم است. هیچ URL مربوط به Payload Admin وارد اپ نشود.
