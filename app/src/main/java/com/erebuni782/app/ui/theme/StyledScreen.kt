package com.erebuni782.app.ui.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

/**
 * ТОНКАЯ стилизация экрана: бледный фоновый паттерн + узкие боковые
 * полоски-клинья + тонкий верх/низ. НЕ перекрывает контент.
 * 3 стиля: керамические круги / клинописные клинья / волны.
 */
@Composable
fun StyledScreen(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val skin = LocalSkin.current
    val primary = MaterialTheme.colorScheme.primary
    val bg = MaterialTheme.colorScheme.background
    // Бледный но ВИДИМЫЙ (5-6%)
    val patternAlpha = 0.055f
    val borderAlpha = 0.25f

    Box(modifier = modifier.fillMaxSize()) {
        // ── фоновый паттерн (бледный, не мешает чтению) ──
        Canvas(Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            drawRect(color = bg)
            val c = primary.copy(alpha = patternAlpha)

            when (skin.ornament) {
                OrnamentStyle.CHEVRON -> {
                    // Pre-Urartu: бледные круги
                    val step = 120f
                    var y = 0f
                    while (y < h) {
                        var x = ((y / step).toInt() % 2) * step / 2
                        while (x < w) {
                            drawCircle(color = c, radius = 30f, center = Offset(x + 60f, y + 60f))
                            drawCircle(color = c.copy(alpha = patternAlpha * 1.5f), radius = 12f, center = Offset(x + 60f, y + 60f))
                            x += step
                        }
                        y += step
                    }
                }
                OrnamentStyle.CRENELLATION -> {
                    // Urartu: бледные клинья ▼
                    val cw = 50f; val ch = 35f
                    var row = 0; var y = 80f
                    while (y < h) {
                        var x = if (row % 2 == 0) 0f else cw / 2
                        while (x < w) {
                            val p = Path().apply {
                                moveTo(x, y); lineTo(x + cw, y); lineTo(x + cw / 2, y + ch); close()
                            }
                            drawPath(p, color = c)
                            x += cw * 2.5f
                        }
                        y += ch * 4; row++
                    }
                }
                OrnamentStyle.MEANDER -> {
                    // Post-Urartu: бледные волны
                    val amp = 35f; val period = 180f
                    var y = 100f
                    while (y < h) {
                        val wave = Path(); var x = 0f
                        wave.moveTo(x, y)
                        while (x < w) {
                            wave.quadraticBezierTo(x + period / 4, y - amp, x + period / 2, y)
                            wave.quadraticBezierTo(x + period * 3 / 4, y + amp, x + period, y)
                            x += period
                        }
                        drawPath(wave, color = c, style = Stroke(width = 3f))
                        y += 200f
                    }
                }
            }
        }

        // ── боковые полоски (4dp, видимая клинопись) ──
        Row(Modifier.fillMaxSize()) {
            Canvas(Modifier.width(4.dp).fillMaxHeight()) {
                val h = size.height; val w = size.width
                drawRect(color = primary.copy(alpha = borderAlpha))
                val step = h / 20
                var y = step / 2
                while (y < h) {
                    drawCircle(color = primary.copy(alpha = borderAlpha * 1.8f), radius = w * 0.4f, center = Offset(w / 2, y))
                    y += step
                }
            }
            Box(Modifier.weight(1f).fillMaxHeight()) { content() }
            Canvas(Modifier.width(4.dp).fillMaxHeight()) {
                val h = size.height; val w = size.width
                drawRect(color = primary.copy(alpha = borderAlpha))
                val step = h / 20
                var y = step / 2
                while (y < h) {
                    drawCircle(color = primary.copy(alpha = borderAlpha * 1.8f), radius = w * 0.4f, center = Offset(w / 2, y))
                    y += step
                }
            }
        }

        // ── верхняя полоса (4dp, клинописные клинья) ──
        Canvas(Modifier.fillMaxWidth().height(4.dp)) {
            val w = size.width; val h = size.height
            drawRect(color = primary.copy(alpha = borderAlpha * 2))
            var x = 0f
            while (x < w) {
                val wedge = Path().apply {
                    moveTo(x, 0f); lineTo(x + 16f, 0f); lineTo(x + 8f, h * 0.7f); close()
                }
                drawPath(wedge, color = primary.copy(alpha = borderAlpha * 2.5f))
                x += 32f
            }
        }

        // ── нижняя полоса ──
        Canvas(
            Modifier.fillMaxWidth().height(4.dp)
                .align(androidx.compose.ui.Alignment.BottomCenter)
        ) {
            val w = size.width; val h = size.height
            drawRect(color = primary.copy(alpha = borderAlpha * 2))
            var x = 0f
            while (x < w) {
                val wedge = Path().apply {
                    moveTo(x, h); lineTo(x + 16f, h); lineTo(x + 8f, h * 0.3f); close()
                }
                drawPath(wedge, color = primary.copy(alpha = borderAlpha * 2.5f))
                x += 32f
            }
        }
    }
}

/**
 * Карточка с ТОНКОЙ клинописной рамкой (не мешает контенту).
 */
@Composable
fun StyledCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val primary = MaterialTheme.colorScheme.primary
    Box(modifier = modifier) {
        // тонкая рамка
        Canvas(Modifier.matchParentSize()) {
            val w = size.width; val h = size.height
            val color = primary.copy(alpha = 0.25f)
            drawRoundRect(
                color = color,
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f),
                style = Stroke(width = 2f)
            )
            // малые клинья по углам
            val cw = 10f
            listOf(
                Offset(6f, 6f), Offset(w - 6f, 6f),
                Offset(6f, h - 6f), Offset(w - 6f, h - 6f)
            ).forEach { corner ->
                val wedge = Path().apply {
                    moveTo(corner.x - cw / 2, corner.y - cw / 4)
                    lineTo(corner.x + cw / 2, corner.y - cw / 4)
                    lineTo(corner.x, corner.y + cw / 2); close()
                }
                drawPath(wedge, color = color.copy(alpha = 0.4f))
            }
        }
        Box(Modifier.padding(12.dp)) { content() }
    }
}
