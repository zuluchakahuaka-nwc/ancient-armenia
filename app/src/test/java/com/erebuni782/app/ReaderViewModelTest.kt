package com.erebuni782.app

import com.erebuni782.app.data.BookContent
import com.erebuni782.app.data.BookStore
import com.erebuni782.app.data.BookUi
import com.erebuni782.app.data.ChapterUi
import com.erebuni782.app.ui.reader.ReaderViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

class FakeBookStore(private val content: BookContent?) : BookStore {
    override suspend fun books(localeTag: String): List<BookUi> =
        content?.let { listOf(it.book) } ?: emptyList()

    override suspend fun book(id: String, localeTag: String): BookContent? = content
}

@OptIn(ExperimentalCoroutinesApi::class)
class ReaderViewModelTest {

    private lateinit var settings: FakeSettingsStore

    private val sample = BookContent(
        book = BookUi("b1", "Book", "Author", "CC0", chapterCount = 3),
        chapters = listOf(
            ChapterUi(0, "C0", "body0"),
            ChapterUi(1, "C1", "body1"),
            ChapterUi(2, "C2", "body2")
        )
    )

    @Before
    fun setUp() {
        settings = FakeSettingsStore()
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loads book and starts at chapter 0`() = runTest {
        val vm = ReaderViewModel("b1", FakeBookStore(sample), settings)
        advanceUntilIdle()
        assertEquals("C0", vm.state.value.currentChapter?.title)
        assertEquals(3, vm.state.value.chapterCount)
    }

    @Test
    fun `chapter navigation clamps`() = runTest {
        val vm = ReaderViewModel("b1", FakeBookStore(sample), settings)
        advanceUntilIdle()
        vm.previous()
        assertEquals(0, vm.state.value.chapterIndex)
        vm.next(); vm.next(); vm.next()
        assertEquals(2, vm.state.value.chapterIndex)
    }

    @Test
    fun `bookmark toggles per chapter and persists`() = runTest {
        val vm = ReaderViewModel("b1", FakeBookStore(sample), settings)
        advanceUntilIdle()
        vm.toggleBookmark()
        advanceUntilIdle()
        assertTrue(vm.state.value.isCurrentBookmarked)
        vm.next()
        assertFalse(vm.state.value.isCurrentBookmarked)
        vm.toggleBookmark()
        advanceUntilIdle()
        assertEquals(setOf("b1:0", "b1:1"), settings.readerBookmarks.value)
        vm.toggleBookmark()
        advanceUntilIdle()
        assertEquals(setOf("b1:0"), settings.readerBookmarks.value)
    }

    @Test
    fun `font scale set and clamped`() = runTest {
        val vm = ReaderViewModel("b1", FakeBookStore(sample), settings)
        advanceUntilIdle()
        vm.setFontScale(5f)
        advanceUntilIdle()
        assertEquals(1.6f, vm.state.value.fontScale)
        vm.setFontScale(0.1f)
        advanceUntilIdle()
        assertEquals(0.8f, vm.state.value.fontScale)
    }

    @Test
    fun `night mode persists`() = runTest {
        val vm = ReaderViewModel("b1", FakeBookStore(sample), settings)
        advanceUntilIdle()
        vm.setNightMode(true)
        advanceUntilIdle()
        assertTrue(vm.state.value.nightMode)
        assertTrue(settings.readerNightMode.value)
    }
}
