# R8-правила релиза. Базовые "don't touch" для рефлексии-зависимых кусков.

# org.json (синк/экспорт P5): Android предоставляет классы, R8 может ругаться на динамический доступ
-dontwarn org.json.**

# Media3/ExoPlayer: держим листенеры, добавляемые из кода
-keep class androidx.media3.** { *; }

# Room: сгенерированные impl-классы
-keep class * extends androidx.room.RoomDatabase { *; }

# EncryptedSharedPreferences (security-crypto) использует рефлексию на реализациях
-keep class androidx.security.crypto.** { *; }

# WorkManager воркер по имени класса
-keep class * extends androidx.work.CoroutineWorker { *; }
-keep class * extends androidx.work.ListenableWorker { *; }

# glTF/GeoJSON пишутся org.json — данные, не код; строковые константы не вырезаем
-keepclassmembers class com.erebuni782.app.** { public static final java.lang.String *; }
