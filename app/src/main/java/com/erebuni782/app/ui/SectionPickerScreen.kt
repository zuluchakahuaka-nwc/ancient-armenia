package com.erebuni782.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.erebuni782.app.R
import com.erebuni782.app.data.Sections
import com.erebuni782.app.ui.theme.OrnamentalDivider

/**
 * Первый экран после онбординга: выбор раздела — «Урарту-Армения» /
 * «Древняя Армения». Режим сотрудника выбирается НЕ здесь — вход через
 * вкладку «Сотрудник» (PIN-гейт) в нижней навигации.
 */
@Composable
fun SectionPickerScreen(
    onPick: (String) -> Unit
) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = stringResource(R.string.section_picker_subtitle),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(16.dp))
        OrnamentalDivider(Modifier.fillMaxWidth())
        Spacer(Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.section_picker_title),
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(Modifier.height(20.dp))

        SectionCard(
            tag = "section_urartu",
            icon = Icons.Filled.TravelExplore,
            labelRes = R.string.section_urartu_label,
            descRes = R.string.section_urartu_desc,
            container = MaterialTheme.colorScheme.primaryContainer,
            tint = MaterialTheme.colorScheme.primary,
            onClick = { onPick(Sections.URARTU) }
        )

        Spacer(Modifier.height(16.dp))

        SectionCard(
            tag = "section_ancient",
            icon = Icons.Filled.AccountBalance,
            labelRes = R.string.section_ancient_label,
            descRes = R.string.section_ancient_desc,
            container = MaterialTheme.colorScheme.secondaryContainer,
            tint = MaterialTheme.colorScheme.secondary,
            onClick = { onPick(Sections.ANCIENT) }
        )
    }
}

@Composable
private fun SectionCard(
    tag: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    labelRes: Int,
    descRes: Int,
    container: androidx.compose.ui.graphics.Color,
    tint: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().testTag(tag),
        colors = CardDefaults.cardColors(containerColor = container),
        onClick = onClick
    ) {
        Row(
            Modifier.fillMaxWidth().padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.height(48.dp),
                tint = tint
            )
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    stringResource(labelRes),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    stringResource(descRes),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
