package com.erebuni782.app

import com.erebuni782.app.data.SettingsStore
import com.erebuni782.app.ui.theme.SkinId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/** In-memory SettingsStore для JVM-тестов. */
class FakeSettingsStore : SettingsStore {
    private val _skin = MutableStateFlow(SkinId.URARTU)
    private val _fm = MutableStateFlow(true)
    private val _font = MutableStateFlow(1f)
    private val _night = MutableStateFlow(false)
    private val _marks = MutableStateFlow<Set<String>>(emptySet())

    override val skin = _skin
    override suspend fun setSkin(id: SkinId) { _skin.value = id }
    override val urartuFmEnabled = _fm
    override suspend fun setUrartuFmEnabled(enabled: Boolean) { _fm.value = enabled }
    override val readerFontScale = _font
    override suspend fun setReaderFontScale(scale: Float) { _font.value = scale }
    override val readerNightMode = _night
    override suspend fun setReaderNightMode(enabled: Boolean) { _night.value = enabled }
    override val readerBookmarks = _marks
    override suspend fun toggleBookmark(key: String) {
        val cur = _marks.value
        _marks.value = if (key in cur) cur - key else cur + key
    }
    private val _keepExif = MutableStateFlow(true)
    override val keepExif = _keepExif
    override suspend fun setKeepExif(enabled: Boolean) { _keepExif.value = enabled }
    private val _modeSelected = MutableStateFlow(false)
    override val modeSelected = _modeSelected
    override suspend fun setModeSelected() { _modeSelected.value = true }
    private val _onboardingShown = MutableStateFlow(true)
    override val onboardingShown = _onboardingShown
    override suspend fun setOnboardingShown() { _onboardingShown.value = true }
    private val _langSelected = MutableStateFlow(true)
    override val langSelected = _langSelected
    override suspend fun setLangSelected() { _langSelected.value = true }
}

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `default skin is Urartu`() {
        val vm = MainViewModel(FakeSettingsStore())
        assertEquals(SkinId.URARTU, vm.skin.value)
    }

    @Test
    fun `setSkin updates state`() = runTest {
        val store = FakeSettingsStore()
        val vm = MainViewModel(store)
        vm.setSkin(SkinId.POST_URARTU)
        advanceUntilIdle()
        assertEquals(SkinId.POST_URARTU, vm.skin.value)
        assertEquals(SkinId.POST_URARTU, store.skin.value)
    }
}
