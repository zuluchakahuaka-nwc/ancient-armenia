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

## 2026-09-27 — P3 (режим сотрудника)

| Фаза | Действие → Результат |
|------|----------------------|
| P3 | Зависимости: security-crypto (EncryptedSharedPreferences), exifinterface, work-runtime-ktx 2.9.1 |
| P3 | Room v2: ArtifactEntity (13 полей + soft-delete под синк P5) + AuditEntryEntity; DAO + репозиторий с обязательным аудитом каждого действия (CREATED/UPDATED/STATUS_CHANGED/PHOTO_ADDED/DELETED) |
| P3 | Машина состояний CustodyStatus (in-situ→agreed→transit→museum→researched, только вперёд, fromRaw-fallback) + ArtifactCategory ×4 |
| P3 | D3: PinRepository — EncryptedSharedPreferences, солёный SHA-256 (PinHasher чистый/JVM-тестируемый), сетап с подтверждением/ввод/смена; EmployeeSession (процессная сессия) |
| P3 | D4: EXIF-политика — SettingsStore.keepExif (дефолт СОХРАНЯТЬ) + PhotoStore (keep → копия как есть; strip → перекодирование битмапом) + тумблер в реестре |
| P3 | WorkManager: InspectionReminderWorker (нотификация, канал, permission-check API33+) + ReminderScheduler (чистый computeDelayMillis, uniqueWork REPLACE) |
| P3 | UI: PinGateScreen (сетап/ввод), EmployeeScreen (реестр + EXIF-тумблер + FAB), ArtifactEditScreen (все поля спеки: категория-чипы, даты dd.mm.yyyy, статусы с advance, фото-Picker, аудит-лента); маршруты в MainShell + guard: employee без сессии → авто-редирект на гейт (дыра закрыта) |
| P3 | Строки: ~50 ключей ×3 локали |
| P3 | Тесты: unit 22 (CustodyStatus 3, PinHasher 2, Reminder 1, ArtifactRepository 4 на фейковых DAO, + прежние) ✓; e2e EmployeeFlowE2E: PIN-сетап → создание → рестарт активности → гейт → данные живы → статус → аудит ✓ |
| P3 | **Отловлено в e2e**: (1) стейт-поллинг: PIN персистенен → гейт адаптивен (SETUP/ENTER); (2) дыра безопасности: recreate открывал реестр без PIN → guard-редирект; (3) гонка: advance-кнопка кликалась до загрузки драфта (id пуст → молча return) → дозагрузка в advanceStatus; (4) аудит ниже сгиба → performScrollToNode по тегу edit_scroll; (5) интеракции из waitUntil-условия НЕ работают (только проверки); (6) стейл-dex при install -r → лечится полным uninstall/install |
| P3 | Ручная проверка драйвером (uiautomator): полный флоу сотрудника живьём — PIN, создание, статус Agreed for transfer, аудит с датами ✓ |
| P3 | Зрение: CLI-вызов исправлен (полный путь C:\Users\Admin\.bun\bin\mcp-cli.exe, JSON в одинарных кавычках, timeout 240с) — OCR полного экрана аудита идеален; гочи задокументированы в AGENTS.md §9 |
| P3 | Скриншоты: p3_pin_gate/registry/edit_form/audit (en) |
| P3 | Итог инструментальных: **OK (6 tests)**, крэш-буфер чист; unit все зелёные |
| P3 | Commit feat(P3) + push |

## 2026-09-27 — P4 (аэро-разведка)

| Фаза | Действие → Результат |
|------|----------------------|
| P4 | Room v3: AerialSessionEntity + AerialMarkerEntity (нормированные 0..1 коорд.; D7: registryEntryId — nullable-резерв, выключено); репо с гео-привязкой, GeoJSON-экспортом, авто/ручными маркерами |
| P4 | D5: ObjectDetector-интерфейс + GridAnomalyDetector — чистая JVM-эвристика (сеточная аномалия яркости/σ, медиана+MAD, пороги 3.0/1.8/1.0 × сетка 48/32/16 = слабый/средний/дотошный); слот под TFLite |
| P4 | Сшивка: Stitcher-интерфейс + BlendStitcher (поиск перекрытия по яркостным профилям, линейный блендинг шва; потолок 75%, даунскейл ≤1024 обязательный); слот под OpenCV detail-pipeline |
| P4 | D6: EraParametricGenerator (Урарту: стены+4 башни+цитадель / Ахемениды: стилобат+24 колонны+архитрав / Эллинизм: ступени+периптер+фронтон) + GltfWriter (glTF 2.0, data-URI base64) — чистые; SfM и SceneView-вьюер — отдельным этапом (P6) |
| P4 | GeoJSON-экспорт разметки; D8: EXIF-гео при импорте (getLatLong), иначе ручная привязка диалогом |
| P4 | Демо-ассеты: 2 генерированных «аэрофото» 512×384 с общими аномалиями + сдвигом 128px — полный офлайн-цикл для e2e |
| P4 | UI: AerialScreen (сессии+FAB), AerialSessionScreen (фото-борд, сшивка, тумблер Manual/Auto, уровни-чипы, канвас-оверлей с нумерованными маркерами, список, экспорт, 3D-диалог с эпохами), вход из EmployeeScreen; маршруты с guard |
| P4 | Маркеры: тап по канвасу → нумерация; авто → зелёные с уверенностью; delete; BoxWithConstraints-позиционирование |
| P4 | Строки: ~35 ключей ×3 локали |
| P4 | Тесты: unit 34 (GridAnomaly 3, Stitcher 3, Generator 4, GltfWriter в составе, GeoJson 2 + прежние) ✓ |
| P4 | **АЛГОРИТМ-СМЕНА** (по решению владельца): e2E-инварианты введены — статус-строка aerial_status (всегда-скомпонованный якорь, machine-парсится "markers=N stitched=N busy=N geo=N"), probe-first, лимит 2 итерации, анти-стейл-dex — прописаны в AGENTS.md §7.5 |
| P4 | Починено в ходе: KSP bind-var (:markerId), combine>5 flows (вложенность), latDouble→getLatLong(FloatArray), Dp/Float/Double-кастомаркине, BoxWithConstraints+Density-scopes, maxOverlap 45%→75% (истинное перекрытие демо 384px) |
| P4 | E2E AerialFlowE2E (полный цикл: PIN→аэро→сессия→демо→сшивка→ручной маркер→WEAK-детекция 54→гео→GeoJSON→glTF) — **OK** после перевода на статус-инвариант; P4DBG-логи: stitch 640×384 (после 75%), detect=54 |
| P4 | Итог инструментальных: **OK (9 tests)** (3 skin + wiki + 2 overlay + tourist + employee + aerial), крэшей нет |
| P4 | Зрение (рабочий CLI): OCR экрана сессии — статус markers=55 stitched=1 geo=1, гео-ручная, нумерованные маркеры на фото видны ✓ |
| P4 | Commit feat(P4) + push |

## 2026-09-27 — P5 (подписанный дамп + синк-протокол)

| Фаза | Действие → Результат |
|------|----------------------|
| P5 | VersionVector + SyncEngine (чистые): доминирование/конкурентность/merge; LWW по (лампорт,deviceId); параллельные без победителя → конфликт-лист; tombstones |
| P5 | SyncTransport: TCP-кадры (4B длина + JSON), SyncServer/SyncClient; SyncLoopback — полный протокол в одном процессе (сервер = «новое устройство»); BT-реализация — слот для железа |
| P5 | Room v4: артефакты + versionVector/lastEditor; DeviceId (персистентный UUID); репозиторий бампает VV на каждом изменении |
| P5 | ExportRepository (D2): zip .e782 = data.json + photos + manifest + signature; PBKDF2(50k)+HMAC-SHA256 от парольной фразы; импорт: constant-time проверка подписи → upsert → фото в приватное хранилище; экспорт-каталог = external-files (виден ПК по USB) |
| P5 | UI: карточка «Экспорт/синк» в реестре (пасфраза, 3 кнопки, статус-якорь export_status); ExportViewModel |
| P5 | Починено: KDoc «photos/*» открывал вложенный комментарий (Kotlin nest!); File(File)-конструктора нет; setValue-импорт; org.json в JVM-тестах (testImplementation org.json:json); INTERNET-permission для loopback-сокетов (EPERM); IndexOutOfBounds — автоМАРКЕРЫ поштучно мутировали список во время measure → батч-вставка одной транзакцией |
| P5 | Тесты: unit +9 (VV 3, SyncEngine 5) = 42 ✓; androidTest +ExportRoundTripTest 2 (раунд-трип двух баз + отказ неверной фразы) + ExportSyncE2E 1 → **OK (12)** |

## 2026-09-27 — P6 (адаптив, доступность)

| Фаза | Действие → Результат |
|------|----------------------|
| P6 | Адаптив-шелл: <840dp = bottom-bar; ≥840dp (планшет) = NavigationRail + мини-плеер сверху; NavHost с weight(1f) |
| P6 | ПЛАНШЕТ-ФИКС: настройки рендерились ПУСТЫМИ при играющей станции (miniplayer+rail раскладка без weight) → полная перестройка wide-режима; воспроизведено вручную, подтверждено полным suite на erebuni_tablet — **OK (12 tests)** |
| P6 | TalkBack-семантика: contentDescription на всех иконках навигации (rail+bar) и FAB; живой прогон скринридера — за владельцем (AVD без TalkBack-сервиса) |
| P6 | E2E: чипы языка/скина получили TAG-якоря (lang_*/skin_*) — все 4 e2e переведены с текстов на теги (планшето-стабильно) |
| P6 | Книги (D1): добор НЕВОЗМОЖЕН — списка книг и лицензий у владельца нет (решение D1 подтверждено); каркас+1 тестовая книга готовы к наполнению |
| P6 | Скриншоты: p6_tablet_rail/guide, p6_tablet_library |
| P6 | Commit feat(P5+P6) + push |

\* время локальное, заполняется по мере возможности.
