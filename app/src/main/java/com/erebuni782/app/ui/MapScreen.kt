package com.erebuni782.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.erebuni782.app.R
import com.erebuni782.app.ui.theme.OrnamentalDivider

/** P2: заглушка карты (офлайн-карты — позже; координаты крепостей). */
@Composable
fun MapScreen() {
    Column(
        Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(stringResource(R.string.map_title), style = MaterialTheme.typography.headlineMedium)
        OrnamentalDivider()
        Card {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(stringResource(R.string.map_erebuni_name), style = MaterialTheme.typography.titleMedium)
                Text(stringResource(R.string.map_coords_erebuni), style = MaterialTheme.typography.bodyMedium)
            }
        }
        Card {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(stringResource(R.string.map_teishebaini_name), style = MaterialTheme.typography.titleMedium)
                Text(stringResource(R.string.map_coords_teishebaini), style = MaterialTheme.typography.bodyMedium)
            }
        }
        Text(
            stringResource(R.string.map_placeholder),
            style = MaterialTheme.typography.bodySmall
        )
    }
}
