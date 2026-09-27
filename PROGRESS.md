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
| - | P0 | Создан AGENTS.md (инструкция агента: продукт, D1–D10, стек, машина, команды, конвенции, тест-пирамида, фазы, zai-vision MCP §9, опасности). Решение владельца: AGENTS.md живёт локально, в GitHub НЕ пушится |
| - | P0 | Создан docs/decisions.md (D1–D10 + обязательства по фазам) и PROGRESS.md |
| - | P0 | git init -b main |
| - | P0 | Skeleton (23 файла): Gradle 8.14.3 wrapper из кэша Studio, AGP 8.9.1, Kotlin 2.0.21, Compose BOM 2024.12.01, app-модуль, strings ×3 локали, adaptive-icon, SanityTest |
| - | P0 | Починено: android.useAndroidX (gradle.properties) |
| - | P0 | assembleDebug ✓ (1m31s), test ✓; AVD erebuni_phone/erebuni_tablet (API 34, D:\Android\avd) |
| - | P0 | Дымовой тест: boot ✓, install ✓, start ok 6.7s, PID жив, OCR «uni 782» |
| - | P0 | Приватный GitHub-репо zuluchakahuaka-nwc/erebuni-782 (main), AGENTS.md исключён (.gitignore), коммит 61c0f29 |
| - | P1 | Зависимости: appcompat 1.7.0, navigation-compose 2.8.5, datastore 1.1.1, coroutines-test 1.9.0 |
| - | P1 | Шрифты Noto Sans/Serif Armenian (Regular+Bold, 4×~30КБ) скачаны в res/font |
| - | P1 | Движок скинов (Skin.kt/Theme.kt/Ornament.kt): SkinId × SkinSpec (светлая/тёмная палитры, пары шрифтов, Shapes, OrnamentStyle), LocalSkin; Pre-Urartu (охра/сиенна, шеврон), Urartu (базальт/глина #B2592A/#F3E7D3, зубцы), Post-Urartu (мрамор/лазурит #1F5F8B/#F6F8FA, меандр); OrnamentalDivider на Canvas |
| - | P1 | SettingsStore (интерфейс) + SettingsRepository (DataStore, дефолт URARTU); MainViewModel (StateFlow) |
| - | P1 | HomeScreen (чипы скинов/языков + PreviewCard) + AboutScreen + AppNavHost; язык — AppCompatDelegate.setApplicationLocales; манифест: autoStoreLocales-сервис + localeConfig; тема → AppCompat.DayNight |
| - | P1 | Строки: 17 ключей × en/hy/ru |
| - | P1 | Починено в ходе: import Composable и Shapes(RoundedCornerShape) в Skin.kt; Dispatchers.setMain в unit-тестах (4/4 ✓) |
| - | P1 | AGP UTP gRPC-падение в connectedDebugAndroidTest → обход через adb install + am instrument -w |
| - | P1 | androidTest-фиксы: одна композиция на тест (все скины сразу), onAllNodesWithText/assertIsSelected импорты, строки через InstrumentationRegistry → OK (4 tests) |
| - | P1 | Ручной прогон через uiautomator-тапы: headless-эмулятор требует wakeup+stayon; bounds-парсинг «x,y» без культурных багов; не-ASCII матчинг по codepoints (Ծ=U+053E) |
| - | P1 | zai-vision: встроенный MCP таймаутит → CLI mcp-cli -c (конфиг в Temp, ключ транзитно из opencode.json, вне репо); имена инструментов без префикса zai-vision_ |
| - | P1 | Зрение: OCR RU ✓ полный; HY без tofu ✓ (+codepoint-дамп точный; армянский OCR модели слабый — слова галлюцинирует); палитра Urartu ✓ (беж/глина/зубцы) |
| - | P1 | **НАЙДЕН БАГ**: тап по скину не пересобирал тему (MainActivity читал StateFlow.value без подписки; применялся только при recreate от смены языка). Фикс: collectAsState |
| - | P1 | После фикса e2e падал на старте: персистентная локаль прошлой сессии (hy) → тест приводит себя к EN через не-локализуемый чип «English» |
| - | P1 | Доказательство фикса: logcat setSkin(POST)→recompose за 60мс; пиксели Post BG=246,248,250 (#F6F8FA) + 21 синий px кнопки. Ранние «бежевые» замеры — устаревшие кадры screencap |
| - | P1 | Дебаг-логи удалены; финал: assembleDebug + test + am instrument OK (4) |
| - | P1 | Commit feat(P1) + push |

## 2026-09-27 — P2 (туристический шелл)

| Фаза | Действие → Результат |
|------|----------------------|
| P2 | Зависимости: Room 2.6.1 + KSP 2.0.21-1.0.28, Media3 1.5.1, material-icons(-core/extended) |
| P2 | Аудио-пакет Urartu.fm СГЕНЕРИРОВАН (PS-скрипт, 3 ambient-WAV по 20с/860КБ в assets/urartu_fm) — станция реально играет офлайн (D10) |
| P2 | Room: WikiArticleEntity/BookEntity/ChapterEntity/AudioTrackEntity + DAO + сид-колбэк; AppGraph (сервис-локатор) + App |
| P2 | Сид-контент: 10 статей вики ×3 локали, книга «Эребуни: крепость на Арин-Берде» (4 главы ×3, licenseNote CC0 — D1), 2 аудиогида-заглушки |
| P2 | Репозитории: Wiki/Book/Audio (+URARTU_FM_PACK), SettingsStore расширен: urartuFmEnabled (дефолт ВКЛ, D10), readerFontScale/NightMode/Bookmarks |
| P2 | PlayerManager (ExoPlayer, интерфейс PlayerController для тестов) + мини-плеер над навбаром |
| P2 | UI: MainShell (bottom-nav Guide/Wiki/Library/Map/Settings + NavHost), GuideScreen (крепости→статьи), WikiScreen+WikiArticleScreen, LibraryScreen (Books/Audio табы), ReaderScreen (шрифт A±, ночь, закладки, главы), AudioTab (Urartu.fm Switch, SAF-импорт музыки, гиды), MapScreen (координаты), SettingsScreen (язык/скин/about); P1-экраны Home/About/AppNavHost удалены |
| P2 | Строки: ~50 ключей ×3 локали |
| P2 | Тесты: unit — MainViewModelTest(2)+LibraryViewModelTest(4: дефолт ON, старт/стоп пакета, CRUD треков)+ReaderViewModelTest(5: главы clamp, закладки persist, шрифт clamp, ночь) = 11+Sanity; component — SkinRenderTest(3, переписан под SampleContent)+WikiScreenTest(сид-статьи из Room); e2e — TouristShellE2E (полный туристический сценарий) заменил LanguageSkinSwitchTest |
| P2 | Починено в ходе: produceState-импорты (runtime, не flow), mainViewModel-имя, rememberLibraryViewModel в MainShell, Icons.Book/Pause → material-icons-extended, composeRule.activity → InstrumentationRegistry |
| P2 | **БАГ-КУЛЬБИТ**: LocalSkin-краш «SkinSpec not provided» в подкомпозиции LazyLayout (стек: CachedItemContent→SaveableStateProvider→PinnableItem) при nav-saveState-лимбо + эмиссии Room-flow в отцепленной композиции. Фикс №1 (чтение в теле вместо default-параметра) НЕ помог; фикс №2 (dynamic compositionLocalOf) НЕ помог; фикс №3 (безопасный дефолт URARTU вместо error) — крашей больше нет. Урок: fail-safe default для скин-local обязателен |
| P2 | e2e-фиксы: awaitText(substring=true) (тело статьи ≠ точная строка), нормализация EN на старте, awaitTag перед tab_audio, выход из ридера reader_back (restoreState честно восстанавливает цепочку library→book — осознанный UX «вернуться к чтению») |
| P2 | Итог инструментальных: **OK (5 tests)** (3 skin + 1 wiki-component + 1 полный e2e), крэш-буфер чист |
| P2 | Визуальный проход (en/ru/hy, dumps + 12 скриншотов build/vision): все 5 вкладок, статья, книги, ридер, аудио (станция+гипы+моя музыка), карта, настройки; мини-плеер подтверждён нодой «Արին-Բերդ» над навбаром при включённой станции; локали переключаются на лету |
| P2 | zai-vision MCP (встроенный + mcp-cli) таймаутил всю сессию (5+ попыток) — верификация через uiautomator-кодпоинты + пиксели; вернуться к OCR при стабилизации сервера |
| P2 | Commit feat(P2) + push |

\* время локальное, заполняется по мере возможности.
