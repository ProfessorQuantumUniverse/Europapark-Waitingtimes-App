package com.quantum_prof.eurpaparkwaittimes.ui.theme

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.edit

/**
 * 🔋 Performance/Battery Save Mode
 * Reduces animations and dims colors for better battery life and performance.
 * Persists state to SharedPreferences.
 */

private const val PREFS_NAME = "performance_mode_prefs"
private const val KEY_PERFORMANCE_MODE_ENABLED = "performance_mode_enabled"

/**
 * CompositionLocal für den globalen Performance-Modus
 */
val LocalPerformanceMode = compositionLocalOf { PerformanceModeState() }

/**
 * State-Holder für den Performance-Modus mit Persistierung
 */
@Stable
class PerformanceModeState(
    initialEnabled: Boolean = false,
    private val onStateChanged: ((Boolean) -> Unit)? = null
) {
    var isEnabled by mutableStateOf(initialEnabled)
        private set

    fun toggle() {
        isEnabled = !isEnabled
        onStateChanged?.invoke(isEnabled)
    }

    fun enable() {
        isEnabled = true
        onStateChanged?.invoke(isEnabled)
    }

    fun disable() {
        isEnabled = false
        onStateChanged?.invoke(isEnabled)
    }
}

/**
 * Erstellt und merkt sich den PerformanceModeState mit Persistierung
 */
@Composable
fun rememberPerformanceModeState(
    context: Context = LocalContext.current
): PerformanceModeState {
    val sharedPreferences = remember {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    val initialEnabled = remember {
        sharedPreferences.getBoolean(KEY_PERFORMANCE_MODE_ENABLED, false)
    }

    return remember {
        PerformanceModeState(
            initialEnabled = initialEnabled,
            onStateChanged = { enabled ->
                sharedPreferences.edit {
                    putBoolean(KEY_PERFORMANCE_MODE_ENABLED, enabled)
                }
            }
        )
    }
}

/**
 * 🎨 Dimmed Color Helper
 * Reduziert die Sättigung und Helligkeit einer Farbe im Battery-Save-Modus
 */
@Composable
fun Color.dimmedIfNeeded(
    dimFactor: Float = 0.6f
): Color {
    val performanceMode = LocalPerformanceMode.current
    return if (performanceMode.isEnabled) {
        this.copy(
            red = this.red * dimFactor,
            green = this.green * dimFactor,
            blue = this.blue * dimFactor,
            alpha = this.alpha * 0.85f
        )
    } else {
        this
    }
}

/**
 * 🔄 Animation Duration Helper
 * Gibt 0 zurück wenn Battery-Save aktiv ist, sonst die normale Duration
 */
@Composable
fun animationDurationIfEnabled(normalDuration: Int): Int {
    val performanceMode = LocalPerformanceMode.current
    return if (performanceMode.isEnabled) 0 else normalDuration
}

/**
 * ✨ Prüft ob Animationen aktiviert sind
 */
@Composable
fun areAnimationsEnabled(): Boolean {
    return !LocalPerformanceMode.current.isEnabled
}

