package com.erebuni782.app.ui.aerial

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.erebuni782.app.R
import com.erebuni782.app.aerial.detect.DetectLevel
import com.erebuni782.app.aerial.gen3d.Era

/** Экран сессии: фото → сшивка → тумблер ручная/авто → маркеры → 3D. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AerialSessionScreen(sessionId: String, onBack: () -> Unit) {
    val vm: AerialViewModel = viewModel(key = "aerial_$sessionId", factory = AerialViewModel.factory(sessionId))
    val state by vm.state.collectAsState()
    var showGeoDialog by rememberSaveable { mutableStateOf(false) }
    var show3dDialog by rememberSaveable { mutableStateOf(false) }
    var infoMessage by rememberSaveable { mutableStateOf("") }

    val picker = rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) { uris ->
        if (uris.isNotEmpty()) vm.importUris(uris)
    }

    LaunchedEffect(state.message) { state.message?.let { infoMessage = it } }

    LazyColumn(
        Modifier.fillMaxSize().testTag("aerial_scroll"),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(state.session?.title.orEmpty(), style = MaterialTheme.typography.headlineSmall)
                    val geo = state.session
                    Text(
                        when {
                            geo?.hasExifGeo == true -> stringResource(R.string.aerial_geo_exif)
                            geo?.geoLat != null -> stringResource(R.string.aerial_geo_manual, geo.geoLat!!, geo.geoLon!!)
                            else -> stringResource(R.string.aerial_geo_none)
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (state.session?.geoLat == null) {
                    TextButton(onClick = { showGeoDialog = true }, modifier = Modifier.testTag("geo_link")) {
                        Text(stringResource(R.string.aerial_geo_link))
                    }
                }
            }
        }

        // ── СТАТУС-СТРОКА: всегда-скомпонованный якорь для e2e (AGENTS.md §7.5) ──
        item {
            val stitched = if (state.session?.stitchedPath != null) 1 else 0
            val geo = if (state.session?.geoLat != null) 1 else 0
            val busy = if (state.busy) 1 else 0
            Text(
                text = stringResource(R.string.status_word) +
                    " markers=${state.markers.size} stitched=$stitched busy=$busy geo=$geo",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.testTag("aerial_status")
            )
        }

        // ── кадры ──
        item {
            Card {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(stringResource(R.string.aerial_photos_count, state.session?.photos?.size ?: 0), style = MaterialTheme.typography.titleSmall)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = { picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                            modifier = Modifier.testTag("import_photos")
                        ) { Text(stringResource(R.string.aerial_import)) }
                        OutlinedButton(
                            onClick = { vm.loadDemoPhotos() },
                            modifier = Modifier.testTag("load_demo_photos")
                        ) { Text(stringResource(R.string.aerial_demo)) }
                    }
                    Button(
                        onClick = { vm.stitch() },
                        enabled = (state.session?.photos?.size ?: 0) >= 2 && !state.busy,
                        modifier = Modifier.testTag("stitch_button")
                    ) { Text(stringResource(R.string.aerial_stitch)) }
                    if (state.busy) CircularProgressIndicator(Modifier.height(24.dp))
                }
            }
        }

        // ── основное фото + маркеры ──
        item {
            val base = state.session?.stitchedPath ?: state.session?.photos?.firstOrNull()
            Box(Modifier.fillMaxWidth().height(300.dp)) {
                base?.let { path ->
                    val bitmap = remember(path) { decodeForDisplay(path) }
                    bitmap?.let {
                        androidx.compose.foundation.Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
                MarkerOverlay(
                    markers = state.markers,
                    manualMode = state.mode == MarkMode.MANUAL,
                    onImageTap = vm::onImageTap
                )
            }
        }

        // ── тумблер ручная/авто + уровни (D5) ──
        item {
            Card {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                        SegmentedButton(
                            selected = state.mode == MarkMode.MANUAL,
                            onClick = { vm.setMode(MarkMode.MANUAL) },
                            shape = SegmentedButtonDefaults.itemShape(0, 2),
                            modifier = Modifier.testTag("mode_manual")
                        ) { Text(stringResource(R.string.mode_manual)) }
                        SegmentedButton(
                            selected = state.mode == MarkMode.AUTO,
                            onClick = { vm.setMode(MarkMode.AUTO) },
                            shape = SegmentedButtonDefaults.itemShape(1, 2),
                            modifier = Modifier.testTag("mode_auto")
                        ) { Text(stringResource(R.string.mode_auto)) }
                    }
                    if (state.mode == MarkMode.AUTO) {
                        Text(stringResource(R.string.detect_level_label), style = MaterialTheme.typography.titleSmall)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf(
                                DetectLevel.WEAK to R.string.level_weak,
                                DetectLevel.MEDIUM to R.string.level_medium,
                                DetectLevel.THOROUGH to R.string.level_thorough
                            ).forEach { (level, label) ->
                                FilterChip(
                                    selected = state.level == level,
                                    onClick = { vm.setLevel(level) },
                                    label = { Text(stringResource(label)) },
                                    modifier = Modifier.testTag("level_${level.name}")
                                )
                            }
                        }
                        Button(
                            onClick = { vm.detect() },
                            enabled = !state.busy,
                            modifier = Modifier.testTag("detect_button")
                        ) { Text(stringResource(R.string.detect_run)) }
                    } else {
                        Text(
                            stringResource(R.string.manual_hint),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // ── маркеры ──
        item {
            Text(stringResource(R.string.markers_count, state.markers.size), style = MaterialTheme.typography.titleSmall)
        }
        items(state.markers, key = { it.id }) { m ->
            ListItem(
                headlineContent = {
                    Text(
                        "#${m.number} " + if (m.manual) stringResource(R.string.source_manual) else stringResource(R.string.source_auto)
                    )
                },
                supportingContent = {
                    Text(
                        if (m.confidence != null) "x=${"%.2f".format(m.x)} y=${"%.2f".format(m.y)} conf=${"%.1f".format(m.confidence)}" 
                        else "x=${"%.2f".format(m.x)} y=${"%.2f".format(m.y)}"
                    )
                },
                trailingContent = {
                    TextButton(onClick = { vm.deleteMarker(m.id) }) { Text(stringResource(R.string.remove_track)) }
                }
            )
        }

        item {
            HorizontalDivider()
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
                OutlinedButton(
                    onClick = { vm.exportGeoJson { infoMessage = it } },
                    modifier = Modifier.testTag("export_geojson")
                ) { Text(stringResource(R.string.export_geojson)) }
                OutlinedButton(
                    onClick = { show3dDialog = true },
                    modifier = Modifier.testTag("open_3d")
                ) { Text(stringResource(R.string.reconstruct_3d)) }
            }
            if (infoMessage.isNotBlank()) {
                Text(infoMessage, style = MaterialTheme.typography.bodySmall)
            }
            TextButton(onClick = onBack) { Text(stringResource(R.string.back)) }
        }
    }

    if (showGeoDialog) {
        GeoDialog(
            onDismiss = { showGeoDialog = false },
            onSave = { lat, lon ->
                vm.setManualGeo(lat, lon)
                showGeoDialog = false
            }
        )
    }
    if (show3dDialog) {
        Gen3dDialog(
            onDismiss = { show3dDialog = false },
            onGenerate = { era, year, place ->
                vm.generate3d(era, year, place) { infoMessage = it }
                show3dDialog = false
            }
        )
    }
}

@Composable
private fun GeoDialog(onDismiss: () -> Unit, onSave: (Double, Double) -> Unit) {
    var lat by rememberSaveable { mutableStateOf("") }
    var lon by rememberSaveable { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.aerial_geo_link)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(stringResource(R.string.aerial_geo_hint))
                OutlinedTextField(lat, { lat = it }, label = { Text("Latitude") }, singleLine = true, modifier = Modifier.testTag("geo_lat"))
                OutlinedTextField(lon, { lon = it }, label = { Text("Longitude") }, singleLine = true, modifier = Modifier.testTag("geo_lon"))
            }
        },
        confirmButton = {
            TextButton(
                onClick = { lat.toDoubleOrNull()?.let { la -> lon.toDoubleOrNull()?.let { lo -> onSave(la, lo) } } },
                modifier = Modifier.testTag("geo_save")
            ) { Text(stringResource(R.string.action_save)) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.back)) } }
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Gen3dDialog(onDismiss: () -> Unit, onGenerate: (Era, Int, String) -> Unit) {
    var era by rememberSaveable { mutableStateOf(Era.URARTU.name) }
    var year by rememberSaveable { mutableStateOf("-782") }
    var place by rememberSaveable { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.reconstruct_3d)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(stringResource(R.string.reconstruct_hint))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Era.entries.forEach { e ->
                        FilterChip(
                            selected = era == e.name,
                            onClick = { era = e.name },
                            label = { Text(eraLabel(e)) },
                            modifier = Modifier.testTag("era_${e.name}")
                        )
                    }
                }
                OutlinedTextField(year, { year = it }, label = { Text(stringResource(R.string.field_year)) }, singleLine = true)
                OutlinedTextField(place, { place = it }, label = { Text(stringResource(R.string.field_place)) }, singleLine = true)
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onGenerate(Era.valueOf(era), year.toIntOrNull() ?: -782, place) },
                modifier = Modifier.testTag("generate_3d")
            ) { Text(stringResource(R.string.generate_3d)) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.back)) } }
    )
}

@Composable
private fun eraLabel(era: Era): String = when (era) {
    Era.URARTU -> stringResource(R.string.era_urartu)
    Era.ACHAEMENID -> stringResource(R.string.era_achaemenid)
    Era.HELLENISTIC -> stringResource(R.string.era_hellenistic)
}

private fun decodeForDisplay(path: String): android.graphics.Bitmap? {
    val opts = android.graphics.BitmapFactory.Options().apply { inSampleSize = 2 }
    return android.graphics.BitmapFactory.decodeFile(path, opts)
}
