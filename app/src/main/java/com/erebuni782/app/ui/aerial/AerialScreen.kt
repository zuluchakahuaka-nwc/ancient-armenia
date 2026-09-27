package com.erebuni782.app.ui.aerial

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import com.erebuni782.app.AppGraph
import com.erebuni782.app.R
import com.erebuni782.app.data.AerialRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AerialListViewModel : ViewModel() {
    val sessions: StateFlow<List<com.erebuni782.app.data.db.AerialSessionEntity>> =
        AppGraph.aerial.sessions()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun createSession(title: String, onCreated: (String) -> Unit) {
        viewModelScope.launch {
            val s = AppGraph.aerial.createSession(title)
            onCreated(s.id)
        }
    }
}

/** Список сессий аэрофотосъёмки (вход из режима сотрудника). */
@Composable
fun AerialScreen(onOpenSession: (String) -> Unit) {
    val vm: AerialListViewModel = viewModel()
    val sessions by vm.sessions.collectAsState()

    Box(Modifier.fillMaxSize()) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Text(stringResource(R.string.aerial_title), style = MaterialTheme.typography.headlineSmall)
                Text(
                    stringResource(R.string.aerial_subtitle),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (sessions.isEmpty()) {
                item { Text(stringResource(R.string.aerial_empty), style = MaterialTheme.typography.bodyMedium) }
            }
            items(sessions, key = { it.id }) { s ->
                Card(Modifier.fillMaxWidth().clickable { onOpenSession(s.id) }) {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(s.title, style = MaterialTheme.typography.titleMedium)
                        Text(
                            SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(Date(s.createdAt)) +
                                " · " + stringResource(R.string.aerial_photos_count, s.photoPaths.lines().count { it.isNotBlank() }),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
        ExtendedFloatingActionButton(
            onClick = { vm.createSession("Session ${SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())}") { onOpenSession(it) } },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp).testTag("new_aerial_session")
        ) {
            Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.aerial_new_session))
            Text(stringResource(R.string.aerial_new_session))
        }
    }
}
