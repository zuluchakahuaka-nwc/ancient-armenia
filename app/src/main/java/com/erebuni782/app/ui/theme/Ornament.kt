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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

/**
 * Орнаментальный разделитель — декоративный элемент, у каждого скина свой
 * паттерн (керамика-шеврон / зубцы крепости / эллинистический меандр).
 * В P6 заменяется/дополняется ассетами из раскопочной живописи.
 */
@Composable
fun OrnamentalDivider(
    modifier: Modifier = Modifier,
    pattern: OrnamentStyle = LocalSkin.current.ornament,
    color: Color = MaterialTheme.colorScheme.primary
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(14.dp)
            .testTag("ornament")
    ) {
        val h = size.height
        when (pattern) {
            OrnamentStyle.CHEVRON -> {
                val period = 22f
                var x = 0f
                while (x < size.width) {
                    val path = Path().apply {
                        moveTo(x, h)
                        lineTo(x + period / 2, 0f)
                        lineTo(x + period, h)
                    }
                    drawPath(path, color = color, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f))
                    x += period + 10f
                }
            }
            OrnamentStyle.CRENELLATION -> {
                val tooth = 16f
                val gap = 8f
                var x = 0f
                while (x < size.width) {
                    drawRect(
                        color = color,
                        topLeft = Offset(x, h / 3),
                        size = Size(tooth, h * 2 / 3)
                    )
                    x += tooth + gap
                }
            }
            OrnamentStyle.MEANDER -> {
                val period = 28f
                var x = 0f
                while (x < size.width) {
                    val path = Path().apply {
                        moveTo(x, h)
                        lineTo(x, h / 3)
                        lineTo(x + period / 2, h / 3)
                        lineTo(x + period / 2, h * 2 / 3)
                        lineTo(x + period, h * 2 / 3)
                        lineTo(x + period, 0f)
                    }
                    drawPath(path, color = color, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f))
                    x += period + 10f
                }
            }
        }
    }
}
