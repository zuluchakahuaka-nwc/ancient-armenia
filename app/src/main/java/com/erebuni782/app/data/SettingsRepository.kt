package com.erebuni782.app.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.erebuni782.app.ui.theme.SkinId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore(name = "settings")

/** Абстракция хранилища настроек (для подмены в unit-тестах). */
interface SettingsStore {
    val skin: Flow<SkinId>
    suspend fun setSkin(id: SkinId)

    /** D10: тумблер Urartu.fm, дефолт ВКЛ. */
    val urartuFmEnabled: Flow<Boolean>
    suspend fun setUrartuFmEnabled(enabled: Boolean)

    /** Ридер: размер шрифта (масштаб), ночной режим, закладки «bookId:chapter». */
    val readerFontScale: Flow<Float>
    suspend fun setReaderFontScale(scale: Float)
    val readerNightMode: Flow<Boolean>
    suspend fun setReaderNightMode(enabled: Boolean)
    val readerBookmarks: Flow<Set<String>>
    suspend fun toggleBookmark(key: String)
}

/** DataStore-реализация: всё персистентно между запусками. */
class SettingsRepository(private val context: Context) : SettingsStore {

    private val store = context.settingsDataStore

    override val skin: Flow<SkinId> = store.data.map { prefs ->
        val raw = prefs[SKIN_KEY]
        SkinId.entries.firstOrNull { it.name == raw } ?: SkinId.URARTU
    }

    override suspend fun setSkin(id: SkinId) {
        store.edit { prefs -> prefs[SKIN_KEY] = id.name }
    }

    override val urartuFmEnabled: Flow<Boolean> = store.data.map { prefs ->
        prefs[URARTU_FM_KEY] ?: true
    }

    override suspend fun setUrartuFmEnabled(enabled: Boolean) {
        store.edit { prefs -> prefs[URARTU_FM_KEY] = enabled }
    }

    override val readerFontScale: Flow<Float> = store.data.map { prefs ->
        prefs[READER_FONT_KEY] ?: 1f
    }

    override suspend fun setReaderFontScale(scale: Float) {
        store.edit { prefs -> prefs[READER_FONT_KEY] = scale }
    }

    override val readerNightMode: Flow<Boolean> = store.data.map { prefs ->
        prefs[READER_NIGHT_KEY] ?: false
    }

    override suspend fun setReaderNightMode(enabled: Boolean) {
        store.edit { prefs -> prefs[READER_NIGHT_KEY] = enabled }
    }

    override val readerBookmarks: Flow<Set<String>> = store.data.map { prefs ->
        prefs[READER_BOOKMARKS_KEY] ?: emptySet()
    }

    override suspend fun toggleBookmark(key: String) {
        store.edit { prefs ->
            val current = prefs[READER_BOOKMARKS_KEY] ?: emptySet()
            prefs[READER_BOOKMARKS_KEY] = if (key in current) current - key else current + key
        }
    }

    companion object {
        private val SKIN_KEY = stringPreferencesKey("skin")
        private val URARTU_FM_KEY = booleanPreferencesKey("urartu_fm_enabled")
        private val READER_FONT_KEY = floatPreferencesKey("reader_font_scale")
        private val READER_NIGHT_KEY = booleanPreferencesKey("reader_night_mode")
        private val READER_BOOKMARKS_KEY = stringSetPreferencesKey("reader_bookmarks")
    }
}
