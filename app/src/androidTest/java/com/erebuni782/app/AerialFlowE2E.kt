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

    /** §7.5.2/7.5.8: скролл к тегу через Lazy-API (находит нескомпонованные айтемы). */
    private fun scrollToTag(tag: String) {
        rule.onNodeWithTag("aerial_scroll").performScrollToNode(hasTestTag(tag))
    }

    /**
     * Первый запуск (v0.2.2+): LanguagePicker → Onboarding → Welcome → main.
     * Холодный старт APK ~385МБ → щедрый таймаут. Идём до nav_settings.
     */
    private fun skipWelcomeIfShown() {
        rule.waitUntil(timeoutMillis = 30_000) {
            rule.onAllNodesWithTag("lang_pick_en").fetchSemanticsNodes().isNotEmpty() ||
                rule.onAllNodesWithTag("ob_skip").fetchSemanticsNodes().isNotEmpty() ||
                rule.onAllNodesWithTag("section_urartu").fetchSemanticsNodes().isNotEmpty() ||
                rule.onAllNodesWithTag("nav_settings").fetchSemanticsNodes().isNotEmpty()
        }
        if (rule.onAllNodesWithTag("lang_pick_en").fetchSemanticsNodes().isNotEmpty()) {
            rule.onNodeWithTag("lang_pick_en").performClick()
        }
        rule.waitUntil(timeoutMillis = 10_000) {
            rule.onAllNodesWithTag("ob_skip").fetchSemanticsNodes().isNotEmpty() ||
                rule.onAllNodesWithTag("section_urartu").fetchSemanticsNodes().isNotEmpty() ||
                rule.onAllNodesWithTag("nav_settings").fetchSemanticsNodes().isNotEmpty()
        }
        if (rule.onAllNodesWithTag("ob_skip").fetchSemanticsNodes().isNotEmpty()) {
            rule.onNodeWithTag("ob_skip").performClick()
        }
        rule.waitUntil(timeoutMillis = 10_000) {
            rule.onAllNodesWithTag("section_urartu").fetchSemanticsNodes().isNotEmpty() ||
                rule.onAllNodesWithTag("nav_settings").fetchSemanticsNodes().isNotEmpty()
        }
        if (rule.onAllNodesWithTag("section_urartu").fetchSemanticsNodes().isNotEmpty()) {
            rule.onNodeWithTag("section_urartu").performClick()
        }
        rule.waitUntil(timeoutMillis = 10_000) {
            rule.onAllNodesWithTag("nav_settings").fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test
    fun aerialSession_fullPipeline() {
        skipWelcomeIfShown()
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
        awaitStatus("markers", 0)

        // v0.2.x: дефолт — быстрая съёмка; демо-фото/сшивка живут в аэро-режиме
        rule.onNodeWithTag("mode_aerial_chip").performClick()
        awaitTag("load_demo_photos")
        awaitStatus("markers", 0)

        // демо-фото → сшивка (статус: stitched=1, busy=0)
        rule.onNodeWithTag("load_demo_photos").performClick()
        awaitText("Photos: 2")
        rule.onNodeWithTag("stitch_button").performClick()
        awaitStatus("stitched", 1)

        // ручной тап-маркер → статус markers=1
        scrollToTag("aerial_canvas")
        rule.onNodeWithTag("aerial_canvas").performTouchInput { click(center) }
        awaitStatus("markers", 1)

        // авто-режим, слабый уровень → детекция → markers >= 2
        scrollToTag("mode_auto")
        rule.onNodeWithTag("mode_auto").performClick()
        scrollToTag("level_WEAK")
        awaitTag("level_WEAK")
        scrollToTag("detect_button")
        rule.onNodeWithTag("detect_button").performClick()
        // §7.5.1: после скролла к detect статус-якорь выпал из композиции — вернуть
        scrollToTag("aerial_status")
        rule.waitUntil(timeoutMillis = 20_000) { (statusNumber("markers") ?: 0) >= 2 }

        // ручная геопривязка (D8) → статус geo=1
        scrollToTag("geo_link")
        rule.onNodeWithTag("geo_link").performClick()
        awaitTag("geo_lat")
        rule.onNodeWithTag("geo_lat").performTextReplacement("40.1776")
        rule.onNodeWithTag("geo_lon").performTextReplacement("44.5164")
        rule.onNodeWithTag("geo_save").performClick()
        scrollToTag("aerial_status")
        awaitStatus("geo", 1)

        // экспорт GeoJSON (кнопки ниже сгиба — скролл контейнера)
        scrollToTag("export_geojson")
        rule.onNodeWithTag("export_geojson").performClick()
        awaitText("markers.geojson")

        // 3D реконструкция (D6): Урарту по умолчанию
        scrollToTag("open_3d")
        rule.onNodeWithTag("open_3d").performClick()
        awaitTag("generate_3d")
        rule.onNodeWithTag("generate_3d").performClick()
        awaitText("reconstruction_urartu.gltf")
    }

    /**
     * QA-фикс: быстрая съёмка валила приложение (file://Uri → FileUriExposedException
     * на API 24+). Теперь TakePicture получает content://Uri от FileProvider.
     * Доказательство: процесс жив после запуска камеры и возврата назад.
     */
    @Test
    fun quickCapture_cameraLaunchDoesNotCrash() {
        skipWelcomeIfShown()
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
        awaitTag("new_aerial_session")
        rule.onNodeWithTag("new_aerial_session").performClick()
        awaitStatus("markers", 0)

        // §7.5.2: сначала скролл к кнопке, потом клик
        rule.onNodeWithTag("aerial_scroll").performScrollToNode(hasTestTag("btn_camera"))
        rule.onNodeWithTag("btn_camera").performClick()
        // камера (внешнее приложение) открывается; до фикса процесс умирал на launch().
        // Espresso.pressBack не работает через чужое приложение → инжектим клавишу глобально.
        Thread.sleep(1500)
        androidx.test.platform.app.InstrumentationRegistry.getInstrumentation()
            .sendKeyDownUpSync(android.view.KeyEvent.KEYCODE_BACK)
        // вернулись в живое приложение — статус-якорь на месте
        awaitTag("aerial_status")
    }
}
