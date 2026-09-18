package com.hafalquran.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = EmeraldPrimary,
    onPrimary = TextWhite,
    primaryContainer = EmeraldContainer,
    onPrimaryContainer = GoldLight,
    secondary = GoldAccent,
    onSecondary = QuranBgDark,
    background = QuranBgDark,
    onBackground = TextWhite,
    surface = QuranSurfaceDark,
    onSurface = TextWhite,
    surfaceVariant = QuranCardDark,
    onSurfaceVariant = TextMuted,
    outline = QuranCardBorder
)

@Composable
fun HafalQuranTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
