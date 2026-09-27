package com.erebuni782.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// P0: временная заглушка. P1 заменит на движок 3 скинов
// (Pre-Urartu / Urartu / Post-Urartu) — AGENTS.md §6.
private val LightColors = lightColorScheme()
private val DarkColors = darkColorScheme()

@Composable
fun Erebuni782Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}
