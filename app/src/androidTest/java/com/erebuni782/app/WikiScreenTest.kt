package com.erebuni782.app

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.erebuni782.app.ui.WikiScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** Component: вики рендерит сид-статьи из Room (граф приложения уже инициализирован). */
@RunWith(AndroidJUnit4::class)
class WikiScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun wikiList_showsSeedArticles() {
        // раздел влияет на фильтр списка: приводим к урартскому (за e2e мог остаться ancient)
        kotlinx.coroutines.runBlocking {
            AppGraph.settings.setSection(com.erebuni782.app.data.Sections.URARTU)
        }
        composeRule.setContent { WikiScreen(onOpenArticle = {}) }
        val firstTitle = "Erebuni fortress" // сид en-локали
        composeRule.waitUntil(10_000) {
            composeRule.onAllNodesWithText(firstTitle).fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText(firstTitle).assertExists()
    }
}
