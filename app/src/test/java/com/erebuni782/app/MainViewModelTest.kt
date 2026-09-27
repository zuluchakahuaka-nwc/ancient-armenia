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

private class FakeSettingsStore(initial: SkinId = SkinId.URARTU) : SettingsStore {
    private val state = MutableStateFlow(initial)
    override val skin: MutableStateFlow<SkinId> = state
    override suspend fun setSkin(id: SkinId) { state.value = id }
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

    @Test
    fun `setSkin persists through store`() = runTest {
        val store = FakeSettingsStore(SkinId.PRE_URARTU)
        val vm = MainViewModel(store)
        assertEquals(SkinId.PRE_URARTU, vm.skin.value)
        vm.setSkin(SkinId.URARTU)
        advanceUntilIdle()
        assertEquals(SkinId.URARTU, store.skin.value)
    }
}
