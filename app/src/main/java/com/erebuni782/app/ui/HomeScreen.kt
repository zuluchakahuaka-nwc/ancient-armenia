package com.erebuni782.app.ui

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import com.erebuni782.app.MainViewModel
import com.erebuni782.app.R
import com.erebuni782.app.ui.theme.OrnamentalDivider
import com.erebuni782.app.ui.theme.SkinId

private data class SkinOption(val id: SkinId, val labelRes: Int)
private data class LangOption(val tag: String, val labelRes: Int)

private val skinOptions = listOf(
    SkinOption(SkinId.PRE_URARTU, R.string.skin_pre_urartu),
    SkinOption(SkinId.URARTU, R.string.skin_urartu),
    SkinOption(SkinId.POST_URARTU, R.string.skin_post_urartu)
)

private val languageOptions = listOf(
    LangOption("hy", R.string.lang_hy),
    LangOption("ru", R.string.lang_ru),
    LangOption("en", R.string.lang_en)
)

/** P1-экран приёмки: скин и язык переключаются на лету + превью дизайн-системы. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onAbout: () -> Unit
) {
    val skin by viewModel.skin.collectAsState()
    val currentLang = LocalConfiguration.current.locales[0]?.language ?: "en"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(R.string.screen_home_title),
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = stringResource(R.string.settings_skin),
            style = MaterialTheme.typography.titleMedium
        )
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            skinOptions.forEach { option ->
                FilterChip(
                    selected = skin == option.id,
                    onClick = { viewModel.setSkin(option.id) },
                    label = { Text(stringResource(option.labelRes)) }
                )
            }
        }

        Text(
            text = stringResource(R.string.settings_language),
            style = MaterialTheme.typography.titleMedium
        )
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            languageOptions.forEach { option ->
                FilterChip(
                    selected = currentLang == option.tag,
                    onClick = {
                        AppCompatDelegate.setApplicationLocales(
                            LocaleListCompat.forLanguageTags(option.tag)
                        )
                    },
                    label = { Text(stringResource(option.labelRes)) }
                )
            }
        }

        PreviewCard()

        Button(
            onClick = onAbout,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(stringResource(R.string.action_about))
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

/** Превью дизайн-системы текущего скина: типографика, орнамент, формы. */
@Composable
fun PreviewCard() {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = stringResource(R.string.sample_card_title),
                style = MaterialTheme.typography.titleLarge
            )
            OrnamentalDivider()
            Text(
                text = stringResource(R.string.sample_body),
                style = MaterialTheme.typography.bodyMedium
            )
            TextButton(onClick = { /* превью */ }) {
                Text(stringResource(R.string.sample_action))
            }
        }
    }
}
