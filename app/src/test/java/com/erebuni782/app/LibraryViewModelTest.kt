package com.erebuni782.app

import com.erebuni782.app.audio.PlayerController
import com.erebuni782.app.audio.PlayerUiState
import com.erebuni782.app.data.AudioStore
import com.erebuni782.app.data.GuideTrackUi
import com.erebuni782.app.data.StationTrack
import com.erebuni782.app.data.URARTU_FM_PACK
import com.erebuni782.app.data.UserTrackUi
import com.erebuni782.app.ui.library.LibraryViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FakeAudioStore : AudioStore {
    val users = MutableStateFlow<List<UserTrackUi>>(emptyList())
    val guideList = listOf(
        GuideTrackUi(1, "Guide A", 180, available = false),
        GuideTrackUi(2, "Guide B", 240, available = false)
    )

    override fun userTracks(localeTag: String): Flow<List<UserTrackUi>> = users
    override fun guides(localeTag: String): Flow<List<GuideTrackUi>> = MutableStateFlow(guideList)
    override suspend fun addUserTrack(uri: String, title: String) {
        users.value = users.value + UserTrackUi(id = users.value.size + 1L, uri = uri, title = title)
    }

    override suspend fun removeUserTrack(id: Long) {
        users.value = users.value.filterNot { it.id == id }
    }
}

class FakePlayerController : PlayerController {
    override val state = MutableStateFlow(PlayerUiState())
    var startedStationTracks: List<StationTrack>? = null
        private set
    var stopped = false
        private set
    var playedUri: String? = null
        private set

    override fun startStation(tracks: List<StationTrack>, resolveTitle: (Int) -> String) {
        startedStationTracks = tracks
        state.value = PlayerUiState(title = "station", isPlaying = true, hasContent = true)
    }

    override fun playUri(uri: String, title: String) {
        playedUri = uri
        state.value = PlayerUiState(title = title, isPlaying = true, hasContent = true)
    }

    override fun togglePlayPause() {}
    override fun stop() {
        stopped = true
        state.value = PlayerUiState()
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class LibraryViewModelTest {

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `urartu fm defaults ON (D10)`() = runTest {
        val vm = LibraryViewModel(FakeAudioStore(), FakeSettingsStore(), FakePlayerController())
        advanceUntilIdle()
        assertTrue(vm.stationEnabled.value)
    }

    @Test
    fun `station off stops player, on starts offline pack`() = runTest {
        val settings = FakeSettingsStore()
        val player = FakePlayerController()
        val vm = LibraryViewModel(FakeAudioStore(), settings, player)
        vm.setStation(false) { "t" }
        advanceUntilIdle()
        assertFalse(settings.urartuFmEnabled.value)
        assertTrue(player.stopped)

        vm.setStation(true) { "t" }
        advanceUntilIdle()
        assertTrue(settings.urartuFmEnabled.value)
        assertEquals(URARTU_FM_PACK.map { it.assetPath }, player.startedStationTracks?.map { it.assetPath })
        assertEquals(3, player.startedStationTracks?.size)
    }

    @Test
    fun `add and remove user track (D9)`() = runTest {
        val audio = FakeAudioStore()
        val vm = LibraryViewModel(audio, FakeSettingsStore(), FakePlayerController())
        vm.addTrack("content://media/1", "song.mp3")
        advanceUntilIdle()
        assertEquals(1, audio.users.value.size)
        assertEquals("song.mp3", audio.users.value.first().title)
        vm.removeTrack(audio.users.value.first().id)
        advanceUntilIdle()
        assertTrue(audio.users.value.isEmpty())
    }

    @Test
    fun `playTrack delegates to player`() = runTest {
        val player = FakePlayerController()
        val vm = LibraryViewModel(FakeAudioStore(), FakeSettingsStore(), player)
        vm.playTrack("content://media/2", "track")
        assertEquals("content://media/2", player.playedUri)
    }
}
