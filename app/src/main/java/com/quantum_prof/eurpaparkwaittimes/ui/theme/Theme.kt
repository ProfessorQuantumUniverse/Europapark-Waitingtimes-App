package com.quantum_prof.eurpaparkwaittimes.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Enhanced Color Schemes with Material 3
private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryVariant,
    onPrimaryContainer = OnPrimary,

    secondary = Secondary,
    onSecondary = OnSecondary,
    secondaryContainer = SecondaryVariant,
    onSecondaryContainer = OnSecondary,

    tertiary = Tertiary,
    onTertiary = OnPrimary,
    tertiaryContainer = Color(0xFF7C2D92),
    onTertiaryContainer = Color(0xFFFFD6FF),

    background = Background,
    onBackground = OnBackground,

    surface = Surface,
    onSurface = OnSurface,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = OnSurfaceVariant,
    surfaceTint = Primary,

    inverseSurface = OnSurface,
    inverseOnSurface = Surface,
    inversePrimary = Color(0xFFDDD6FE),

    outline = Color(0xFF64748B),
    outlineVariant = Color(0xFF334155),

    error = Color(0xFFEF4444),
    onError = OnPrimary,
    errorContainer = Color(0xFF7F1D1D),
    onErrorContainer = Color(0xFFFECDD3),

    scrim = Color(0x80000000)
)

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = Color(0xFFE4D9FF),
    onPrimaryContainer = Color(0xFF3B0764),

    secondary = Secondary,
    onSecondary = OnSecondary,
    secondaryContainer = Color(0xFFCFFAFE),
    onSecondaryContainer = Color(0xFF164E63),

    tertiary = Tertiary,
    onTertiary = OnPrimary,
    tertiaryContainer = Color(0xFFFFE1F1),
    onTertiaryContainer = Color(0xFF831843),

    background = Color(0xFFF8FAFC),
    onBackground = Color(0xFF0F172A),

    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1E293B),
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF475569),
    surfaceTint = Primary,

    inverseSurface = Color(0xFF1E293B),
    inverseOnSurface = Color(0xFFF1F5F9),
    inversePrimary = Color(0xFFDDD6FE),

    outline = Color(0xFF64748B),
    outlineVariant = Color(0xFFCBD5E1),

    error = Color(0xFFDC2626),
    onError = OnPrimary,
    errorContainer = Color(0xFFFECDD3),
    onErrorContainer = Color(0xFF7F1D1D),

    scrim = Color(0x80000000)
)

@Composable
fun EuropaparkWaitTimesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Default to false to enforce our custom design
    performanceModeState: PerformanceModeState = rememberPerformanceModeState(),
    content: @Composable () -> Unit
) {
    // 🔋 Dimmed colors for battery save mode
    val dimFactor = if (performanceModeState.isEnabled) 0.65f else 1f

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> if (performanceModeState.isEnabled) {
            // Gedämpftes Dark Color Scheme für Battery Save
            DarkColorScheme.copy(
                primary = DarkColorScheme.primary.copy(
                    red = DarkColorScheme.primary.red * dimFactor,
                    green = DarkColorScheme.primary.green * dimFactor,
                    blue = DarkColorScheme.primary.blue * dimFactor
                ),
                secondary = DarkColorScheme.secondary.copy(
                    red = DarkColorScheme.secondary.red * dimFactor,
                    green = DarkColorScheme.secondary.green * dimFactor,
                    blue = DarkColorScheme.secondary.blue * dimFactor
                ),
                tertiary = DarkColorScheme.tertiary.copy(
                    red = DarkColorScheme.tertiary.red * dimFactor,
                    green = DarkColorScheme.tertiary.green * dimFactor,
                    blue = DarkColorScheme.tertiary.blue * dimFactor
                ),
                surface = Color(0xFF0A0A0A), // Noch dunkler für echte OLED-Effizienz
                background = Color(0xFF050505),
                surfaceVariant = Color(0xFF1A1A1A)
            )
        } else DarkColorScheme
        else -> if (performanceModeState.isEnabled) {
            // Gedämpftes Light Color Scheme für Battery Save
            LightColorScheme.copy(
                primary = LightColorScheme.primary.copy(
                    red = LightColorScheme.primary.red * dimFactor,
                    green = LightColorScheme.primary.green * dimFactor,
                    blue = LightColorScheme.primary.blue * dimFactor
                ),
                secondary = LightColorScheme.secondary.copy(
                    red = LightColorScheme.secondary.red * dimFactor,
                    green = LightColorScheme.secondary.green * dimFactor,
                    blue = LightColorScheme.secondary.blue * dimFactor
                ),
                tertiary = LightColorScheme.tertiary.copy(
                    red = LightColorScheme.tertiary.red * dimFactor,
                    green = LightColorScheme.tertiary.green * dimFactor,
                    blue = LightColorScheme.tertiary.blue * dimFactor
                )
            )
        } else LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb() // Transparent status bar
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    // 🔋 Provide the PerformanceModeState through CompositionLocal
    CompositionLocalProvider(
        LocalPerformanceMode provides performanceModeState
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}