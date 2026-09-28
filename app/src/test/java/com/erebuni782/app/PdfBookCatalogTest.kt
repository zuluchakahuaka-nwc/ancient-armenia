package com.erebuni782.app

import com.erebuni782.app.data.book.PdfBookCatalog
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PdfBookCatalogTest {

    private val fixture = """
        {"license": "test",
         "books": [
           {"id":"b_2011","title":"История и культура","author":"Пиотровский Б.Б.","year":2011,
            "file":"a.pdf","format":"pdf"},
           {"id":"b_1955","title":"В древнем царстве Урарту","author":"Моисеева К.М.","year":1955,
            "file":"b.pdf","format":"pdf"},
           {"id":"b_1959","title":"Ванское царство","author":"Пиотровский Б.Б.","year":1959,
            "file":"c.djvu","format":"djvu"}
         ]}
    """.trimIndent()

    @Test
    fun `parse sorts by year and flags readable`() {
        val books = PdfBookCatalog.parse(fixture)
        assertEquals(3, books.size)
        assertEquals("b_1955", books.first().id)
        assertEquals("b_2011", books.last().id)
        assertTrue(books.first().isReadable)
        assertFalse(books.first { it.id == "b_1959" }.isReadable)
    }
}
