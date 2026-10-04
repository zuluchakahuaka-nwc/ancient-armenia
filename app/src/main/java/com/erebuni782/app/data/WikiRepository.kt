package com.erebuni782.app.data

import com.erebuni782.app.data.db.WikiArticleEntity
import java.util.Locale

/** Локализованное представление для UI. */
data class LocalizedContent(val title: String, val body: String)

fun WikiArticleEntity.resolve(localeTag: String): LocalizedContent {
    val lang = Locale.forLanguageTag(localeTag).language
    return when (lang) {
        "hy" -> LocalizedContent(titleHy, bodyHy)
        "ru" -> LocalizedContent(titleRu, bodyRu)
        else -> LocalizedContent(titleEn, bodyEn)
    }
}

data class WikiArticleUi(
    val id: String,
    val category: String,
    val section: String,
    val title: String,
    val body: String
)

interface WikiStore {
    suspend fun articles(localeTag: String): List<WikiArticleUi>
    suspend fun article(id: String, localeTag: String): WikiArticleUi?
}

/** P2: сид-контент из Room (10 статей). */
class WikiRepository(private val dao: com.erebuni782.app.data.db.WikiDao) : WikiStore {

    override suspend fun articles(localeTag: String): List<WikiArticleUi> =
        dao.allOnce().map { it.toUi(localeTag) }

    override suspend fun article(id: String, localeTag: String): WikiArticleUi? =
        dao.byId(id)?.toUi(localeTag)

    private fun WikiArticleEntity.toUi(localeTag: String): WikiArticleUi {
        val c = resolve(localeTag)
        return WikiArticleUi(id = id, category = category, section = section, title = c.title, body = c.body)
    }
}
