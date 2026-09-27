package com.erebuni782.app.ui.aerial

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.erebuni782.app.data.AerialMarkerUi
import kotlin.math.roundToInt

/**
 * Оверлей маркеров поверх аэрофото: тап в ручном режиме = нумерованный
 * маркер («объекты для внимания»). Маркеры позиционируются долей
 * контейнера (нормированные координаты 0..1). Компонентно-тестируем:
 * ноды с testTag marker_N.
 */
@Composable
fun MarkerOverlay(
    markers: List<AerialMarkerUi>,
    manualMode: Boolean,
    onImageTap: (Double, Double) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .testTag("aerial_canvas")
            .pointerInput(manualMode) {
                if (manualMode) {
                    detectTapGestures { offset ->
                        if (size.width > 0 && size.height > 0) {
                            onImageTap(
                                (offset.x / size.width).toDouble(),
                                (offset.y / size.height).toDouble()
                            )
                        }
                    }
                }
            }
    ) {
        markers.forEach { m ->
            Box(
                modifier = Modifier
                    .offset {
                        val half = 14.dp.roundToPx()
                        IntOffset(
                            (m.x * maxWidth.toPx().toDouble()).toInt() - half,
                            (m.y * maxHeight.toPx().toDouble()).toInt() - half
                        )
                    }
                    .size(28.dp)
                    .testTag("marker_${m.number}")
                    .background(
                        if (m.manual) MaterialTheme.colorScheme.primary else Color(0xFF2E7D32),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("${m.number}", color = Color.White)
            }
        }
    }
}
