package com.erebuni782.app.ui.reader

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.erebuni782.app.R

/** Читалка книги: глава + контролы (шрифт/ночь/закладка/навигация). */
@Composable
fun ReaderScreen(bookId: String, onBack: () -> Unit) {
    val vm: ReaderViewModel = viewModel(key = "reader_$bookId", factory = ReaderViewModel.factory(bookId))
    val state by vm.state.collectAsState()

    val pageBg = if (state.nightMode) Color(0xFF16130F) else MaterialTheme.colorScheme.surface
    val pageFg = if (state.nightMode) Color(0xFFEDE4D3) else MaterialTheme.colorScheme.onSurface

    Column(Modifier.fillMaxSize().background(pageBg)) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.testTag("reader_back")) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
            }
            Column(Modifier.weight(1f)) {
                Text(
                    text = state.book?.book?.title.orEmpty(),
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1
                )
                Text(
                    text = stringResource(
                        R.string.reader_chapter_of,
                        state.chapterIndex + 1,
                        state.chapterCount
                    ),
                    style = MaterialTheme.typography.labelSmall
                )
            }
            IconButton(onClick = { vm.toggleBookmark() }, modifier = Modifier.testTag("reader_bookmark")) {
                if (state.isCurrentBookmarked) {
                    Icon(Icons.Filled.Favorite, tint = MaterialTheme.colorScheme.primary, contentDescription = stringResource(R.string.reader_bookmarked))
                } else {
                    Icon(Icons.Filled.FavoriteBorder, contentDescription = stringResource(R.string.reader_bookmark))
                }
            }
        }

        Card(
            modifier = Modifier.weight(1f).fillMaxWidth().padding(12.dp),
            colors = CardDefaults.cardColors(containerColor = pageBg, contentColor = pageFg),
            shape = RoundedCornerShape(6.dp)
        ) {
            Column(
                Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = state.currentChapter?.title.orEmpty(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    fontSize = (22 * state.fontScale).sp
                )
                Text(
                    text = state.currentChapter?.body.orEmpty(),
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = (16 * state.fontScale).sp,
                    lineHeight = (24 * state.fontScale).sp
                )
            }
        }

        Row(
            Modifier.fillMaxWidth().padding(horizontal = 6.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { vm.previous() }, modifier = Modifier.testTag("reader_prev")) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.previous_chapter))
            }
            TextButton(onClick = { vm.setFontScale(state.fontScale - 0.2f) }, modifier = Modifier.testTag("reader_font_down")) {
                Text("A-")
            }
            TextButton(onClick = { vm.setNightMode(!state.nightMode) }, modifier = Modifier.testTag("reader_night")) {
                Text(stringResource(R.string.night_mode))
            }
            TextButton(onClick = { vm.setFontScale(state.fontScale + 0.2f) }, modifier = Modifier.testTag("reader_font_up")) {
                Text("A+")
            }
            IconButton(onClick = { vm.next() }, modifier = Modifier.testTag("reader_next")) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = stringResource(R.string.next_chapter))
            }
        }
    }
}
