package com.erebuni782.app.data.book

import android.content.Context
import org.json.JSONObject

/**
 * D1: каталог книг владельца (assets/books/manifest.json).
 * PDF открывается встроенной читалкой (PdfRenderer); DJVU — статус
 * «нужна конвертация в PDF на ПК» (движка DJVU в Android нет).
 */
data class OwnerBook(
    val id: String,
    val title: String,
    val author: String,
    val year: Int,
    val file: String,
    val format: String
) {
    val isReadable: Boolean get() = format == "pdf"
}

object PdfBookCatalog {

    fun load(context: Context): List<OwnerBook> {
        val json = context.assets.open("books/manifest.json").bufferedReader().use { it.readText() }
        return parse(json)
    }

    /** Чистый парсер — JVM-тестируется. */
    fun parse(manifestJson: String): List<OwnerBook> {
        val root = JSONObject(manifestJson)
        val arr = root.getJSONArray("books")
        val out = mutableListOf<OwnerBook>()
        for (i in 0 until arr.length()) {
            val b = arr.getJSONObject(i)
            out += OwnerBook(
                id = b.getString("id"),
                title = b.getString("title"),
                author = b.getString("author"),
                year = b.getInt("year"),
                file = b.getString("file"),
                format = b.optString("format", "pdf")
            )
        }
        return out.sortedBy { it.year }
    }

    fun find(context: Context, id: String): OwnerBook? =
        load(context).firstOrNull { it.id == id }
}
