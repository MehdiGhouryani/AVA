# AVA — باغ صداها (Persian Early Reading App & Studio)

Interactive React application for AVA, a child-first Persian early literacy learning experience and UX design studio for ages 4–7.

## Architecture & Technology Stack

- **Framework**: React 18 with Vite
- **Styling**: Tailwind CSS & custom design tokens (Paper-mint `#EDF6F2`, Ink `#233D47`, Evergreen `#14766A`, Plum `#7954B5`, Apricot `#FFBD59`)
- **Typography**: Local Vazirmatn font (variable weight) with full RTL support
- **UI Components**: Semantic design components with Radix UI primitives and Lucide icons
- **State & Data**: Grounded in baseline curriculum registry (`data/screens_v18.json`, `data/baseline/b/`)

## Features & Screens

1. **Child Discovery Path (باغ من)**:
   - Guided learning path through the sound garden (`S06-B`)
   - Sequential learning nodes: Sound games (`S32`), phoneme introduction (`S07`, `S08`), tactile letter tracing (`S09`), listening discrimination (`S10`), word building (`S12-G3`), memory games (`S12-G4`), syllable bridges (`S12-G5`), story reading (`S14`), and leaf rewards (`S13`, `S16`).
   - Sound blending slider (`S33`) from right to left combining sounds into words.
   - Interactive tracing pad with touch tracking and demo stroke guide (`S09`).
2. **Companion & Profiles (همراه من)**:
   - Avatar builder with custom colors, hats, books, and patterns (`S15`, `S04`).
   - Per-profile local persistence and switching (`S27`).
3. **Parent Space (بخش بزرگ‌ترها)**:
   - Reading progress reporting by phoneme mastery (`S19`).
   - Phone + OTP sign-in flow and parental consent gate (`S03a-b`).
   - Offline package download management (`S22`, `S24`).
   - Subscription information (`S18`).
   - Privacy and data deletion flows (`UX-01`, `UX-02`, `UX-03`).
4. **CMS Content Studio (استودیوی محتوا)**:
   - Module exploration from `CMS-00` to `CMS-09`: Readiness gates, curriculum nodes, media assets, audio slots, waveform reviews, release pipelines, and role segregation.
   - Interactive review dialogue with notes and draft decision auditing.
5. **Accessibility & Review Tools**:
   - Screen state switcher for testing every registered interaction state.
   - Reduced motion toggle and Quiet mode visual cues.
   - Design tokens inspector and screen contract specification panel.
   - Global review test hooks (`window.avaSelect`, `window.avaSetState`, `window.avaScreens`).

## Getting Started

```bash
npm install
npm run dev
```

App runs on port 3000 at `http://localhost:3000`.
