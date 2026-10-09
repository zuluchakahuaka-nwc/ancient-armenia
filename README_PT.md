# Armênia Antiga

Aplicativo Android offline quadrilíngue (hy/ru/en/pt): guia educacional sobre as fortalezas de Urartu **Erebuni** (782 a.C.) e **Teishebaini** (Karmir Blur).

![Fortaleza de Erebuni](app/src/main/assets/wiki_images/erebuni_fortress.jpg)

## Funcionalidades

### 🧭 Modo Turista
- **Guia** — cartões das fortalezas com fotografias
- **Wiki** — artigos: reis, deuses, vida cotidiana, cuneiforme
- **Biblioteca** — livros PDF + áudio
- **Urartu.fm** — estação offline: ⏮ ⏯ ⏭, toque longo = parar
- **Mapa** — GPS + rotas a pé e de carro
- **Configurações** — troca de idioma e tema (Pré-Urartu / Urartu / Pós-Urartu) em tempo real

### 👤 Modo Funcionário (PIN)
- **Captura rápida** — 📷 foto da câmera → marcação por toques
- **Reconhecimento aéreo** — costura de fotos, detecção automática de 3 níveis, reconstrução 3D
- **Registro de artefatos** — inventário, status de custódia, auditoria, lembretes
- **Exportação** — arquivo assinado para PC

### 🎨 Design
- 3 temas históricos (cada um = paleta + fontes + ornamentos cuneiformes)
- Fontes Noto Sans/Serif Armenian incluídas

## Compilação

```bash
# Requer: Android SDK, Java 21, livros em assets/books/ (ver README lá)
./gradlew assembleDebug
# APK: app/build/outputs/apk/debug/app-debug.apk
```

## Tecnologias
Kotlin · Jetpack Compose · Material 3 · Room · DataStore · Media3 · PdfRenderer · WorkManager

## Compatibilidade
Android 7.0+ (API 24) · Totalmente offline

---

[Русский](README.md) | [English](README_EN.md) | [Հայերեն](README_HY.md)
