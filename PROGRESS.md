# PROGRESS.md — журнал работы над Erebuni 782

Формат: дата | фаза | действие → результат. Фиксируется ВСЁ (AGENTS.md §6).

## 2026-09-27

| Время* | Фаза | Действие → Результат |
|--------|------|----------------------|
| - | пре-P0 | Прочитана спека `описание.txt` (v1.1, 81 строка) → продукт, режимы, стек, фазы поняты |
| - | пре-P0 | Закрыты Q1–Q4 спеки владельцем → D1–D4 (см. docs/decisions.md) |
| - | пре-P0 | Новая фича владельца: аэро-разведка (сшивка, тумблер ручная/авто, 3 уровня автодетекции, 3D-реконструкция) → D5–D8, добавлена фаза P4 |
| - | пре-P0 | Новая фича владельца: музыка (свой импорт + аудиогиды + станция Urartu.fm с тумблером, дефолт ВКЛ) → D9–D10, влита в P2 |
| - | пре-P0 | Машинный аудит: SDK D:\Android\Sdk (platforms 34/35/36, build-tools 34/35/36.1, emulator 36.5.10, cmdline-tools latest), Java 21.0.10, standalone-gradle нет → wrapper; AVD: ruspanol-24/34 (user-profile) + TestAPI34/Test_API34/Test_API34_B (D:\Android\avd, нужен ANDROID_AVD_HOME); Studio data D:\AndroidStudioData (gradle-дистрибутивы 8.2–8.14.3 в кэше). Блокеров нет |
| - | P0 | Создан AGENTS.md (каноническая инструкция агента: продукт, D1–D10, стек, машина, команды, конвенции, тест-пирамида unit/component/e2e, фазы, zai-vision MCP §9, опасности). Решение владельца: AGENTS.md живёт локально, в GitHub НЕ пушится |
| - | P0 | Создан docs/decisions.md (D1–D10 + обязательства по фазам) |
| - | P0 | Создан PROGRESS.md (этот файл) |
| - | P0 | git init -b main → репозиторий инициализирован |
| - | P0 | Skeleton создан (23 файла): settings.gradle.kts, build.gradle.kts, gradle/libs.versions.toml (AGP 8.9.1, Kotlin 2.0.21, Compose BOM 2024.12.01), app-модуль (Manifest, MainActivity, theme-заглушка), res: values/values-hy/values-ru (app_name ×3 локали), adaptive-icon (клинья, вектор), SanityTest (JVM), proguard-заготовка, .gitignore (AGENTS.md и local.properties исключены), local.properties (sdk.dir) |
| - | P0 | Wrapper: сгенерирован локальным Gradle 8.14.3 из кэша Studio (GRADLE_USER_HOME=D:\AndroidStudioData\gradle), distributionUrl = gradle-8.14.3-all.zip (уже в кэше, без скачивания) → BUILD SUCCESSFUL 1m49s |
| - | P0 | Первая сборка упала: :app:checkDebugAarMetadata — нет android.useAndroidX → добавлен gradle.properties (jvmargs 2G, useAndroidX, nonTransitiveRClass, kotlin.code.style) |
| - | P0 | Повтор: assembleDebug BUILD SUCCESSFUL (1m31s, 35 tasks, APK собран); test BUILD SUCCESSFUL (39s, SanityTest зелёный) |
| - | P0 | AVD созданы в D:\Android\avd: erebuni_phone (pixel_6), erebuni_tablet (pixel_tablet); API 34 google_apis x86_64; подтверждены emulator -list-avds |
| - | P0 | Дымовой тест (erebuni_phone headless, swiftshader): BOOTED=True, install Success, am start Status ok / TotalTime 6733ms, процесс жив PID 2111, скриншот → build/p0_smoke.png |
| - | P0 | OCR скриншота (zai-vision_extract_text_from_screenshot): «uni 782» — строковый ресурс конвейер локализации рендерит; «Ereb» обрезается по левому краю → центр текста в P1 |
| - | P0 | Conventional commit + приватный GitHub-репо через gh (аккаунт zuluchakahuaka-nwc), пуш БЕЗ AGENTS.md (gitignored) |

\* время локальное, заполняется по мере возможности.
