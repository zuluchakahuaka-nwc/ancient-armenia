package com.erebuni782.app.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.erebuni782.app.AppGraph
import com.erebuni782.app.audio.PlayerController
import com.erebuni782.app.audio.PlayerUiState
import com.erebuni782.app.data.AudioStore
import com.erebuni782.app.data.SettingsStore
import com.erebuni782.app.data.URARTU_FM_PACK
import com.erebuni782.app.data.UserTrackUi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Состояние аудио-секции Библиотеки: моя музыка, гиды, станция (D9/D10). */
class LibraryViewModel(
    private val audio: AudioStore,
    private val settings: SettingsStore,
    private val player: PlayerController
) : ViewModel() {

    val stationEnabled: StateFlow<Boolean> = settings.urartuFmEnabled
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    val playerState: StateFlow<PlayerUiState> = player.state

    /** Мини-плеер показывается только после взаимодействия юзера с плеером. */
    private val _userInteracted = kotlinx.coroutines.flow.MutableStateFlow(false)
    val userInteractedWithPlayer: StateFlow<Boolean> = _userInteracted.asStateFlow()

    fun markPlayerInteracted() { _userInteracted.value = true }

    /** Транспорт станции: play/pause; из idle при включённой станции — старт. */
    fun stationPlayPause() {
        markPlayerInteracted()
        val st = player.state.value
        if (!st.hasContent) {
            if (stationEnabled.value) {
                viewModelScope.launch { player.startStation(URARTU_FM_PACK) }
            }
        } else {
            player.togglePlayPause()
        }
    }

    fun stationStop() { markPlayerInteracted(); player.stop() }
    fun stationNext() { markPlayerInteracted(); player.next() }
    fun stationPrevious() { markPlayerInteracted(); player.previous() }

    fun userTracks(localeTag: String): Flow<List<UserTrackUi>> = audio.userTracks(localeTag)

    fun guides(localeTag: String) = audio.guides(localeTag)

    fun addTrack(uri: String, title: String) {
        viewModelScope.launch { audio.addUserTrack(uri, title) }
    }

    fun removeTrack(id: Long) {
        viewModelScope.launch { audio.removeUserTrack(id) }
    }

    fun playTrack(uri: String, title: String) = player.playUri(uri, title)

    fun togglePlayPause() = player.togglePlayPause()

    fun stopPlayer() = player.stop()

    fun setStation(enabled: Boolean) {
        markPlayerInteracted()
        viewModelScope.launch {
            settings.setUrartuFmEnabled(enabled)
            if (enabled) player.startStation(URARTU_FM_PACK) else player.stop()
        }
    }

    companion object {
        fun factory() = viewModelFactory {
            initializer {
                LibraryViewModel(AppGraph.audio, AppGraph.settings, AppGraph.player)
            }
        }
    }
}
