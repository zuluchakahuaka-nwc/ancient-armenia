package com.erebuni782.app.ui.library

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.erebuni782.app.R

/** Мини-плеер над нижней навигацией: ПОКАЗЫВАЕТСЯ ТОЛЬКО после взаимодействия юзера с плеером. */
@Composable
fun MiniPlayerBar(viewModel: LibraryViewModel) {
    val state by viewModel.playerState.collectAsState()
    val userInteracted by viewModel.userInteractedWithPlayer.collectAsState()
    if (!state.hasContent || !userInteracted) return

    Surface(tonalElevation = 4.dp) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = state.title.orEmpty(),
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1,
                modifier = Modifier.weight(1f).testTag("miniplayer_title")
            )
            IconButton(onClick = { viewModel.togglePlayPause() }) {
                if (state.isPlaying) {
                    Icon(Icons.Filled.Pause, contentDescription = stringResource(R.string.pause))
                } else {
                    Icon(Icons.Filled.PlayArrow, contentDescription = stringResource(R.string.play))
                }
            }
            IconButton(onClick = { viewModel.stopPlayer() }) {
                Icon(Icons.Filled.Close, contentDescription = stringResource(R.string.stop_playback))
            }
        }
    }
}

/**
 * Единственный activity-scoped LibraryViewModel: и MainShell (мини-плеер, автостарт),
 * и Library/AudioTab обязаны делить ОДИН экземпляр — иначе viewModel() внутри NavHost
 * скопится к NavBackStackEntry и userInteracted/плеер расщепятся (мини-плеер не вернётся
 * после OFF→ON станции).
 */
@Composable
fun rememberLibraryViewModel(): LibraryViewModel {
    val activity = androidx.compose.ui.platform.LocalContext.current as? androidx.activity.ComponentActivity
        ?: error("rememberLibraryViewModel requires an Activity context")
    return viewModel(viewModelStoreOwner = activity, factory = LibraryViewModel.factory())
}
