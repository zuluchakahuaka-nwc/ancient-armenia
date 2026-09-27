package com.erebuni782.app

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.click
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.erebuni782.app.data.AerialMarkerUi
import com.erebuni782.app.ui.aerial.MarkerOverlay
import com.erebuni782.app.ui.theme.Erebuni782Theme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** Component: маркеры рендерятся нумерованными нодами, тап в ручном режиме. */
@RunWith(AndroidJUnit4::class)
class MarkerOverlayTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun markers_renderAsNumberedNodes() {
        composeRule.setContent {
            Erebuni782Theme {
                MarkerOverlay(
                    markers = listOf(
                        AerialMarkerUi(1, 1, 0.2, 0.3, manual = true, confidence = null, note = ""),
                        AerialMarkerUi(2, 2, 0.6, 0.7, manual = false, confidence = 2.5, note = "")
                    ),
                    manualMode = true,
                    onImageTap = { _, _ -> }
                )
            }
        }
        assertEquals(1, composeRule.onAllNodesWithTag("marker_1").fetchSemanticsNodes().size)
        assertTrue(composeRule.onAllNodesWithTag("marker_2").fetchSemanticsNodes().isNotEmpty())
    }

    @Test
    fun manualTap_reportsNormalizedCoordinates() {
        var tapped: Pair<Double, Double>? = null
        composeRule.setContent {
            Erebuni782Theme {
                MarkerOverlay(
                    markers = emptyList(),
                    manualMode = true,
                    onImageTap = { x, y -> tapped = x to y }
                )
            }
        }
        composeRule.onNodeWithTag("aerial_canvas").performTouchInput { click(center) }
        composeRule.waitForIdle()
        val t = tapped
        assertTrue("тап зарегистрирован", t != null)
        assertTrue("x≈0.5: ${t?.first}", kotlin.math.abs((t!!.first) - 0.5) < 0.05)
        assertTrue("y≈0.5: ${t.second}", kotlin.math.abs(t.second - 0.5) < 0.05)
    }
}
