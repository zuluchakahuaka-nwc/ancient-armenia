# Книги (медиа НЕ в git — политика владельца)

Бинарники книг лежат локально и попадают только в APK. В репо — лишь
этот каталог-заглушка, `manifest.json` (список книг для GitHub) и этот README.

Восстановление файлов на новой машине — скопировать из библиотеки
владельца `C:\Users\Admin\Documents\books_urartu` (PDF как есть) и
конвертировать DJVU (см. поля `source` в манифесте):

```powershell
# DjVuLibre (ddjvu) — распаковать установщик DjVuLibre-3.5.29_DjView-4.12_Setup.exe 7-Zip'ом
& ddjvu.exe -format=pdf -mode=color -subsample=2 in.djvu out.pdf
```

Файлы каталога (все обязаны существовать перед `assembleDebug`):

| Файл | Размер | Источник |
|---|---|---|
| moiseeva_ancient_urartu_1955.pdf | 5.9 МБ | оригинал PDF |
| arutunyan_biainili_urartu_2006.pdf | 15.2 МБ | оригинал PDF |
| piotrovsky_history_culture_urartu_2011.pdf | 51.4 МБ | оригинал PDF |
| piotrovsky_van_kingdom_1959.pdf | 109.6 МБ | DJVU -> PDF |
| arutunyan_agriculture_urartu_1964.pdf | 105.6 МБ | DJVU -> PDF |
