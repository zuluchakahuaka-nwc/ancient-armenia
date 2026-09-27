package com.erebuni782.app

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.key
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.erebuni782.app.ui.PreviewCard
import com.erebuni782.app.ui.theme.Erebuni782Theme
import com.erebuni782.app.ui.theme.OrnamentStyle
import com.erebuni782.app.ui.theme.OrnamentalDivider
import com.erebuni782.app.ui.theme.SkinId
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private fun str(res: Int): String =
    InstrumentationRegistry.getInstrumentation().targetContext.getString(res)

/** Component-уровень: все скины рендерятся в одной композиции без падений. */
@RunWith(AndroidJUnit4::class)
class SkinRenderTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun allSkins_renderPreviewCard() {
        composeRule.setContent {
            Column {
                SkinId.entries.forEach { skin ->
                    key(skin) {
                        Erebuni782Theme(skinId = skin) { PreviewCard() }
                    }
                }
            }
        }
        val previews = composeRule.onAllNodesWithText(str(R.string.sample_card_title))
        assertEquals(SkinId.entries.size, previews.fetchSemanticsNodes().size)
        val ornaments = composeRule.onAllNodesWithTag("ornament")
        assertEquals(SkinId.entries.size, ornaments.fetchSemanticsNodes().size)
    }

    @Test
    fun allOrnamentStyles_render() {
        composeRule.setContent {
            Column {
                OrnamentStyle.entries.forEach { style ->
                    key(style) { OrnamentalDivider(pattern = style) }
                }
            }
        }
        val ornaments = composeRule.onAllNodesWithTag("ornament")
        assertEquals(OrnamentStyle.entries.size, ornaments.fetchSemanticsNodes().size)
    }

    @Test
    fun urartuSkin_rendersPreview() {
        composeRule.setContent {
            Erebuni782Theme(skinId = SkinId.URARTU) { PreviewCard() }
        }
        composeRule.waitForIdle()
        composeRule.onNodeWithText(str(R.string.sample_card_title)).assertExists()
    }
}
