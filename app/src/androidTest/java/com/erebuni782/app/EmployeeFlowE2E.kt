package com.erebuni782.app

import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextReplacement
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.erebuni782.app.data.EmployeeSession
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * E2E P3 (AGENTS.md §7): сотрудник — PIN → создание записи → рестарт →
 * данные живы → смена статуса → аудит.
 */
@RunWith(AndroidJUnit4::class)
class EmployeeFlowE2E {

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

    /** Пропускаем welcome-экран если показан (первый запуск). */
    private fun skipWelcomeIfShown() {
        rule.waitUntil(timeoutMillis = 5_000) {
            rule.onAllNodesWithTag("btn_tourist").fetchSemanticsNodes().isNotEmpty() ||
                rule.onAllNodesWithTag("nav_settings").fetchSemanticsNodes().isNotEmpty()
        }
        if (rule.onAllNodesWithTag("btn_tourist").fetchSemanticsNodes().isNotEmpty()) {
            rule.onNodeWithTag("btn_tourist").performClick()
        }
    }

    @Test
    fun pinGate_crud_restartSurvival_statusAudit() {
        val artifactTitle = "Stone-${System.currentTimeMillis()}"
        skipWelcomeIfShown()

        // нормализуем локаль EN
        awaitTag("nav_settings")
        rule.onNodeWithTag("nav_settings").performClick()
        awaitTag("lang_en")
        rule.onNodeWithTag("lang_en").performClick()
        awaitTag("skin_POST_URARTU")

        // гейт: адаптивно — SETUP если PIN ещё нет, иначе ENTER (PIN персистенен)
        rule.onNodeWithTag("open_employee").performClick()
        awaitTag("pin_input")
        val setupMode = rule.onAllNodesWithTag("pin_confirm").fetchSemanticsNodes().isNotEmpty()
        rule.onNodeWithTag("pin_input").performTextReplacement("1234")
        if (setupMode) {
            rule.onNodeWithTag("pin_confirm").performTextReplacement("1234")
        }
        rule.onNodeWithTag("pin_submit").performClick()
        awaitTag("add_artifact")

        // создаём запись (уникальный титул — иммунитет к прошлым прогонам)
        rule.onNodeWithTag("add_artifact").performClick()
        awaitTag("field_title")
        rule.onNodeWithTag("field_title").performTextReplacement(artifactTitle)
        rule.onNodeWithTag("category_STONE_BLOCK").performClick()
        rule.onNodeWithTag("field_address").performTextReplacement("Arin-Berd 1")
        rule.onNodeWithTag("save_artifact").performClick()
        awaitText(artifactTitle)

        // рестарт активности: сессия закрыта → guard сам ведёт на PIN-гейт;
        // после ввода PIN данные живы (Room персистентен)
        EmployeeSession.unlocked = false
        rule.activityRule.scenario.recreate()

        awaitTag("pin_input")
        rule.onNodeWithTag("pin_input").performTextReplacement("1234")
        rule.onNodeWithTag("pin_submit").performClick()
        awaitText(artifactTitle)

        // открываем, продвигаем статус, проверяем аудит
        rule.onNodeWithText(artifactTitle).performClick()
        awaitTag("advance_status")
        rule.onNodeWithTag("advance_status").performScrollTo()
        rule.onNodeWithTag("advance_status").performClick()
        // блок аудита ниже сгиба — скролл контейнера к нодам (Lazy API)
        rule.onNodeWithTag("edit_scroll")
            .performScrollToNode(hasText("STATUS_CHANGED", substring = true))
        awaitText("STATUS_CHANGED")
        rule.onNodeWithTag("edit_scroll")
            .performScrollToNode(hasText("CREATED", substring = true))
        awaitText("CREATED")
    }
}
