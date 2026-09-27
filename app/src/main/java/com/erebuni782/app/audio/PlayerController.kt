package com.erebuni782.app.audio

import com.erebuni782.app.data.StationTrack
import kotlinx.coroutines.flow.StateFlow

/** Абстракция над плеером (подменяется фейком в unit-тестах). */
interface PlayerController {
    val state: StateFlow<PlayerUiState>
    fun startStation(tracks: List<StationTrack>, resolveTitle: (Int) -> String)
    fun playUri(uri: String, title: String)
    fun togglePlayPause()
    fun stop()
}
