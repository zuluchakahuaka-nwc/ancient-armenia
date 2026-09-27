package com.erebuni782.app

import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private fun str(res: Int): String =
    InstrumentationRegistry.getInstrumentation().targetContext.getString(res)

/**
 * E2E P1 (AGENTS.md §7): смена языка и скина на лету.
 * Язык пересоздаёт активность (per-app locales), скин — recomposition.
 */
@RunWith(AndroidJUnit4::class)
class LanguageSkinSwitchTest {

    @get:Rule
    val rule = createAndroidComposeRule<MainActivity>()

    private fun awaitText(text: String) {
        rule.waitUntil(timeoutMillis = 10_000) {
            rule.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test
    fun switchAllSkins_andLanguages_atRuntime() {
        // приведение к EN независимо от персистентной локали/скина прошлых сессий
        // (названия языков не локализуются — «English» кликабелен из любой локали)
        rule.waitUntil(timeoutMillis = 10_000) {
            rule.onAllNodesWithText("English").fetchSemanticsNodes().isNotEmpty()
        }
        rule.onNodeWithText("English").performClick()
        awaitText("Urartu")

        // стартовый язык теперь en: проходим все скины
        awaitText("Urartu")
        val enSkins = listOf("Pre-Urartu", "Urartu", "Post-Urartu")
        enSkins.forEach { label ->
            rule.onNodeWithText(label).performClick()
            rule.onNodeWithText(label).assertIsSelected()
        }

        // → русский: подписи скинов локализуются после recreate
        rule.onNodeWithText("Русский").performClick()
        awaitText("Урарту")
        rule.onNodeWithText("Пост-Урарту").performClick()
        rule.onNodeWithText("Пост-Урарту").assertIsSelected()

        // навигация home → about
        rule.onNodeWithText(str(R.string.action_about)).performClick()
        awaitText(str(R.string.about_title))
        rule.onNodeWithText(str(R.string.action_back)).performClick()
        awaitText("Урарту")

        // → армянский
        rule.onNodeWithText("Հայերեն").performClick()
        awaitText("Ուրարտու")
        rule.onNodeWithText("Ուրարտու").performClick()
        rule.onNodeWithText("Ուրարտու").assertIsSelected()

        // → обратно english (названия языков не локализуются — стабильные метки)
        rule.onNodeWithText("English").performClick()
        awaitText("Urartu")
        rule.onNodeWithText("Urartu").assertIsSelected()
    }
}
