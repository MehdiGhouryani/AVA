# Playbook فاز ۰٫۷٫۰ — CMS MVP و صوت

## مرز داده

- `master`: فایل خام خصوصی در quarantine؛ هرگز در export اپ ظاهر نمی‌شود.
- `inspection`: نتیجه scanner و decoder با نسخه ابزار، timestamp و error code کنترل‌شده.
- `delivery`: خروجی محدودشده برای اپ با object key، MIME، bytes و SHA-256.
- `review`: دو تصمیم انسانی مستقل از creator و از یکدیگر.
- `release`: snapshot تغییرناپذیر و deterministic فقط از content/audio approved و delivery ready.

## pipeline لازم

```text
upload master
  → size/magic/hash
  → malware scan (fail closed)
  → real decode/probe
  → technical review
  → educational review
  → idempotent transcode
  → read-back hash verification
  → delivery ready
  → independent publish
```

## قواعد job

- کلید idempotency باید از stable ID + source hash + profile version ساخته شود.
- retry نباید deliveryهای متناقض یا audit تکراری غیرقابل تشخیص بسازد.
- stdout/stderr ابزار خارجی sanitize و محدود شود.
- timeout و unavailable بودن scanner/decoder failure است.
- worker فقط حداقل دسترسی object/row لازم را داشته باشد.
- publisher endpoint حق نوشتن مستقیم metadata delivery را ندارد.

## تست‌های ضروری

- فایل آلوده/خراب/بزرگ/mismatch/timeout؛
- retry و concurrent job؛
- تغییر master پس از review؛
- self-review و same-reviewer denial؛
- delivery pending/failed/missing hash؛
- export byte-identical و بدون master filename/source hash؛
- audit غیرقابل update/delete از API.

## خارج از دامنه

طراحی اپ Android، دانلود remote و گسترش درس در این فاز ممنوع‌اند.
