package com.erebuni782.app

import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextReplacement
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * E2E P5: экспорт подписанного дампа → импорт раунд-трип → loopback-синк.
 * Все ожидания — через якорь export_status (AGENTS.md §7.5).
 */
@RunWith(AndroidJUnit4::class)
class ExportSyncE2E {

    @get:Rule
    val rule = createAndroidComposeRule<MainActivity>()

    private fun awaitTag(tag: String) {
        rule.waitUntil(timeoutMillis = 15_000) {
            rule.onAllNodesWithTag(tag).fetchSemanticsNodes().isNotEmpty()
        }
    }

    /**
     * Первый запуск (v0.2.2+): LanguagePicker → Onboarding → Welcome → main.
     * Холодный старт APK ~385МБ → щедрый таймаут. Идём до nav_settings.
     */
    private fun skipWelcomeIfShown() {
        rule.waitUntil(timeoutMillis = 30_000) {
            rule.onAllNodesWithTag("lang_pick_en").fetchSemanticsNodes().isNotEmpty() ||
                rule.onAllNodesWithTag("ob_skip").fetchSemanticsNodes().isNotEmpty() ||
                rule.onAllNodesWithTag("btn_tourist").fetchSemanticsNodes().isNotEmpty() ||
                rule.onAllNodesWithTag("nav_settings").fetchSemanticsNodes().isNotEmpty()
        }
        if (rule.onAllNodesWithTag("lang_pick_en").fetchSemanticsNodes().isNotEmpty()) {
            rule.onNodeWithTag("lang_pick_en").performClick()
        }
        rule.waitUntil(timeoutMillis = 10_000) {
            rule.onAllNodesWithTag("ob_skip").fetchSemanticsNodes().isNotEmpty() ||
                rule.onAllNodesWithTag("btn_tourist").fetchSemanticsNodes().isNotEmpty() ||
                rule.onAllNodesWithTag("nav_settings").fetchSemanticsNodes().isNotEmpty()
        }
        if (rule.onAllNodesWithTag("ob_skip").fetchSemanticsNodes().isNotEmpty()) {
            rule.onNodeWithTag("ob_skip").performClick()
        }
        rule.waitUntil(timeoutMillis = 10_000) {
            rule.onAllNodesWithTag("btn_tourist").fetchSemanticsNodes().isNotEmpty() ||
                rule.onAllNodesWithTag("nav_settings").fetchSemanticsNodes().isNotEmpty()
        }
        if (rule.onAllNodesWithTag("btn_tourist").fetchSemanticsNodes().isNotEmpty()) {
            rule.onNodeWithTag("btn_tourist").performClick()
        }
        rule.waitUntil(timeoutMillis = 10_000) {
            rule.onAllNodesWithTag("nav_settings").fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun awaitStatus(token: String) {
        rule.waitUntil(timeoutMillis = 20_000) {
            rule.onAllNodesWithTag("export_status").fetchSemanticsNodes()
                .any { node ->
                    try {
                        node.config[androidx.compose.ui.semantics.SemanticsProperties.Text]
                            .first().text.contains(token)
                    } catch (e: Exception) {
                        false
                    }
                }
        }
    }

    @Test
    fun exportImportLoopbackSync() {
        skipWelcomeIfShown()
        // EN + PIN + сотрудник
        awaitTag("nav_settings")
        rule.onNodeWithTag("nav_settings").performClick()
        awaitTag("lang_en")
        rule.onNodeWithTag("lang_en").performClick()
        awaitTag("open_employee")
        rule.onNodeWithTag("open_employee").performClick()
        awaitTag("pin_input")
        val setup = rule.onAllNodesWithTag("pin_confirm").fetchSemanticsNodes().isNotEmpty()
        rule.onNodeWithTag("pin_input").performTextReplacement("1234")
        if (setup) rule.onNodeWithTag("pin_confirm").performTextReplacement("1234")
        rule.onNodeWithTag("pin_submit").performClick()
        awaitTag("employee_scroll")
        rule.onNodeWithTag("employee_scroll").performScrollToNode(hasTestTag("btn_export"))
        awaitTag("btn_export")

        // экспорт (пасфраза по умолчанию уже в поле)
        rule.onNodeWithTag("btn_export").performClick()
        awaitStatus("EXPORT ok artifacts=")

        // импорт того же файла: подпись сходится, данные идемпотентны
        rule.onNodeWithTag("btn_import").performClick()
        awaitStatus("IMPORT ok artifacts=")

        // loopback-синк: полный протокол в одном процессе
        rule.onNodeWithTag("btn_sync").performClick()
        awaitStatus("SYNC ok sent=")

        // D3: смена PIN 1234 → 9999 → обратно 1234
        rule.onNodeWithTag("btn_change_pin").performClick()
        awaitTag("pin_current")
        rule.onNodeWithTag("pin_current").performTextReplacement("1234")
        rule.onNodeWithTag("pin_new").performTextReplacement("9999")
        rule.onNodeWithTag("pin_new_confirm").performTextReplacement("9999")
        rule.onNodeWithTag("pin_change_apply").performClick()
        awaitStatus("PIN ok changed=1")
        rule.onNodeWithTag("btn_change_pin").performClick()
        awaitTag("pin_current")
        rule.onNodeWithTag("pin_current").performTextReplacement("9999")
        rule.onNodeWithTag("pin_new").performTextReplacement("1234")
        rule.onNodeWithTag("pin_new_confirm").performTextReplacement("1234")
        rule.onNodeWithTag("pin_change_apply").performClick()
        awaitStatus("PIN ok changed=1")
    }
}
