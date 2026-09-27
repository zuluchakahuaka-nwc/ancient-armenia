package com.erebuni782.app.audio

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.erebuni782.app.data.StationTrack
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class PlayerUiState(
    val title: String? = null,
    val isPlaying: Boolean = false,
    val hasContent: Boolean = false
)

/**
 * Единственный плеер приложения (Media3/ExoPlayer): станция Urartu.fm,
 * личная музыка, аудиогиды — один источник, мини-плеер сверху навигации.
 */
class PlayerManager(context: Context) : PlayerController {

    private val appContext = context.applicationContext

    private val _state = MutableStateFlow(PlayerUiState())
    override val state: StateFlow<PlayerUiState> = _state

    private var player: ExoPlayer? = null

    private fun ensurePlayer(): ExoPlayer {
        player?.let { return it }
        val p = ExoPlayer.Builder(appContext).build()
        p.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _state.value = _state.value.copy(isPlaying = isPlaying)
            }
        })
        player = p
        return p
    }

    /** D10: станция играет офлайн-пакет из assets. */
    override fun startStation(tracks: List<StationTrack>, resolveTitle: (Int) -> String) {
        val p = ensurePlayer()
        p.clearMediaItems()
        tracks.forEach { track ->
            p.addMediaItem(
                MediaItem.Builder()
                    .setUri("asset:///${track.assetPath}")
                    .setMediaId(track.assetPath)
                    .build()
            )
        }
        _state.value = PlayerUiState(
            title = tracks.firstOrNull()?.let { resolveTitle(it.titleRes) },
            isPlaying = false,
            hasContent = true
        )
        p.prepare()
        p.playWhenReady = true
    }

    override fun playUri(uri: String, title: String) {
        val p = ensurePlayer()
        p.clearMediaItems()
        p.setMediaItem(MediaItem.fromUri(uri))
        _state.value = PlayerUiState(title = title, isPlaying = false, hasContent = true)
        p.prepare()
        p.playWhenReady = true
    }

    override fun togglePlayPause() {
        val p = player ?: return
        if (p.isPlaying) p.pause() else p.play()
    }

    override fun stop() {
        player?.stop()
        player?.clearMediaItems()
        _state.value = PlayerUiState()
    }

    fun release() {
        player?.release()
        player = null
        _state.value = PlayerUiState()
    }
}
