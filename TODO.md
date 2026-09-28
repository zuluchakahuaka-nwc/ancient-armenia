# TODO — следующая сессия

## КРИТИЧНО (git не пушится — 432 МБ)

- [ ] **git filter-repo — НЕ сработал до конца**: APK 385 МБ всё ещё в истории. Запустить:
  ```powershell
  python -m pip install git-filter-repo
  git filter-repo --force --invert-paths --path "dist/Erebuni782-v0.2.2.apk" --path-glob "dist/*.apk"
  # ПРОВЕРИТЬ результат через: git rev-list --objects --all | git cat-file --batch-check='%(objecttype) %(objectname) %(objectsize) %(rest)' | Where-Object { $_ -match "blob" -and ($_ -split " ")[2] -gt 1000000 }
  # Урок из P5: filter-repo может молча не переписать — проверять commit-map/новые хеши!
  git reflog expire --expire=now --all
  git gc --prune=now --aggressive
  ```
- [ ] **Удалить тяжёлые wiki_images из git**: book_*.png (~20 МБ), extract_*.png (~30 МБ) — это рендеры страниц книг, не нужны в git. Добавить в .gitignore:
  ```
  app/src/main/assets/wiki_images/book_*.png
  app/src/main/assets/wiki_images/extract_*.png
  ```
- [ ] Сделать репо **публичным** на GitHub (Settings → Danger Zone → Change visibility)
- [ ] Force-push после чистки

## Контент и фичи

- [ ] **Португальский язык** (values-pt) — strings.xml для всех экранов
- [ ] **Расширить wiki-статьи** — сейчас по 2-3 предложения, нужно 5-8 с деталями
- [ ] **Армянский PDF-мануал** — проверить шрифт (возможно не отображается на некоторых устройствах; попробовать Sylfaen вместо Arial)
- [ ] **Камера в быстрой съёмке** — ActivityResultContracts.TakePicture требует FileProvider на API 24+ (может не работать на Redmi 4X без настройки)
- [ ] **Скачивание фото богов** (Халди, Тейшеба, Шивини) — сейчас используют фрески, нужны отдельные изображения
- [ ] **Wiki: категории** — добавить фильтрацию по категориям (цари/боги/крепости/быт)
- [ ] **Timeline** — визуальная шкала 860→590 до н.э. с событиями

## UX/UI

- [ ] **Стилизация карточек** — StyledCard ещё не подключена к гиду/вики (только StyledScreen на уровне MainShell)
- [ ] **Орнаменты при смене скина** — проверить что фон/бока/верх перерисовываются мгновенно
- [ ] **TalkBack** — живой прогон скринридером на телефоне
- [ ] **Планшет** — прогнать suite на erebuni_tablet после всех изменений

## Инфраструктура

- [ ] **APK 385 МБ** — рассмотреть App Bundle (AAB) для уменьшения размера
- [ ] **CI/CD** — GitHub Actions для автосборки
- [ ] **Release build** — настроить подпись и minify (R8)
