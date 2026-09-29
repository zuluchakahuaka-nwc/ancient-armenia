package com.erebuni782.app.ui

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import com.erebuni782.app.R
import com.erebuni782.app.ui.theme.OrnamentalDivider

/**
 * Экран выбора языка (самый первый при запуске).
 * Названия языков не локализуются — всегда на языке носителя.
 */
@Composable
fun LanguagePickerScreen(
    onLangSelected: (String) -> Unit,
    onSkip: () -> Unit
) {
    Column(
        Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            stringResource(R.string.lang_pick_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(12.dp))
        OrnamentalDivider(Modifier.fillMaxWidth())
        Spacer(Modifier.height(32.dp))

        // Հայերեն
        Button(
            onClick = {
                AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("hy"))
                onLangSelected("hy")
            },
            modifier = Modifier.fillMaxWidth().height(64.dp).testTag("lang_pick_hy")
        ) {
            Text("Հայերեն", style = MaterialTheme.typography.titleLarge)
        }

        Spacer(Modifier.height(16.dp))

        // Русский
        Button(
            onClick = {
                AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("ru"))
                onLangSelected("ru")
            },
            modifier = Modifier.fillMaxWidth().height(64.dp).testTag("lang_pick_ru")
        ) {
            Text("Русский", style = MaterialTheme.typography.titleLarge)
        }

        Spacer(Modifier.height(16.dp))

        // English
        Button(
            onClick = {
                AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("en"))
                onLangSelected("en")
            },
            modifier = Modifier.fillMaxWidth().height(64.dp).testTag("lang_pick_en")
        ) {
            Text("English", style = MaterialTheme.typography.titleLarge)
        }

        Spacer(Modifier.height(16.dp))

        // Português
        Button(
            onClick = {
                AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("pt"))
                onLangSelected("pt")
            },
            modifier = Modifier.fillMaxWidth().height(64.dp).testTag("lang_pick_pt")
        ) {
            Text("Português", style = MaterialTheme.typography.titleLarge)
        }
    }
}
