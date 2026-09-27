package com.erebuni782.app.data

import com.erebuni782.app.data.db.BookDao
import com.erebuni782.app.data.db.BookEntity
import com.erebuni782.app.data.db.ChapterEntity
import kotlinx.coroutines.flow.first
import java.util.Locale

data class BookUi(
    val id: String,
    val title: String,
    val author: String,
    val licenseNote: String,
    val chapterCount: Int
)

data class ChapterUi(val index: Int, val title: String, val body: String)

data class BookContent(val book: BookUi, val chapters: List<ChapterUi>)

interface BookStore {
    suspend fun books(localeTag: String): List<BookUi>
    suspend fun book(id: String, localeTag: String): BookContent?
}

/**
 * D1: пока 1 тестовая книга (sample_erebuni). Формат-адаптер: сейчас главы в Room;
 * Readium (EPUB) подключается отдельным BookSource в P6 без переделки UI.
 */
class BookRepository(private val dao: BookDao) : BookStore {

    override suspend fun books(localeTag: String): List<BookUi> {
        val books = dao.observeBooks().first()
        return books.map { it.toUi(localeTag) }
    }

    override suspend fun book(id: String, localeTag: String): BookContent? {
        val book = dao.bookById(id) ?: return null
        val chapters = dao.chaptersOf(id)
        return BookContent(book.toUi(localeTag, chapters.size), chapters.map { it.toUi(localeTag) })
    }

    private fun BookEntity.toUi(localeTag: String, chapterCount: Int = 0): BookUi {
        val lang = Locale.forLanguageTag(localeTag).language
        val (title, author) = when (lang) {
            "hy" -> titleHy to authorHy
            "ru" -> titleRu to authorRu
            else -> titleEn to authorEn
        }
        return BookUi(id = id, title = title, author = author, licenseNote = licenseNote, chapterCount = chapterCount)
    }

    private fun ChapterEntity.toUi(localeTag: String): ChapterUi {
        val lang = Locale.forLanguageTag(localeTag).language
        val (title, body) = when (lang) {
            "hy" -> titleHy to bodyHy
            "ru" -> titleRu to bodyRu
            else -> titleEn to bodyEn
        }
        return ChapterUi(index = chapterIndex, title = title, body = body)
    }
}
