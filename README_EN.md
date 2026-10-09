# Ancient Armenia

Quadrilingual (hy/ru/en/pt) offline Android app: educational guide to the Urartian fortresses **Erebuni** (782 BC) and **Teishebaini** (Karmir Blur).

![Erebuni Fortress](app/src/main/assets/wiki_images/erebuni_fortress.jpg)

## Features

### 🧭 Tourist Mode
- **Guide** — fortress cards with photos
- **Wiki** — articles: kings, gods, daily life, cuneiform
- **Library** — PDF books + audio
- **Urartu.fm** — offline station: ⏮ ⏯ ⏭, long-press = stop
- **Map** — GPS + walking and driving directions
- **Settings** — switch language and skin (Pre-Urartu / Urartu / Post-Urartu) on the fly

### 👤 Employee Mode (PIN protected)
- **Quick Capture** — 📷 camera photo → tap to mark targets
- **Aerial Survey** — photo stitching, 3-level auto-detection, 3D reconstruction
- **Artifact Registry** — catalog, custody status, audit log, reminders
- **Export** — signed backup for PC or device sync

### 🎨 Design
- 3 historical skins (each = palette + fonts + cuneiform ornaments)
- Noto Sans/Serif Armenian fonts bundled
- Full-screen styling: background + sides + top/bottom + card borders

## Build

```bash
# Requires: Android SDK, Java 21, books in assets/books/ (see README there)
./gradlew assembleDebug
# APK: app/build/outputs/apk/debug/app-debug.apk
```

## Tech Stack
Kotlin · Jetpack Compose · Material 3 · Room · DataStore · Media3 · PdfRenderer · WorkManager

## Compatibility
Android 7.0+ (API 24) · Fully offline

---

[Русский](README.md) | [Հայերեն](README_HY.md) | [Português](README_PT.md)
