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
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp

/**
 * ПОЛНАЯ стилизация экрана в историческом стиле скина.
 * Оборачивает контент: фоновый паттерн + боковые орнаменты + верх/низ.
 * Каждый скин — своя композиция декоративных элементов.
 */
@Composable
fun StyledScreen(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val skin = LocalSkin.current
    val primary = MaterialTheme.colorScheme.primary
    val bg = MaterialTheme.colorScheme.background
    val onBg = primary.copy(alpha = 0.06f) // очень бледный фон-паттерн

    Box(modifier = modifier.fillMaxSize()) {
        // ── фоновый паттерн (весь экран, бледный) ──
        Canvas(Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            drawRect(color = bg) // база

            when (skin.ornament) {
                OrnamentStyle.CHEVRON -> {
                    // Pre-Urartu: керамические отпечатки по диагонали
                    val step = 80f
                    var y = 0f
                    while (y < h) {
                        var x = (y / step).toInt() % 2 * step / 2
                        while (x < w) {
                            drawCircle(
                                color = onBg,
                                radius = 20f,
                                center = Offset(x + step / 2, y + step / 2)
                            )
                            // внутренняя точка
                            drawCircle(
                                color = onBg.copy(alpha = onBg.alpha * 1.5f),
                                radius = 8f,
                                center = Offset(x + step / 2, y + step / 2)
                            )
                            x += step
                        }
                        y += step
                    }
                }
                OrnamentStyle.CRENELLATION -> {
                    // Urartu: клинописные ряды (▼▼▼)
                    val cw = 35f
                    val ch = 25f
                    var row = 0
                    var y = 60f
                    while (y < h) {
                        var x = if (row % 2 == 0) 0f else cw / 2
                        while (x < w) {
                            // клин ▼
                            val path = Path().apply {
                                moveTo(x, y)
                                lineTo(x + cw, y)
                                lineTo(x + cw / 2, y + ch)
                                close()
                            }
                            drawPath(path, color = onBg)
                            x += cw * 1.8f
                        }
                        y += ch * 3
                        row++
                    }
                }
                OrnamentStyle.MEANDER -> {
                    // Post-Urartu: эллинистические волны
                    val amp = 25f
                    val period = 120f
                    var y = 80f
                    while (y < h) {
                        val wave = Path()
                        var x = 0f
                        wave.moveTo(x, y)
                        while (x < w) {
                            wave.quadraticBezierTo(x + period / 4, y - amp, x + period / 2, y)
                            wave.quadraticBezierTo(x + period * 3 / 4, y + amp, x + period, y)
                            x += period
                        }
                        drawPath(wave, color = onBg, style = Stroke(width = 4f))
                        y += 140f
                    }
                }
            }
        }

        // ── боковые вертикальные орнаменты ──
        Row(Modifier.fillMaxSize()) {
            // левая полоса
            Canvas(Modifier.width(8.dp).fillMaxHeight()) {
                val h = size.height
                val w = size.width
                val color = primary.copy(alpha = 0.3f)
                drawRect(color = color, topLeft = Offset(0f, 0f), size = Size(w, h))
                // клинья на полосе
                val step = h / 20
                var y = step / 2
                while (y < h) {
                    drawCircle(
                        color = primary.copy(alpha = 0.5f),
                        radius = w * 0.3f,
                        center = Offset(w / 2, y)
                    )
                    y += step
                }
            }

            // контент
            Box(Modifier.weight(1f).fillMaxHeight()) {
                content()
            }

            // правая полоса (зеркальная)
            Canvas(Modifier.width(8.dp).fillMaxHeight()) {
                val h = size.height
                val w = size.width
                val color = primary.copy(alpha = 0.3f)
                drawRect(color = color, topLeft = Offset(0f, 0f), size = Size(w, h))
                val step = h / 20
                var y = step / 2
                while (y < h) {
                    drawCircle(
                        color = primary.copy(alpha = 0.5f),
                        radius = w * 0.3f,
                        center = Offset(w / 2, y)
                    )
                    y += step
                }
            }
        }

        // ── верхний орнамент ──
        OrnamentalDivider(
            Modifier.fillMaxWidth(),
            pattern = skin.ornament,
            color = primary
        )

        // ── нижний орнамент ──
        Canvas(
            Modifier.fillMaxWidth().height(20.dp).align(androidx.compose.ui.Alignment.BottomCenter)
        ) {
            val w = size.width
            val h = size.height
            // простая полоса с клиньями
            drawRect(color = primary.copy(alpha = 0.3f), topLeft = Offset(0f, 0f), size = Size(w, h))
            var x = 0f
            while (x < w) {
                val wedge = Path().apply {
                    moveTo(x, 0f)
                    lineTo(x + 20f, 0f)
                    lineTo(x + 10f, h * 0.7f)
                    close()
                }
                drawPath(wedge, color = primary.copy(alpha = 0.5f))
                x += 40f
            }
        }
    }
}

/**
 * Стилизованная карточка с клинописной рамкой.
 */
@Composable
fun StyledCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val primary = MaterialTheme.colorScheme.primary
    val container = MaterialTheme.colorScheme.surface

    Box(modifier = modifier) {
        // клинописная рамка
        Canvas(Modifier.matchParentSize()) {
            val w = size.width
            val h = size.height
            val strokeW = 3f
            val color = primary.copy(alpha = 0.4f)

            // внешняя рамка
            drawRoundRect(
                color = color,
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(12f, 12f),
                style = Stroke(width = strokeW)
            )
            // внутренняя рамка (двойная линия)
            drawRoundRect(
                color = color.copy(alpha = 0.2f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f),
                style = Stroke(width = strokeW * 0.6f),
                topLeft = Offset(4f, 4f),
                size = Size(w - 8f, h - 8f)
            )
            // клинописные клинья по углам
            val cw = 18f
            listOf(
                Offset(8f, 8f),
                Offset(w - 8f, 8f),
                Offset(8f, h - 8f),
                Offset(w - 8f, h - 8f)
            ).forEach { corner ->
                val wedge = Path().apply {
                    moveTo(corner.x - cw / 2, corner.y - cw / 4)
                    lineTo(corner.x + cw / 2, corner.y - cw / 4)
                    lineTo(corner.x, corner.y + cw / 2)
                    close()
                }
                drawPath(wedge, color = color.copy(alpha = 0.6f))
            }
            // горизонтальные клинья по центрам сторон
            val midW = 14f
            // верх
            val topWedge = Path().apply {
                moveTo(w / 2 - midW / 2, 0f)
                lineTo(w / 2 + midW / 2, 0f)
                lineTo(w / 2, midW * 0.6f)
                close()
            }
            drawPath(topWedge, color = color.copy(alpha = 0.5f))
            // низ
            val botWedge = Path().apply {
                moveTo(w / 2 - midW / 2, h)
                lineTo(w / 2 + midW / 2, h)
                lineTo(w / 2, h - midW * 0.6f)
                close()
            }
            drawPath(botWedge, color = color.copy(alpha = 0.5f))
        }
        // контент с отступом для рамки
        Box(Modifier.padding(16.dp)) {
            content()
        }
    }
}
