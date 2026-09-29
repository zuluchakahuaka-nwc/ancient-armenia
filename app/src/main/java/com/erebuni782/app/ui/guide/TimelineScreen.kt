package com.erebuni782.app.ui.guide

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.erebuni782.app.R
import com.erebuni782.app.ui.theme.OrnamentalDivider
import com.erebuni782.app.ui.theme.StyledCard

/** Событие таймлайна: год до н.э. — отрицательный; approx = «~». */
data class TimelineEvent(
    val year: Int,
    val approx: Boolean,
    val major: Boolean = false,
    val titleRes: Int,
    val bodyRes: Int
)

val TIMELINE: List<TimelineEvent> = listOf(
    TimelineEvent(-860, approx = true, titleRes = R.string.tl_860_title, bodyRes = R.string.tl_860_body),
    TimelineEvent(-840, approx = true, titleRes = R.string.tl_840_title, bodyRes = R.string.tl_840_body),
    TimelineEvent(-825, approx = true, titleRes = R.string.tl_825_title, bodyRes = R.string.tl_825_body),
    TimelineEvent(-800, approx = true, titleRes = R.string.tl_800_title, bodyRes = R.string.tl_800_body),
    TimelineEvent(-782, approx = false, major = true, titleRes = R.string.tl_782_title, bodyRes = R.string.tl_782_body),
    TimelineEvent(-760, approx = true, titleRes = R.string.tl_760_title, bodyRes = R.string.tl_760_body),
    TimelineEvent(-735, approx = false, titleRes = R.string.tl_735_title, bodyRes = R.string.tl_735_body),
    TimelineEvent(-714, approx = false, titleRes = R.string.tl_714_title, bodyRes = R.string.tl_714_body),
    TimelineEvent(-680, approx = true, titleRes = R.string.tl_680_title, bodyRes = R.string.tl_680_body),
    TimelineEvent(-590, approx = true, titleRes = R.string.tl_590_title, bodyRes = R.string.tl_590_body),
    TimelineEvent(-539, approx = true, titleRes = R.string.tl_539_title, bodyRes = R.string.tl_539_body),
    TimelineEvent(1939, approx = false, titleRes = R.string.tl_1939_title, bodyRes = R.string.tl_1939_body),
    TimelineEvent(1950, approx = false, titleRes = R.string.tl_1950_title, bodyRes = R.string.tl_1950_body)
)

/** Таймлайн 860 до н.э. → XX век: вертикальная шкала с узлами-событиями. */
@Composable
fun TimelineScreen() {
    val primary = MaterialTheme.colorScheme.primary
    val nodeColor = MaterialTheme.colorScheme.tertiary

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = stringResource(R.string.timeline_title),
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = stringResource(R.string.timeline_subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            OrnamentalDivider(Modifier.padding(top = 8.dp))
            Spacer(Modifier.height(6.dp))
        }
        itemsIndexed(TIMELINE, key = { _, e -> e.year }) { _, event ->
            val yearLabel = if (event.year < 0) {
                val y = -event.year
                stringResource(
                    if (event.approx) R.string.timeline_year_bc else R.string.timeline_year_bc_exact,
                    y
                )
            } else {
                stringResource(R.string.timeline_year_ad, event.year)
            }
            Row(Modifier.fillMaxWidth().testTag("timeline_event_${event.year}")) {
                // шкала: узел + линия вниз
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        Modifier
                            .size(if (event.major) 18.dp else 12.dp)
                            .background(nodeColor, CircleShape)
                    )
                    Box(
                        Modifier
                            .width(2.dp)
                            .height(92.dp)
                            .background(primary.copy(alpha = 0.35f))
                    )
                }
                Spacer(Modifier.width(12.dp))
                StyledCard(Modifier.weight(1f).padding(bottom = 4.dp)) {
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(
                            text = yearLabel,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = primary
                        )
                        Text(
                            text = stringResource(event.titleRes),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = stringResource(event.bodyRes),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}
