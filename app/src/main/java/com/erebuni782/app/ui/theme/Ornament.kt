package com.erebuni782.app.ui.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

/**
 * Богатый орнаментальный разделитель в урартском стиле.
 * Каждый скин — свой паттерн: шеврон-керамика / зубцы крепости / меандр.
 * Толстый (36dp), двухслойный: фон-полоса + узор + тонкая рамка.
 */
@Composable
fun OrnamentalDivider(
    modifier: Modifier = Modifier,
    pattern: OrnamentStyle? = null,
    color: Color = Color.Unspecified
) {
    val resolvedPattern = pattern ?: LocalSkin.current.ornament
    val primary = if (color == Color.Unspecified) MaterialTheme.colorScheme.primary else color
    val onPrimaryDim = primary.copy(alpha = 0.3f)
    val onPrimaryBright = primary.copy(alpha = 0.7f)

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(36.dp)
            .testTag("ornament")
    ) {
        val h = size.height
        val w = size.width

        when (resolvedPattern) {
            OrnamentStyle.CHEVRON -> {
                // фон-полоса
                drawRect(color = onPrimaryDim, topLeft = Offset(0f, h * 0.2f), size = Size(w, h * 0.6f))
                // крупные шевроны
                val period = w / 12f
                var x = 0f
                while (x < w) {
                    val path = Path().apply {
                        moveTo(x, h * 0.8f)
                        lineTo(x + period / 2, h * 0.2f)
                        lineTo(x + period, h * 0.8f)
                    }
                    drawPath(path, color = primary, style = Stroke(width = h * 0.08f))
                    // внутренний малый
                    val p2 = Path().apply {
                        moveTo(x + period * 0.2f, h * 0.7f)
                        lineTo(x + period * 0.5f, h * 0.35f)
                        lineTo(x + period * 0.8f, h * 0.7f)
                    }
                    drawPath(p2, color = onPrimaryBright, style = Stroke(width = h * 0.05f))
                    x += period * 1.4f
                }
                // рамки
                drawLine(primary, Offset(0f, h * 0.15f), Offset(w, h * 0.15f), strokeWidth = h * 0.03f)
                drawLine(primary, Offset(0f, h * 0.85f), Offset(w, h * 0.85f), strokeWidth = h * 0.03f)
            }

            OrnamentStyle.CRENELLATION -> {
                // фон
                drawRect(color = onPrimaryDim, topLeft = Offset(0f, h * 0.15f), size = Size(w, h * 0.7f))
                // крупные зубцы
                val tooth = w / 14f
                val gap = tooth * 0.4f
                var x = 0f
                while (x < w) {
                    drawRoundRect(
                        color = primary,
                        topLeft = Offset(x, h * 0.1f),
                        size = Size(tooth, h * 0.55f),
                        cornerRadius = CornerRadius(h * 0.02f)
                    )
                    // малый зубец между крупными
                    drawRoundRect(
                        color = onPrimaryBright,
                        topLeft = Offset(x + tooth + gap * 0.3f, h * 0.45f),
                        size = Size(gap * 0.4f, h * 0.35f),
                        cornerRadius = CornerRadius(h * 0.01f)
                    )
                    x += tooth + gap
                }
                // основа
                drawRect(color = primary, topLeft = Offset(0f, h * 0.65f), size = Size(w, h * 0.2f))
                // клинья на основе
                var cx = tooth / 3
                while (cx < w) {
                    drawRect(
                        color = onPrimaryDim,
                        topLeft = Offset(cx, h * 0.68f),
                        size = Size(tooth * 0.3f, h * 0.14f)
                    )
                    cx += tooth * 0.8f
                }
            }

            OrnamentStyle.MEANDER -> {
                // фон
                drawRect(color = onPrimaryDim, topLeft = Offset(0f, h * 0.1f), size = Size(w, h * 0.8f))
                // меандр (греческий ключ)
                val period = w / 8f
                val strokeWidth = h * 0.06f
                var x = 0f
                while (x < w) {
                    val path = Path().apply {
                        // вертикаль
                        moveTo(x, h * 0.8f)
                        lineTo(x, h * 0.2f)
                        // горизонталь вправо
                        lineTo(x + period * 0.4f, h * 0.2f)
                        // вниз
                        lineTo(x + period * 0.4f, h * 0.5f)
                        // вправо
                        lineTo(x + period * 0.7f, h * 0.5f)
                        // вверх
                        lineTo(x + period * 0.7f, h * 0.3f)
                        // вправо (выход)
                        lineTo(x + period, h * 0.3f)
                    }
                    drawPath(path, color = primary, style = Stroke(width = strokeWidth))
                    // точка в углу
                    drawCircle(
                        color = onPrimaryBright,
                        radius = strokeWidth * 0.8f,
                        center = Offset(x + period * 0.15f, h * 0.65f)
                    )
                    x += period * 1.2f
                }
                // рамки
                drawLine(primary, Offset(0f, h * 0.1f), Offset(w, h * 0.1f), strokeWidth = strokeWidth * 0.5f)
                drawLine(primary, Offset(0f, h * 0.9f), Offset(w, h * 0.9f), strokeWidth = strokeWidth * 0.5f)
            }
        }
    }
}

/**
 * Декоративный орнаментальный КАРД-БОРДЕР — обводка вокруг карточек.
 * Тонкая рамка с узором по углам.
 */
@Composable
fun OrnamentalBorder(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeWidth = 2.dp.toPx()
        val cornerSize = 12.dp.toPx()

        // рамка
        drawRoundRect(
            color = color.copy(alpha = 0.4f),
            cornerRadius = CornerRadius(cornerSize),
            style = Stroke(width = strokeWidth)
        )
        // уголки — клинья
        val cw = 6.dp.toPx()
        listOf(
            Offset(cornerSize * 0.5f, cornerSize * 0.5f),
            Offset(w - cornerSize * 0.5f, cornerSize * 0.5f),
            Offset(cornerSize * 0.5f, h - cornerSize * 0.5f),
            Offset(w - cornerSize * 0.5f, h - cornerSize * 0.5f)
        ).forEach { corner ->
            drawRect(
                color = color.copy(alpha = 0.6f),
                topLeft = Offset(corner.x - cw / 2, corner.y - cw / 2),
                size = Size(cw, cw)
            )
        }
    }
}
