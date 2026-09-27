package com.erebuni782.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

/**
 * Корневая тема приложения. Скин выбирается снаружи (DataStore → ViewModel);
 * тёмный режим следует системе.
 */
@Composable
fun Erebuni782Theme(
    skinId: SkinId = SkinId.URARTU,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val spec = skinSpec(skinId)
    CompositionLocalProvider(LocalSkin provides spec) {
        MaterialTheme(
            colorScheme = if (darkTheme) spec.dark else spec.light,
            typography = spec.typography,
            shapes = spec.shapes,
            content = content
        )
    }
}
