package com.erebuni782.app.ui.settings

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import com.erebuni782.app.MainViewModel
import com.erebuni782.app.R
import com.erebuni782.app.ui.theme.OrnamentalDivider
import com.erebuni782.app.ui.theme.SkinId

private val skinOptions = listOf(
    SkinId.PRE_URARTU to R.string.skin_pre_urartu,
    SkinId.URARTU to R.string.skin_urartu,
    SkinId.POST_URARTU to R.string.skin_post_urartu
)

private val languageOptions = listOf(
    "hy" to R.string.lang_hy,
    "ru" to R.string.lang_ru,
    "en" to R.string.lang_en
)

/** Настройки: язык, скин, о приложении. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(viewModel: MainViewModel) {
    val skin by viewModel.skin.collectAsState()
    val currentLang = LocalConfiguration.current.locales[0]?.language ?: "en"

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(stringResource(R.string.settings_title), style = MaterialTheme.typography.headlineMedium)
            OrnamentalDivider(Modifier.padding(top = 8.dp))
        }
        item {
            Text(stringResource(R.string.settings_language), style = MaterialTheme.typography.titleMedium)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                languageOptions.forEach { (tag, labelRes) ->
                    FilterChip(
                        selected = currentLang == tag,
                        onClick = {
                            AppCompatDelegate.setApplicationLocales(
                                LocaleListCompat.forLanguageTags(tag)
                            )
                        },
                        label = { Text(stringResource(labelRes)) }
                    )
                }
            }
        }
        item {
            Text(stringResource(R.string.settings_skin), style = MaterialTheme.typography.titleMedium)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                skinOptions.forEach { (id, labelRes) ->
                    FilterChip(
                        selected = skin == id,
                        onClick = { viewModel.setSkin(id) },
                        label = { Text(stringResource(labelRes)) }
                    )
                }
            }
        }
        item {
            Card {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(stringResource(R.string.about_title), style = MaterialTheme.typography.titleMedium)
                    Text(stringResource(R.string.about_body), style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
