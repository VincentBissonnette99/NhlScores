package com.vincent.nhlscores.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = NhlRedDark,
    onPrimary = NhlWhite,
    primaryContainer = NhlBlueDark,
    onPrimaryContainer = NhlWhite,
    secondary = NhlBlueDark,
    onSecondary = NhlWhite,
    secondaryContainer = NhlBlackDark,
    onSecondaryContainer = NhlWhiteDark,
    tertiary = NhlRedDark,
    onTertiary = NhlWhite,
    error = NhlRedDark,
    onError = NhlWhite,
    errorContainer = NhlRedDark.copy(alpha = 0.1f),
    onErrorContainer = NhlRedDark,
    background = NhlBlack,
    onBackground = NhlWhiteDark,
    surface = NhlBlackDark,
    onSurface = NhlWhiteDark,
    surfaceVariant = NhlBlackDark,
    onSurfaceVariant = NhlWhiteDark.copy(alpha = 0.7f),
    outline = NhlGray.copy(alpha = 0.5f)
)

private val LightColorScheme = lightColorScheme(
    primary = NhlRed,
    onPrimary = NhlWhite,
    primaryContainer = NhlBlue,
    onPrimaryContainer = NhlWhite,
    secondary = NhlBlue,
    onSecondary = NhlWhite,
    secondaryContainer = NhlLightGray,
    onSecondaryContainer = NhlBlack,
    tertiary = NhlRed,
    onTertiary = NhlWhite,
    error = NhlRed,
    onError = NhlWhite,
    errorContainer = NhlRed.copy(alpha = 0.1f),
    onErrorContainer = NhlRed,
    background = NhlWhite,
    onBackground = NhlBlack,
    surface = NhlWhite,
    onSurface = NhlBlack,
    surfaceVariant = NhlLightGray,
    onSurfaceVariant = NhlGray,
    outline = NhlGray.copy(alpha = 0.3f)
)

@Composable
fun NhlScoresTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}