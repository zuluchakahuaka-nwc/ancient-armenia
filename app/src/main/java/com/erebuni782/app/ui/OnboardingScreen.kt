package com.erebuni782.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.erebuni782.app.R
import com.erebuni782.app.ui.theme.OrnamentalDivider
import kotlinx.coroutines.launch

/**
 * Обучающий экран (onboarding): слайды по функциям.
 * Долгое нажатие в любом месте = пропуск. Кнопка «Пропустить» тоже есть.
 */
@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onFinished: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 5 })
    val scope = rememberCoroutineScope()

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
            .combinedClickable(
                onClick = { /* обычный тап — ничего */ },
                onLongClick = onFinished
            )
    ) {
        OrnamentalDivider(Modifier.fillMaxWidth())

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f).fillMaxWidth()
        ) { page ->
            when (page) {
                0 -> Slide(
                    icon = Icons.Filled.TravelExplore,
                    iconBg = MaterialTheme.colorScheme.primary,
                    title = stringResource(R.string.ob_welcome_title),
                    body = stringResource(R.string.ob_welcome_body)
                )
                1 -> Slide(
                    icon = Icons.Filled.Book,
                    iconBg = Color(0xFF2E7D32),
                    title = stringResource(R.string.ob_tourist_title),
                    body = stringResource(R.string.ob_tourist_body)
                )
                2 -> Slide(
                    icon = Icons.Filled.AdminPanelSettings,
                    iconBg = Color(0xFFC62828),
                    title = stringResource(R.string.ob_employee_title),
                    body = stringResource(R.string.ob_employee_body)
                )
                3 -> Slide(
                    icon = Icons.Filled.MusicNote,
                    iconBg = Color(0xFF1565C0),
                    title = stringResource(R.string.ob_music_title),
                    body = stringResource(R.string.ob_music_body)
                )
                4 -> Slide(
                    icon = Icons.Filled.Map,
                    iconBg = Color(0xFF6A1B9A),
                    title = stringResource(R.string.ob_topbar_title),
                    body = stringResource(R.string.ob_topbar_body)
                )
            }
        }

        // точки
        Row(
            Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(5) { index ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(if (pagerState.currentPage == index) 12.dp else 8.dp)
                        .background(
                            if (pagerState.currentPage == index)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.surfaceVariant,
                            CircleShape
                        )
                )
            }
        }

        // кнопки
        Row(
            Modifier.fillMaxWidth().padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                onClick = onFinished,
                modifier = Modifier.testTag("ob_skip")
            ) { Text(stringResource(R.string.ob_skip)) }

            if (pagerState.currentPage < 4) {
                Button(
                    onClick = {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    },
                    modifier = Modifier.testTag("ob_next")
                ) { Text(stringResource(R.string.ob_next)) }
            } else {
                Button(
                    onClick = onFinished,
                    modifier = Modifier.testTag("ob_start")
                ) { Text(stringResource(R.string.ob_start)) }
            }
        }

        // подпись про долгое нажатие
        Text(
            text = stringResource(R.string.ob_longpress_hint),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )
    }
}

@Composable
private fun Slide(
    icon: ImageVector,
    iconBg: Color,
    title: String,
    body: String
) {
    Column(
        Modifier.fillMaxSize().padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(iconBg, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
        }
        Spacer(Modifier.height(24.dp))
        Text(
            title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(12.dp))
        Text(
            body,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
