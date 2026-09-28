package com.erebuni782.app.ui.library

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.erebuni782.app.R
import com.erebuni782.app.data.BookUi
import com.erebuni782.app.data.GuideTrackUi
import com.erebuni782.app.data.UserTrackUi
import com.erebuni782.app.ui.theme.OrnamentalDivider
import java.util.Locale

/** Библиотека: книги + аудио (D9/D10) + книги владельца (D1, PDF/DJVU). */
@Composable
fun LibraryScreen(onOpenBook: (String) -> Unit, onOpenPdf: (String) -> Unit = {}) {
    var tab by rememberSaveable { mutableIntStateOf(0) }

    Column(Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = tab) {
            Tab(
                selected = tab == 0,
                onClick = { tab = 0 },
                text = { Text(stringResource(R.string.tab_books)) },
                modifier = Modifier.testTag("tab_books")
            )
            Tab(
                selected = tab == 1,
                onClick = { tab = 1 },
                text = { Text(stringResource(R.string.tab_audio)) },
                modifier = Modifier.testTag("tab_audio")
            )
        }
        when (tab) {
            0 -> BooksTab(onOpenBook, onOpenPdf)
            1 -> AudioTab(viewModel = rememberLibraryViewModel())
        }
    }
}

@Composable
private fun BooksTab(onOpenBook: (String) -> Unit, onOpenPdf: (String) -> Unit) {
    val books by produceState(initialValue = emptyList<BookUi>()) {
        value = com.erebuni782.app.AppGraph.books.books(Locale.getDefault().toLanguageTag())
    }
    val context = androidx.compose.ui.platform.LocalContext.current
    val ownerBooks = remember { com.erebuni782.app.data.book.PdfBookCatalog.load(context) }

    LazyColumn(
        modifier = Modifier.testTag("books_scroll"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(books, key = { it.id }) { book ->
            Card(modifier = Modifier.fillMaxWidth().clickable { onOpenBook(book.id) }) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(book.title, style = MaterialTheme.typography.titleMedium)
                    Text(book.author, style = MaterialTheme.typography.bodySmall)
                    Text(
                        text = book.licenseNote,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item {
            Text(
                stringResource(R.string.owner_library_title),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 8.dp).testTag("owner_books_header")
            )
            Text(
                stringResource(R.string.owner_library_license),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        items(ownerBooks, key = { it.id }) { ob ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = ob.isReadable) { onOpenPdf(ob.id) }
                    .testTag("owner_book_${ob.id}")
            ) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(ob.title, style = MaterialTheme.typography.titleSmall)
                    Text(
                        "${ob.author} · ${ob.year} · ${ob.format.uppercase()}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    if (!ob.isReadable) {
                        Text(
                            stringResource(R.string.book_djvu_hint),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AudioTab(viewModel: LibraryViewModel) {
    val context = LocalContext.current
    val localeTag = LocalConfiguration.current.locales[0]?.toLanguageTag() ?: "en"
    val stationEnabled by viewModel.stationEnabled.collectAsState()

    val userTracks by remember(localeTag) {
        viewModel.userTracks(localeTag)
    }.collectAsState(initial = emptyList())

    val guides by remember(localeTag) {
        viewModel.guides(localeTag)
    }.collectAsState(initial = emptyList())

    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
        uris.forEach { uri ->
            runCatching {
                context.contentResolver.takePersistableUriPermission(
                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
            viewModel.addTrack(uri.toString(), queryDisplayName(context, uri))
        }
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(stringResource(R.string.audio_section_title), style = MaterialTheme.typography.headlineSmall)
            OrnamentalDivider(Modifier.padding(top = 6.dp))
        }

        // ── Urartu.fm (D10) ──
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    Modifier.padding(14.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text("Urartu.fm", style = MaterialTheme.typography.titleMedium)
                        Text(
                            stringResource(R.string.station_hint),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Switch(
                        checked = stationEnabled,
                        onCheckedChange = { enabled ->
                            viewModel.setStation(enabled) { res -> context.getString(res) }
                        },
                        modifier = Modifier.testTag("station_toggle")
                    )
                }
            }
        }

        // ── Моя музыка (D9) ──
        item {
            Text(stringResource(R.string.my_music), style = MaterialTheme.typography.titleMedium)
            Button(
                onClick = { picker.launch(arrayOf("audio/*")) },
                modifier = Modifier.fillMaxWidth().testTag("import_music")
            ) {
                Text(stringResource(R.string.import_music))
            }
        }
        if (userTracks.isEmpty()) {
            item {
                Text(
                    stringResource(R.string.library_empty),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        items(userTracks, key = { it.id }) { track ->
            UserTrackRow(track, viewModel)
        }

        // ── Аудиогиды ──
        item {
            Text(stringResource(R.string.audio_guides), style = MaterialTheme.typography.titleMedium)
        }
        items(guides, key = { it.id }) { guide ->
            ListItem(
                headlineContent = { Text(guide.title) },
                supportingContent = { Text(formatDuration(guide.durationSec)) },
                trailingContent = {
                    if (!guide.available) {
                        AssistChip(onClick = {}, label = { Text(stringResource(R.string.soon)) })
                    } else {
                        IconButton(onClick = { }) {
                            Icon(Icons.Filled.PlayArrow, contentDescription = stringResource(R.string.play))
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun UserTrackRow(track: UserTrackUi, viewModel: LibraryViewModel) {
    ListItem(
        headlineContent = { Text(track.title, maxLines = 1) },
        leadingContent = {
            IconButton(onClick = { viewModel.playTrack(track.uri, track.title) }) {
                Icon(Icons.Filled.PlayArrow, contentDescription = stringResource(R.string.play))
            }
        },
        trailingContent = {
            IconButton(onClick = { viewModel.removeTrack(track.id) }) {
                Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.remove_track))
            }
        }
    )
}

private fun formatDuration(sec: Int): String {
    val m = sec / 60
    val s = sec % 60
    return "%d:%02d".format(m, s)
}

private fun queryDisplayName(context: android.content.Context, uri: android.net.Uri): String {
    runCatching {
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val idx = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
            if (idx >= 0 && cursor.moveToFirst()) {
                return cursor.getString(idx) ?: uri.lastPathSegment.orEmpty()
            }
        }
    }
    return uri.lastPathSegment.orEmpty()
}
