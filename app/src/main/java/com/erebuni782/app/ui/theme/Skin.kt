package com.erebuni782.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.erebuni782.app.R

/** Идентификаторы исторических скинов (AGENTS.md §6). */
enum class SkinId { PRE_URARTU, URARTU, POST_URARTU }

/** Стиль орнаментального декора, свой каждому скину. */
enum class OrnamentStyle { CHEVRON, CRENELLATION, MEANDER }

/**
 * Полная спецификация скина: палитры (светлая/тёмная), пара шрифтов,
 * формы, орнамент. Скин = замена всей дизайн-системы, не только цветов.
 */
data class SkinSpec(
    val id: SkinId,
    val light: androidx.compose.material3.ColorScheme,
    val dark: androidx.compose.material3.ColorScheme,
    val typography: Typography,
    val shapes: Shapes,
    val ornament: OrnamentStyle
)

/**
 * Доступ к текущему скину из любого места композиции.
 * Безопасный дефолт (URARTU) вместо error: подкомпозиции LazyLayout в
 * nav-saveState-лимбо (movableContent) могут читать local вне провайдера
 * (эмиссия Room-flow в отцепленной композиции) — падать там нельзя.
 * Реальная композиция всегда сидит под провайдером с актуальным скином.
 */
val LocalSkin = compositionLocalOf { skinSpec(SkinId.URARTU) }

object AppTheme {
    val skin: SkinSpec
        @Composable get() = LocalSkin.current
}

private val sansArmenian = FontFamily(
    Font(R.font.noto_sans_armenian_regular, FontWeight.Normal),
    Font(R.font.noto_sans_armenian_bold, FontWeight.Bold)
)

private val serifArmenian = FontFamily(
    Font(R.font.noto_serif_armenian_regular, FontWeight.Normal),
    Font(R.font.noto_serif_armenian_bold, FontWeight.Bold)
)

private fun typographyFor(display: FontFamily, body: FontFamily): Typography {
    val base = Typography()
    return base.copy(
        displayLarge = base.displayLarge.copy(fontFamily = display),
        displayMedium = base.displayMedium.copy(fontFamily = display),
        displaySmall = base.displaySmall.copy(fontFamily = display),
        headlineLarge = base.headlineLarge.copy(fontFamily = display),
        headlineMedium = base.headlineMedium.copy(fontFamily = display),
        headlineSmall = base.headlineSmall.copy(fontFamily = display),
        titleLarge = base.titleLarge.copy(fontFamily = display),
        titleMedium = base.titleMedium.copy(fontFamily = display),
        titleSmall = base.titleSmall.copy(fontFamily = display),
        bodyLarge = base.bodyLarge.copy(fontFamily = body),
        bodyMedium = base.bodyMedium.copy(fontFamily = body),
        bodySmall = base.bodySmall.copy(fontFamily = body),
        labelLarge = base.labelLarge.copy(fontFamily = body),
        labelMedium = base.labelMedium.copy(fontFamily = body),
        labelSmall = base.labelSmall.copy(fontFamily = body)
    )
}

// ── Pre-Urartu: ранняя железная эпоха, керамика, грубые фактуры ──
private val preUrartuLight = lightColorScheme(
    primary = Color(0xFF8A4B2A),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFEFD9C4),
    onPrimaryContainer = Color(0xFF351404),
    secondary = Color(0xFF6D5A48),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFD8C3AC),
    onSecondaryContainer = Color(0xFF251A0E),
    tertiary = Color(0xFF5C6235),
    background = Color(0xFFF8F1E4),
    onBackground = Color(0xFF211B12),
    surface = Color(0xFFF8F1E4),
    onSurface = Color(0xFF211B12),
    surfaceVariant = Color(0xFFEADFC9),
    onSurfaceVariant = Color(0xFF4D4536),
    outline = Color(0xFF7F7565)
)

private val preUrartuDark = darkColorScheme(
    primary = Color(0xFFE5B28E),
    onPrimary = Color(0xFF43290F),
    primaryContainer = Color(0xFF5C3D24),
    onPrimaryContainer = Color(0xFFF7DEC4),
    secondary = Color(0xFFBFA892),
    background = Color(0xFF191510),
    onBackground = Color(0xFFEAE1D3),
    surface = Color(0xFF191510),
    onSurface = Color(0xFFEAE1D3),
    surfaceVariant = Color(0xFF332C21),
    onSurfaceVariant = Color(0xFFC9BCA6),
    outline = Color(0xFF93866F)
)

// ── Urartu (флагман): базальт, глина, клинописные мотивы ──
private val urartuLight = lightColorScheme(
    primary = Color(0xFFB2592A),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFFDBCC),
    onPrimaryContainer = Color(0xFF3A0B00),
    secondary = Color(0xFF77574A),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFFDBCC),
    onSecondaryContainer = Color(0xFF2C150C),
    tertiary = Color(0xFF695F2E),
    background = Color(0xFFF3E7D3),
    onBackground = Color(0xFF221A14),
    surface = Color(0xFFF3E7D3),
    onSurface = Color(0xFF221A14),
    surfaceVariant = Color(0xFFE2D4BC),
    onSurfaceVariant = Color(0xFF4B4337),
    outline = Color(0xFF7D7365)
)

private val urartuDark = darkColorScheme(
    primary = Color(0xFFFFB68A),
    onPrimary = Color(0xFF551F00),
    primaryContainer = Color(0xFF78340F),
    onPrimaryContainer = Color(0xFFFFDBCC),
    secondary = Color(0xFFE7BEAD),
    background = Color(0xFF1A1410),
    onBackground = Color(0xFFEFE1D4),
    surface = Color(0xFF1A1410),
    onSurface = Color(0xFFEFE1D4),
    surfaceVariant = Color(0xFF332A22),
    onSurfaceVariant = Color(0xFFD2C3B1),
    outline = Color(0xFF9B8D7C)
)

// ── Post-Urartu: ахеменидско-эллинистический слой: мрамор, лазурит, золото ──
private val postUrartuLight = lightColorScheme(
    primary = Color(0xFF1F5F8B),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFCCE5FF),
    onPrimaryContainer = Color(0xFF001E31),
    secondary = Color(0xFF8A6E2F),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFFDFA6),
    onSecondaryContainer = Color(0xFF2A1E00),
    tertiary = Color(0xFF4C6358),
    background = Color(0xFFF6F8FA),
    onBackground = Color(0xFF171C1F),
    surface = Color(0xFFF6F8FA),
    onSurface = Color(0xFF171C1F),
    surfaceVariant = Color(0xFFDCE3E9),
    onSurfaceVariant = Color(0xFF41484D),
    outline = Color(0xFF71787E)
)

private val postUrartuDark = darkColorScheme(
    primary = Color(0xFF97CCF5),
    onPrimary = Color(0xFF003351),
    primaryContainer = Color(0xFF004B73),
    onPrimaryContainer = Color(0xFFCCE5FF),
    secondary = Color(0xFFD6C289),
    background = Color(0xFF0F1417),
    onBackground = Color(0xFFDFE3E7),
    surface = Color(0xFF0F1417),
    onSurface = Color(0xFFDFE3E7),
    surfaceVariant = Color(0xFF2A3136),
    onSurfaceVariant = Color(0xFFC0C7CD),
    outline = Color(0xFF8A9197)
)

private val skinSpecs: Map<SkinId, SkinSpec> = mapOf(
    SkinId.PRE_URARTU to SkinSpec(
        id = SkinId.PRE_URARTU,
        light = preUrartuLight,
        dark = preUrartuDark,
        typography = typographyFor(serifArmenian, serifArmenian),
        shapes = Shapes(
            extraSmall = RoundedCornerShape(2.dp),
            small = RoundedCornerShape(2.dp),
            medium = RoundedCornerShape(4.dp),
            large = RoundedCornerShape(4.dp),
            extraLarge = RoundedCornerShape(6.dp)
        ),
        ornament = OrnamentStyle.CHEVRON
    ),
    SkinId.URARTU to SkinSpec(
        id = SkinId.URARTU,
        light = urartuLight,
        dark = urartuDark,
        typography = typographyFor(sansArmenian, sansArmenian),
        shapes = Shapes(
            extraSmall = RoundedCornerShape(4.dp),
            small = RoundedCornerShape(6.dp),
            medium = RoundedCornerShape(10.dp),
            large = RoundedCornerShape(14.dp),
            extraLarge = RoundedCornerShape(20.dp)
        ),
        ornament = OrnamentStyle.CRENELLATION
    ),
    SkinId.POST_URARTU to SkinSpec(
        id = SkinId.POST_URARTU,
        light = postUrartuLight,
        dark = postUrartuDark,
        typography = typographyFor(serifArmenian, sansArmenian),
        shapes = Shapes(
            extraSmall = RoundedCornerShape(8.dp),
            small = RoundedCornerShape(12.dp),
            medium = RoundedCornerShape(16.dp),
            large = RoundedCornerShape(24.dp),
            extraLarge = RoundedCornerShape(32.dp)
        ),
        ornament = OrnamentStyle.MEANDER
    )
)

fun skinSpec(id: SkinId): SkinSpec = skinSpecs.getValue(id)
