package com.erebuni782.app.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.erebuni782.app.AppGraph
import com.erebuni782.app.R
import com.erebuni782.app.data.Sections
import com.erebuni782.app.ui.theme.OrnamentalDivider
import android.graphics.BitmapFactory

/** Карта: иллюстрация + GPS + как дойти / как доехать (карточки текущего раздела). */
@Composable
fun MapScreen() {
    val context = LocalContext.current
    val section by AppGraph.settings.section.collectAsState(initial = Sections.URARTU)

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(stringResource(R.string.map_title), style = MaterialTheme.typography.headlineMedium)
        OrnamentalDivider()

        if (section == Sections.ANCIENT) {
            // ── Древняя Армения: Гарни / Гегард / Эчмиадзин ──
            MapCard(
                title = stringResource(R.string.map_garni_name),
                coords = stringResource(R.string.map_coords_garni),
                walkText = stringResource(R.string.map_garni_walk),
                driveText = stringResource(R.string.map_garni_drive),
                lat = 40.1125, lon = 44.7169
            )
            MapCard(
                title = stringResource(R.string.map_geghard_name),
                coords = stringResource(R.string.map_coords_geghard),
                walkText = stringResource(R.string.map_geghard_walk),
                driveText = stringResource(R.string.map_geghard_drive),
                lat = 40.1407, lon = 44.7975
            )
            MapCard(
                title = stringResource(R.string.map_etchmiadzin_name),
                coords = stringResource(R.string.map_coords_etchmiadzin),
                walkText = stringResource(R.string.map_etchmiadzin_walk),
                driveText = stringResource(R.string.map_etchmiadzin_drive),
                lat = 40.1625, lon = 44.2919
            )
        } else {
            // ── карта-картинка (только урартский раздел: обе крепости у Еревана) ──
            val mapBitmap = remember {
                runCatching {
                    BitmapFactory.decodeStream(
                        AppGraph.appContext.assets.open("wiki_images/map_fortresses.png")
                    )
                }.getOrNull()
            }
            mapBitmap?.let {
                Card(Modifier.fillMaxWidth()) {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = stringResource(R.string.map_image_desc),
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // ── ЭРЕБУНИ (Арин-Берд) ──
            MapCard(
                title = stringResource(R.string.map_erebuni_name),
                coords = stringResource(R.string.map_coords_erebuni),
                walkText = stringResource(R.string.map_erebuni_walk),
                driveText = stringResource(R.string.map_erebuni_drive),
                lat = 40.1776, lon = 44.5164
            )

            // ── ТЕЙШЕБАИНИ (Кармир-Блур) ──
            MapCard(
                title = stringResource(R.string.map_teishebaini_name),
                coords = stringResource(R.string.map_coords_teishebaini),
                walkText = stringResource(R.string.map_teishebaini_walk),
                driveText = stringResource(R.string.map_teishebaini_drive),
                lat = 40.1872, lon = 44.4642
            )
        }

        Text(
            stringResource(R.string.map_disclaimer),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun MapCard(
    title: String,
    coords: String,
    walkText: String,
    driveText: String,
    lat: Double,
    lon: Double
) {
    val context = LocalContext.current
    Card(Modifier.fillMaxWidth().testTag("map_card")) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(coords, style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary)

            // «Как дойти» (пешеходный)
            Text("🚶 " + stringResource(R.string.map_walk_title),
                style = MaterialTheme.typography.titleSmall)
            Text(walkText, style = MaterialTheme.typography.bodySmall)

            // «Как доехать» (авто)
            Text("🚗 " + stringResource(R.string.map_drive_title),
                style = MaterialTheme.typography.titleSmall)
            Text(driveText, style = MaterialTheme.typography.bodySmall)

            // кнопки — открыть во внешней карте
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("geo:$lat,$lon?q=$lat,$lon($title)")))
                    },
                    modifier = Modifier.testTag("open_maps")
                ) { Text("🗺 " + stringResource(R.string.map_open_maps)) }
                OutlinedButton(
                    onClick = {
                        context.startActivity(Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://maps.google.com/dir/?api=1&destination=$lat,$lon&travelmode=driving")))
                    }
                ) { Text("🚗 " + stringResource(R.string.map_navigate)) }
            }
        }
    }
}
