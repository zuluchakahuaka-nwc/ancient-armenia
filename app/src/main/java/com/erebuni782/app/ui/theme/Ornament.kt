package com.erebuni782.app.ui.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

/**
 * Клинописный орнаментальный разделитель — 3 исторических стиля:
 *
 * Pre-Urartu (ранний железный век): грубые керамические треугольники,
 * отпечатки пальцев гончара, примитивная штриховка.
 *
 * Urartu (флагман): клинописные клинья (▼◀▶) из фундаментных надписей,
 * зубцы крепостных стен, строгая геометрия базальта.
 *
 * Post-Urartu (эллинизм): греческий меандр, лотосы, плавные волны.
 */
@Composable
fun OrnamentalDivider(
    modifier: Modifier = Modifier,
    pattern: OrnamentStyle? = null,
    color: Color = Color.Unspecified
) {
    val resolvedPattern = pattern ?: LocalSkin.current.ornament
    val primary = if (color == Color.Unspecified) MaterialTheme.colorScheme.primary else color
    val dim = primary.copy(alpha = 0.25f)
    val mid = primary.copy(alpha = 0.5f)
    val bright = primary.copy(alpha = 0.8f)

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp)
            .testTag("ornament")
    ) {
        val h = size.height
        val w = size.width
        val cw = h * 0.22f // ширина клина

        when (resolvedPattern) {
            OrnamentStyle.CHEVRON -> {
                // PRE-URARTU: грубые керамические треугольники + штриховка
                // фон-полоса
                drawRect(color = dim, topLeft = Offset(0f, h * 0.15f), size = Size(w, h * 0.7f))
                // большие треугольники вниз (керамический орнамент)
                val period = w / 8f
                var x = 0f
                while (x < w) {
                    val path = Path().apply {
                        moveTo(x, h * 0.2f)
                        lineTo(x + period * 0.5f, h * 0.8f)
                        lineTo(x + period, h * 0.2f)
                        close()
                    }
                    drawPath(path, color = mid)
                    // внутренний треугольник (отпечаток)
                    val inner = Path().apply {
                        moveTo(x + period * 0.2f, h * 0.35f)
                        lineTo(x + period * 0.5f, h * 0.65f)
                        lineTo(x + period * 0.8f, h * 0.35f)
                        close()
                    }
                    drawPath(inner, color = bright)
                    x += period * 1.3f
                }
                // грубая штриховка сверху и снизу
                var sx = period * 0.3f
                while (sx < w) {
                    drawLine(dim, Offset(sx, h * 0.05f), Offset(sx + h * 0.1f, h * 0.12f), strokeWidth = h * 0.03f)
                    drawLine(dim, Offset(sx, h * 0.88f), Offset(sx + h * 0.1f, h * 0.95f), strokeWidth = h * 0.03f)
                    sx += period * 0.5f
                }
            }

            OrnamentStyle.CRENELLATION -> {
                // URARTU: клинописные клинья (▼) + зубцы крепости
                drawRect(color = dim, topLeft = Offset(0f, h * 0.1f), size = Size(w, h * 0.8f))
                // основа — стена
                drawRect(color = primary, topLeft = Offset(0f, h * 0.6f), size = Size(w, h * 0.25f))
                // зубцы (крепостная стена)
                val tooth = w / 14f
                val gap = tooth * 0.35f
                var x = 0f
                while (x < w) {
                    drawRect(
                        color = primary,
                        topLeft = Offset(x, h * 0.1f),
                        size = Size(tooth, h * 0.5f)
                    )
                    x += tooth + gap
                }
                // клинописные клинья на стене (▼ из надписей)
                var cx = tooth * 0.5f
                while (cx < w) {
                    // большой клин ▼
                    val wedge = Path().apply {
                        moveTo(cx - cw * 0.5f, h * 0.65f)
                        lineTo(cx + cw * 0.5f, h * 0.65f)
                        lineTo(cx, h * 0.85f)
                        close()
                    }
                    drawPath(wedge, color = bright)
                    // малый клин ◀
                    if (cx + tooth * 1.2f < w) {
                        val small = Path().apply {
                            moveTo(cx + tooth * 0.4f, h * 0.68f)
                            lineTo(cx + tooth * 0.4f + cw * 0.4f, h * 0.72f)
                            lineTo(cx + tooth * 0.4f, h * 0.76f)
                            close()
                        }
                        drawPath(small, color = mid)
                    }
                    cx += tooth * 1.5f
                }
                // рамки
                drawLine(primary, Offset(0f, h * 0.08f), Offset(w, h * 0.08f), strokeWidth = h * 0.03f)
                drawLine(primary, Offset(0f, h * 0.92f), Offset(w, h * 0.92f), strokeWidth = h * 0.03f)
            }

            OrnamentStyle.MEANDER -> {
                // POST-URARTU: греческий меандр + лотосы
                drawRect(color = dim, topLeft = Offset(0f, h * 0.1f), size = Size(w, h * 0.8f))
                val period = w / 6f
                val sw = h * 0.05f
                var x = 0f
                while (x < w) {
                    // меандр (греческий ключ)
                    val path = Path().apply {
                        moveTo(x, h * 0.8f)
                        lineTo(x, h * 0.2f)
                        lineTo(x + period * 0.35f, h * 0.2f)
                        lineTo(x + period * 0.35f, h * 0.55f)
                        lineTo(x + period * 0.6f, h * 0.55f)
                        lineTo(x + period * 0.6f, h * 0.3f)
                        lineTo(x + period, h * 0.3f)
                    }
                    drawPath(path, color = primary, style = Stroke(width = sw))
                    // лотос между меандрами
                    if (x + period * 0.75f < w) {
                        val lotus = Path().apply {
                            val lx = x + period * 0.5f
                            val ly = h * 0.65f
                            moveTo(lx, ly - h * 0.08f)         // верхушка
                            cubicTo(lx - cw, ly, lx - cw, ly + h * 0.06f, lx, ly + h * 0.08f)
                            cubicTo(lx + cw, ly + h * 0.06f, lx + cw, ly, lx, ly - h * 0.08f)
                        }
                        drawPath(lotus, color = bright)
                        // точка в лотосе
                        drawCircle(color = mid, radius = sw * 0.6f, center = Offset(x + period * 0.5f, h * 0.68f))
                    }
                    x += period * 1.1f
                }
                // волнистая линия сверху (эллинистический фриз)
                val wave = Path()
                var wx = 0f
                wave.moveTo(wx, h * 0.06f)
                while (wx < w) {
                    wave.quadraticBezierTo(wx + cw, h * 0.02f, wx + cw * 2f, h * 0.06f)
                    wx += cw * 2f
                }
                drawPath(wave, color = mid, style = Stroke(width = sw * 0.7f))
            }
        }
    }
}

/**
 * Декоративная клинописная рамка вокруг карточек.
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
            color = color.copy(alpha = 0.35f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerSize),
            style = Stroke(width = strokeWidth)
        )
        // клинописные клинья по углам (▼)
        val cw = 8.dp.toPx()
        listOf(
            Offset(cornerSize * 0.5f, cornerSize * 0.5f),
            Offset(w - cornerSize * 0.5f, cornerSize * 0.5f),
            Offset(cornerSize * 0.5f, h - cornerSize * 0.5f),
            Offset(w - cornerSize * 0.5f, h - cornerSize * 0.5f)
        ).forEach { corner ->
            val wedge = Path().apply {
                moveTo(corner.x - cw / 2, corner.y - cw / 4)
                lineTo(corner.x + cw / 2, corner.y - cw / 4)
                lineTo(corner.x, corner.y + cw / 2)
                close()
            }
            drawPath(wedge, color = color.copy(alpha = 0.6f))
        }
    }
}
