package com.erebuni782.app

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.click
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * E2E P4 (AGENTS.md §7 + §7.5): аэро — сессия → демо-фото → сшивка →
 * ручной тап-маркер → автодетекция → геопривязка → GeoJSON → 3D glTF.
 * Все ожидания — через всегда-скомпонованную статус-строку aerial_status.
 */
@RunWith(AndroidJUnit4::class)
class AerialFlowE2E {

    @get:Rule
    val rule = createAndroidComposeRule<MainActivity>()

    private fun statusText(): String? {
        val nodes = rule.onAllNodesWithTag("aerial_status").fetchSemanticsNodes()
        if (nodes.isEmpty()) return null
        return try {
            nodes.first().config[SemanticsProperties.Text].first().text
        } catch (e: Exception) {
            null
        }
    }

    private fun statusNumber(token: String): Int? =
        statusText()?.let { Regex("$token=(\\d+)").find(it)?.groupValues?.get(1)?.toInt() }

    private fun awaitStatus(token: String, expected: Int, timeout: Long = 20_000) {
        rule.waitUntil(timeoutMillis = timeout) {
            statusNumber(token) == expected
        }
    }

    private fun awaitText(text: String) {
        rule.waitUntil(timeoutMillis = 15_000) {
            rule.onAllNodesWithText(text, substring = true).fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun awaitTag(tag: String) {
        rule.waitUntil(timeoutMillis = 15_000) {
            rule.onAllNodesWithTag(tag).fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test
    fun aerialSession_fullPipeline() {
        // EN + PIN (адаптивно) + вход в аэро
        awaitTag("nav_settings")
        rule.onNodeWithTag("nav_settings").performClick()
        awaitTag("lang_en")
        rule.onNodeWithTag("lang_en").performClick()
        awaitTag("skin_POST_URARTU")
        rule.onNodeWithTag("open_employee").performClick()
        awaitTag("pin_input")
        val setup = rule.onAllNodesWithTag("pin_confirm").fetchSemanticsNodes().isNotEmpty()
        rule.onNodeWithTag("pin_input").performTextReplacement("1234")
        if (setup) rule.onNodeWithTag("pin_confirm").performTextReplacement("1234")
        rule.onNodeWithTag("pin_submit").performClick()
        awaitTag("open_aerial")
        rule.onNodeWithTag("open_aerial").performClick()

        // новая сессия
        awaitTag("new_aerial_session")
        rule.onNodeWithTag("new_aerial_session").performClick()
        awaitTag("load_demo_photos")
        awaitStatus("markers", 0)

        // демо-фото → сшивка (статус: stitched=1, busy=0)
        rule.onNodeWithTag("load_demo_photos").performClick()
        awaitText("Photos: 2")
        rule.onNodeWithTag("stitch_button").performClick()
        awaitStatus("stitched", 1)

        // ручной тап-маркер → статус markers=1
        rule.onNodeWithTag("aerial_canvas").performTouchInput { click(center) }
        awaitStatus("markers", 1)

        // авто-режим, слабый уровень → детекция → markers >= 2
        rule.onNodeWithTag("mode_auto").performClick()
        awaitTag("level_WEAK")
        rule.onNodeWithTag("aerial_scroll").performScrollToNode(hasTestTag("detect_button"))
        rule.onNodeWithTag("detect_button").performClick()
        rule.waitUntil(timeoutMillis = 20_000) { (statusNumber("markers") ?: 0) >= 2 }

        // ручная геопривязка (D8) → статус geo=1
        rule.onNodeWithTag("aerial_scroll").performScrollToNode(hasTestTag("geo_link"))
        rule.onNodeWithTag("geo_link").performClick()
        awaitTag("geo_lat")
        rule.onNodeWithTag("geo_lat").performTextReplacement("40.1776")
        rule.onNodeWithTag("geo_lon").performTextReplacement("44.5164")
        rule.onNodeWithTag("geo_save").performClick()
        awaitStatus("geo", 1)

        // экспорт GeoJSON (кнопки ниже сгиба — скролл контейнера)
        rule.onNodeWithTag("aerial_scroll").performScrollToNode(hasTestTag("export_geojson"))
        rule.onNodeWithTag("export_geojson").performClick()
        awaitText("markers.geojson")

        // 3D реконструкция (D6): Урарту по умолчанию
        rule.onNodeWithTag("aerial_scroll").performScrollToNode(hasTestTag("open_3d"))
        rule.onNodeWithTag("open_3d").performClick()
        awaitTag("generate_3d")
        rule.onNodeWithTag("generate_3d").performClick()
        awaitText("reconstruction_urartu.gltf")
    }
}
