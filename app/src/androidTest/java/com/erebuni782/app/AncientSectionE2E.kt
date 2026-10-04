package com.erebuni782.app

import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * E2E раздела «Древняя Армения» (§7): выбор раздела на первом запуске →
 * гид показывает Гарни (не Эребуни!) → статья → таймлайн с 405 годом
 * (алфавит Маштоца) и без урартских событий.
 */
@RunWith(AndroidJUnit4::class)
class AncientSectionE2E {

    @get:Rule
    val rule = createAndroidComposeRule<MainActivity>()

    private fun awaitTag(tag: String) {
        rule.waitUntil(timeoutMillis = 30_000) {
            rule.onAllNodesWithTag(tag).fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun tagExists(tag: String) =
        rule.onAllNodesWithTag(tag).fetchSemanticsNodes().isNotEmpty()

    private fun textExists(text: String) =
        rule.onAllNodesWithText(text, substring = true).fetchSemanticsNodes().isNotEmpty()

    @Test
    fun ancientSectionGuideWikiTimeline() {
        // холодный старт: LanguagePicker → Onboarding → SectionPicker (или сразу шелл)
        rule.waitUntil(timeoutMillis = 30_000) {
            tagExists("lang_pick_en") || tagExists("ob_skip") ||
                tagExists("section_urartu") || tagExists("nav_guide")
        }
        if (tagExists("lang_pick_en")) {
            rule.onNodeWithTag("lang_pick_en").performClick()
        }
        rule.waitUntil(timeoutMillis = 10_000) {
            tagExists("ob_skip") || tagExists("section_urartu") || tagExists("nav_guide")
        }
        if (tagExists("ob_skip")) {
            rule.onNodeWithTag("ob_skip").performClick()
        }

        // ── выбор раздела: Древняя Армения ──
        rule.waitUntil(timeoutMillis = 10_000) {
            tagExists("section_ancient") || tagExists("nav_guide")
        }
        if (tagExists("section_ancient")) {
            rule.onNodeWithTag("section_ancient").performClick()
        } else {
            // шелл уже поднят с чужим разделом (сериальный прогон): переключаем из настроек
            awaitTag("nav_settings")
            rule.onNodeWithTag("nav_settings").performClick()
            awaitTag("section_chip_ancient")
            rule.onNodeWithTag("section_chip_ancient").performClick()
            awaitTag("nav_guide")
            rule.onNodeWithTag("nav_guide").performClick()
        }

        // ── гид: карточки древних объектов; урартская Эребуни не течёт ──
        awaitTag("guide_list")
        rule.onNodeWithTag("guide_list").performScrollToNode(hasTestTag("open_timeline"))
        rule.onNodeWithTag("guide_list")
            .performScrollToNode(androidx.compose.ui.test.hasText("Garni", substring = true))
        rule.waitUntil(timeoutMillis = 10_000) { textExists("Garni") }
        rule.onNodeWithText("Temple of Garni").performClick()

        // ── статья вики Гарни открылась ──
        rule.waitUntil(timeoutMillis = 10_000) { textExists("Azat") }

        // ── назад → таймлайн раздела: 405 (алфавит) есть, 782 до н.э. (Эребуни) нет ──
        Espresso.pressBack()
        awaitTag("guide_list")
        rule.onNodeWithTag("guide_list").performScrollToNode(hasTestTag("open_timeline"))
        rule.onNodeWithTag("open_timeline").performClick()
        rule.waitUntil(timeoutMillis = 10_000) { tagExists("timeline_event_-331") }
        rule.onNode(hasScrollAction()).performScrollToNode(hasTestTag("timeline_event_405"))
        rule.waitUntil(timeoutMillis = 5_000) { tagExists("timeline_event_405") }
        assert(!tagExists("timeline_event_-782")) { "урартское событие 782 просочилось в древний раздел" }
    }
}
