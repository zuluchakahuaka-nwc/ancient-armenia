package com.erebuni782.app.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.erebuni782.app.ui.theme.SkinId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore(name = "settings")

/** Абстракция хранилища настроек (для подмены в unit-тестах). */
interface SettingsStore {
    val skin: Flow<SkinId>
    suspend fun setSkin(id: SkinId)
}

/** DataStore-реализация: скин персистентен между запусками. */
class SettingsRepository(private val context: Context) : SettingsStore {

    private val store = context.settingsDataStore

    override val skin: Flow<SkinId> = store.data.map { prefs ->
        val raw = prefs[SKIN_KEY]
        SkinId.entries.firstOrNull { it.name == raw } ?: SkinId.URARTU
    }

    override suspend fun setSkin(id: SkinId) {
        store.edit { prefs -> prefs[SKIN_KEY] = id.name }
    }

    companion object {
        private val SKIN_KEY = stringPreferencesKey("skin")
    }
}
