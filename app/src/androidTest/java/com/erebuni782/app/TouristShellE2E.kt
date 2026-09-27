package com.erebuni782.app

import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
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
        rule.waitUntil(timeoutMillis = 10_000) {
            rule.onAllNodesWithTag(tag).fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun goneTag(tag: String) {
        rule.waitUntil(timeoutMillis = 10_000) {
            rule.onAllNodesWithTag(tag).fetchSemanticsNodes().isEmpty()
        }
    }

    @Test
    fun fullTouristFlow() {
        // ── нормализуем локаль в EN через настройки (названия языков не локализуются)
        awaitTag("nav_settings")
        rule.onNodeWithTag("nav_settings").performClick()
        awaitText("English")
        rule.onNodeWithText("English").performClick()
        awaitText("Post-Urartu")

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

        // ── настройки: скин и язык на лету
        rule.onNodeWithTag("nav_settings").performClick()
        awaitText("Post-Urartu")
        rule.onNodeWithText("Post-Urartu").performClick()
        rule.onNodeWithText("Post-Urartu").assertIsSelected()
        rule.onNodeWithText("Русский").performClick()
        awaitText("Урарту")
        rule.onNodeWithText("English").performClick()
        awaitText("Urartu")
    }
}
