package com.erebuni782.app.ui.reader

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.erebuni782.app.AppGraph
import com.erebuni782.app.data.BookContent
import com.erebuni782.app.data.BookStore
import com.erebuni782.app.data.SettingsStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale

data class ReaderState(
    val book: BookContent? = null,
    val chapterIndex: Int = 0,
    val fontScale: Float = 1f,
    val nightMode: Boolean = false,
    val bookmarks: Set<String> = emptySet()
) {
    val currentChapter get() = book?.chapters?.getOrNull(chapterIndex)
    val chapterCount get() = book?.chapters?.size ?: 0
    fun bookmarkKey(bookId: String = book?.book?.id.orEmpty()): String = "$bookId:$chapterIndex"
    val isCurrentBookmarked: Boolean get() = chapterIndex in bookmarkedIndexes
    val bookmarkedIndexes: Set<Int>
        get() = bookmarks
            .filter { it.startsWith("${book?.book?.id}:") }
            .mapNotNull { it.substringAfter(':').toIntOrNull() }
            .toSet()
}

/** Ридер: шрифт, ночной режим, закладки — всё персистентно (DataStore). */
class ReaderViewModel(
    private val bookId: String,
    books: BookStore,
    private val settings: SettingsStore
) : ViewModel() {

    private val content = MutableStateFlow<BookContent?>(null)
    private val chapterIndex = MutableStateFlow(0)

    val state: StateFlow<ReaderState> = combine(
        content, chapterIndex.asStateFlow(),
        settings.readerFontScale, settings.readerNightMode, settings.readerBookmarks
    ) { book, idx, font, night, marks ->
        ReaderState(
            book = book, chapterIndex = idx, fontScale = font,
            nightMode = night, bookmarks = marks
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, ReaderState())

    init {
        viewModelScope.launch {
            content.value = books.book(bookId, Locale.getDefault().toLanguageTag())
        }
    }

    fun setFontScale(scale: Float) {
        viewModelScope.launch { settings.setReaderFontScale(scale.coerceIn(0.8f, 1.6f)) }
    }

    fun setNightMode(enabled: Boolean) {
        viewModelScope.launch { settings.setReaderNightMode(enabled) }
    }

    fun toggleBookmark() {
        viewModelScope.launch { settings.toggleBookmark(state.value.bookmarkKey()) }
    }

    fun next() {
        if (chapterIndex.value < (content.value?.chapters?.size ?: 0) - 1) chapterIndex.value++
    }

    fun previous() {
        if (chapterIndex.value > 0) chapterIndex.value--
    }

    companion object {
        fun factory(bookId: String) = viewModelFactory {
            initializer { ReaderViewModel(bookId, AppGraph.books, AppGraph.settings) }
        }
    }
}
