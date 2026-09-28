package com.erebuni782.app

import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * E2E P2 (AGENTS.md §7): туристический шелл — вкладки, вики-статья, ридер,
 * аудио/Urartu.fm (тумблер), настройки языка/скина на лету.
 */
@RunWith(AndroidJUnit4::class)
class TouristShellE2E {

    @get:Rule
    val rule = createAndroidComposeRule<MainActivity>()

    private fun awaitText(text: String) {
        rule.waitUntil(timeoutMillis = 10_000) {
            rule.onAllNodesWithText(text, substring = true).fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun awaitTag(tag: String) {
        rule.waitUntil(timeoutMillis = 15_000) {
            rule.onAllNodesWithTag(tag).fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun pdfStatus(): String? {
        val nodes = rule.onAllNodesWithTag("pdf_status").fetchSemanticsNodes()
        if (nodes.isEmpty()) return null
        return try {
            nodes.first().config[androidx.compose.ui.semantics.SemanticsProperties.Text].first().text
        } catch (e: Exception) {
            null
        }
    }
    }

    private fun goneTag(tag: String) {
        rule.waitUntil(timeoutMillis = 10_000) {
            rule.onAllNodesWithTag(tag).fetchSemanticsNodes().isEmpty()
        }
    }

    @Test
    fun fullTouristFlow() {
        // нормализация EN/скины через TAG-якоря чипов (§7.5 — планшето-стабильно)
        awaitTag("nav_settings")
        rule.onNodeWithTag("nav_settings").performClick()
        awaitTag("lang_en")
        rule.onNodeWithTag("lang_en").performClick()
        awaitTag("skin_POST_URARTU")

        // ── гид → вики-статья
        rule.onNodeWithTag("nav_guide").performClick()
        awaitText("Erebuni fortress")
        rule.onNodeWithTag("nav_wiki").performClick()
        rule.onNodeWithText("Erebuni fortress").performClick()
        awaitText("Arin-Berd")

        // ── библиотека → книга → ридер (шрифт, закладка, главы)
        rule.onNodeWithTag("nav_library").performClick()
        awaitText("Erebuni: a fortress on Arin-Berd")
        rule.onNodeWithText("Erebuni: a fortress on Arin-Berd").performClick()
        awaitText("The hill and the inscription")
        rule.onNodeWithTag("reader_font_up").performClick()
        rule.onNodeWithTag("reader_bookmark").performClick()
        rule.onNodeWithTag("reader_next").performClick()
        awaitText("Walls and palace")

        // ── аудио: выходим из ридера «назад» → Library (restoreState вернул бы книгу)
        rule.onNodeWithTag("reader_back").performClick()
        awaitTag("tab_audio")
        rule.onNodeWithTag("tab_audio").performClick()
        awaitTag("station_toggle")
        rule.onNodeWithTag("station_toggle").performClick() // OFF
        rule.onNodeWithTag("station_toggle").performClick() // ON → станция играет офлайн-пакет
        awaitTag("miniplayer_title")
        rule.onNodeWithTag("nav_map").performClick()
        awaitText("40.1776")

        // ── PDF-читалка книг владельца (D1): Моисеева-1955 → страница → свайп
        rule.onNodeWithTag("nav_library").performClick()
        awaitTag("owner_books_header")
        rule.onNodeWithTag("books_scroll").performScrollToNode(hasTestTag("owner_book_moiseeva_1955"))
        rule.onNodeWithTag("owner_book_moiseeva_1955").performClick()
        awaitTag("pdf_status")
        rule.waitUntil(15_000) { pdfStatus()?.startsWith("page=1/") == true }
        rule.onNodeWithTag("pdf_pager").performTouchInput { swipeLeft() }
        rule.waitUntil(15_000) { pdfStatus()?.startsWith("page=2/") == true }

        // ── настройки: скин и язык на лету (TAG-якоря)
        rule.onNodeWithTag("nav_settings").performClick()
        awaitTag("skin_POST_URARTU")
        rule.onNodeWithTag("skin_POST_URARTU").performClick()
        rule.onNodeWithTag("skin_POST_URARTU").assertIsSelected()
        rule.onNodeWithTag("lang_ru").performClick()
        awaitText("Урарту")
        rule.onNodeWithTag("lang_en").performClick()
        awaitText("Urartu")
    }
}
