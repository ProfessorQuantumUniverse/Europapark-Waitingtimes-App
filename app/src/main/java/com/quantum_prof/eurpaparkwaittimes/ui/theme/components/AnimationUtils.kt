package com.quantum_prof.eurpaparkwaittimes.ui.theme.components

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.rotate
import com.quantum_prof.eurpaparkwaittimes.ui.theme.LocalPerformanceMode
import kotlin.math.*
import kotlin.random.Random

// 🎆 POLISHED ANIMATIONS FOR EUROPAPARK APP 🎆
// 🔋 All animations respect Battery Save / Performance Mode

/**
 * 🌟 Pulsierender Glow-Effekt für wichtige Elemente
 * 🔋 Deaktiviert im Battery-Save-Modus
 */
@Composable
fun Modifier.pulsingGlow(
    glowColor: Color = Color.Cyan,
    animationDuration: Int = 2000
): Modifier = composed {
    val performanceMode = LocalPerformanceMode.current

    // 🔋 Im Battery-Save-Modus: Kein Glow-Effekt
    if (performanceMode.isEnabled) {
        return@composed this
    }

    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(animationDuration, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    this.drawBehind {
        val radius = size.minDimension * 0.7f
        val brush = Brush.radialGradient(
            colors = listOf(
                glowColor.copy(alpha = glowAlpha * 0.4f),
                glowColor.copy(alpha = glowAlpha * 0.1f),
                Color.Transparent
            ),
            radius = radius
        )
        drawCircle(
            brush = brush,
            radius = radius,
            center = center
        )
    }
}

/**
 * 🌟 Starburst Animation
 * 🔋 Deaktiviert im Battery-Save-Modus
 */
@Composable
fun StarburstAnimation(
    isActive: Boolean,
    modifier: Modifier = Modifier,
    color: Color = Color.White
) {
    val performanceMode = LocalPerformanceMode.current

    // 🔋 Im Battery-Save-Modus: Keine Animation
    if (performanceMode.isEnabled) return

    val animationProgress by animateFloatAsState(
        targetValue = if (isActive) 1f else 0f,
        animationSpec = tween(1000, easing = EaseOutBack),
        label = "starburst"
    )

    if (animationProgress > 0f) {
        Canvas(modifier = modifier.fillMaxSize()) {
            val rayCount = 12
            val maxLength = size.minDimension * 0.4f * animationProgress

            repeat(rayCount) { i ->
                val angle = (360f / rayCount) * i * PI / 180
                val length = maxLength * (0.7f + 0.3f * sin(animationProgress * PI))

                val startX = center.x + cos(angle) * 20f
                val startY = center.y + sin(angle) * 20f
                val endX = center.x + cos(angle) * length
                val endY = center.y + sin(angle) * length

                drawLine(
                    color = color.copy(alpha = 1f - animationProgress * 0.5f),
                    start = Offset(startX.toFloat(), startY.toFloat()),
                    end = Offset(endX.toFloat(), endY.toFloat()),
                    strokeWidth = 8f * (1f - animationProgress * 0.5f),
                    cap = StrokeCap.Round
                )
            }
        }
    }
}

/**
 * 🌟 Smooth Scale Animation für Favoriten
 * 🔋 Deaktiviert im Battery-Save-Modus
 */
@Composable
fun Modifier.favoriteAnimation(
    isFavorite: Boolean,
    duration: Int = 300
): Modifier = composed {
    val performanceMode = LocalPerformanceMode.current

    // 🔋 Im Battery-Save-Modus: Keine Animation
    if (performanceMode.isEnabled) {
        return@composed this
    }

    val scale by animateFloatAsState(
        targetValue = if (isFavorite) 1.02f else 1.0f,
        animationSpec = tween(
            durationMillis = duration,
            easing = FastOutSlowInEasing
        ),
        label = "favorite_scale"
    )

    if (scale != 1.0f) {
        this.graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
    } else {
        this
    }
}

/**
 * ✨ Sparkle Animation für neu favorisierte Items
 * 🔋 Deaktiviert im Battery-Save-Modus
 */
@Composable
fun Modifier.sparkleOnFavorite(
    isFavorite: Boolean,
    sparkleColor: Color = Color.Yellow
): Modifier = composed {
    val performanceMode = LocalPerformanceMode.current

    // 🔋 Im Battery-Save-Modus: Keine Animation
    if (performanceMode.isEnabled) {
        return@composed this
    }

    var showSparkle by remember { mutableStateOf(false) }

    LaunchedEffect(isFavorite) {
        if (isFavorite) {
            showSparkle = true
        }
    }

    val sparkleAlpha by animateFloatAsState(
        targetValue = if (showSparkle && isFavorite) 1f else 0f,
        animationSpec = tween(500),
        finishedListener = { showSparkle = false },
        label = "sparkle_alpha"
    )

    this.drawBehind {
        if (sparkleAlpha > 0f) {
            val sparkleCount = 8
            val radius = size.maxDimension * 0.3f

            for (i in 0 until sparkleCount) {
                val angle = (i * 45f) * PI / 180
                val x = center.x + cos(angle) * radius
                val y = center.y + sin(angle) * radius

                drawCircle(
                    color = sparkleColor.copy(alpha = sparkleAlpha),
                    radius = 3f,
                    center = Offset(x.toFloat(), y.toFloat())
                )
            }
        }
    }
}

/**
 * 🎯 Smooth Slide Animation für Listenelemente
 * 🔋 Deaktiviert im Battery-Save-Modus
 */
@Composable
fun Modifier.slideInFromBottom(
    delay: Int = 0
): Modifier = composed {
    val performanceMode = LocalPerformanceMode.current

    // 🔋 Im Battery-Save-Modus: Keine Animation
    if (performanceMode.isEnabled) {
        return@composed this
    }

    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(delay.toLong())
        isVisible = true
    }

    val offsetY by animateFloatAsState(
        targetValue = if (isVisible) 0f else 50f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "slide_in"
    )

    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(300),
        label = "fade_in"
    )

    this.graphicsLayer {
        translationY = offsetY
        this.alpha = alpha
    }
}


/**
 * 🎨 Glow Border für Favoriten
 * 🔋 Deaktiviert im Battery-Save-Modus
 */
@Composable
fun Modifier.favoriteGlowBorder(
    isFavorite: Boolean,
    glowColor: Color = Color(0xFFFFD700), // Gold
    borderWidth: Dp = 2.dp
): Modifier = composed {
    val performanceMode = LocalPerformanceMode.current

    // 🔋 Im Battery-Save-Modus: Kein animierter Glow
    if (performanceMode.isEnabled) {
        return@composed this
    }

    val infiniteTransition = rememberInfiniteTransition(label = "favorite_glow")

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = if (isFavorite) 0.3f else 0f,
        targetValue = if (isFavorite) 0.8f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_border_alpha"
    )

    this.drawBehind {
        if (isFavorite && glowAlpha > 0f) {
            drawRoundRect(
                color = glowColor.copy(alpha = glowAlpha),
                size = size,
                style = Stroke(width = borderWidth.toPx()),
                cornerRadius = CornerRadius(16.dp.toPx())
            )
        }
    }
}

/**
 * 💎 Modern Glassmorphism Effect
 * Adds a semi-transparent background with a subtle gradient and border.
 */
fun Modifier.glassBackground(
    shape: Shape = RoundedCornerShape(16.dp),
    tintColor: Color = Color.White,
    alpha: Float = 0.1f,
    strokeWidth: Dp = 1.dp,
    strokeColor: Color = Color.White.copy(alpha = 0.3f)
): Modifier = this
    .clip(shape)
    .background(
        brush = Brush.verticalGradient(
            colors = listOf(
                tintColor.copy(alpha = alpha + 0.05f),
                tintColor.copy(alpha = alpha)
            )
        ),
        shape = shape
    )
    .border(
        width = strokeWidth,
        brush = Brush.linearGradient(
            colors = listOf(
                strokeColor.copy(alpha = 0.5f),
                strokeColor.copy(alpha = 0.1f)
            ),
            start = Offset(0f, 0f),
            end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
        ),
        shape = shape
    )

/**
 * 👆 Bouncy Click Effect with Haptic Feedback
 * Scales the component down slightly when pressed with satisfying haptic feedback.
 * MATERIAL 3 EXPRESSIVE STYLE
 * 🔋 Animation deaktiviert im Battery-Save-Modus (Haptics bleiben)
 */
@Composable
fun Modifier.bouncyClick(
    scaleDown: Float = 0.92f, // Stärkerer Bounce für Expressivität
    enableHaptics: Boolean = true,
    onClick: () -> Unit
): Modifier = composed {
    val performanceMode = LocalPerformanceMode.current
    val view = LocalView.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Haptic feedback beim Drücken (bleibt auch im Performance-Modus)
    LaunchedEffect(isPressed) {
        if (isPressed && enableHaptics) {
            performHapticFeedback(view, HapticType.LIGHT)
        }
    }

    // 🔋 Im Battery-Save-Modus: Keine Animation, nur Click
    if (performanceMode.isEnabled) {
        return@composed this.clickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = {
                if (enableHaptics) {
                    performHapticFeedback(view, HapticType.CONFIRM)
                }
                onClick()
            }
        )
    }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) scaleDown else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "bouncy_click"
    )

    // Leichte Rotation für mehr Dynamik
    val rotation by animateFloatAsState(
        targetValue = if (isPressed) -1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "bouncy_rotation"
    )

    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
            rotationZ = rotation
        }
        .clickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = {
                if (enableHaptics) {
                    performHapticFeedback(view, HapticType.CONFIRM)
                }
                onClick()
            }
        )
}

/**
 * 🎯 Haptic Feedback Types for different interactions
 */
enum class HapticType {
    LIGHT,      // Leichtes Tippen
    CONFIRM,    // Bestätigung
    REJECT,     // Ablehnung/Error
    HEAVY,      // Schwerer Klick
    TICK,       // Tick für Slider/Scrolling
    TOGGLE_ON,  // Toggle aktiviert
    TOGGLE_OFF  // Toggle deaktiviert
}

/**
 * 🎯 Unified Haptic Feedback Function
 * Verwendet die beste verfügbare Haptic-Konstante basierend auf API-Level
 */
fun performHapticFeedback(view: View, type: HapticType) {
    try {
        // First try system haptic feedback
        val constant = when (type) {
            HapticType.LIGHT -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    HapticFeedbackConstants.GESTURE_START
                } else {
                    HapticFeedbackConstants.KEYBOARD_TAP
                }
            }
            HapticType.CONFIRM -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    HapticFeedbackConstants.CONFIRM
                } else {
                    HapticFeedbackConstants.CONTEXT_CLICK
                }
            }
            HapticType.REJECT -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    HapticFeedbackConstants.REJECT
                } else {
                    HapticFeedbackConstants.LONG_PRESS
                }
            }
            HapticType.HEAVY -> {
                HapticFeedbackConstants.LONG_PRESS
            }
            HapticType.TICK -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                    HapticFeedbackConstants.KEYBOARD_PRESS
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    HapticFeedbackConstants.CONTEXT_CLICK
                } else {
                    HapticFeedbackConstants.LONG_PRESS
                }
            }
            HapticType.TOGGLE_ON -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    HapticFeedbackConstants.TOGGLE_ON
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    HapticFeedbackConstants.CONFIRM
                } else {
                    HapticFeedbackConstants.CONTEXT_CLICK
                }
            }
            HapticType.TOGGLE_OFF -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    HapticFeedbackConstants.TOGGLE_OFF
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    HapticFeedbackConstants.REJECT
                } else {
                    HapticFeedbackConstants.VIRTUAL_KEY
                }
            }
        }

        // Always try both approaches for maximum compatibility
        val performedSystem = try {
            view.performHapticFeedback(constant, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING)
        } catch (e: Exception) {
            false
        }

        // Also try without ignoring global setting for some devices
        if (!performedSystem) {
            try {
                view.performHapticFeedback(constant)
            } catch (e: Exception) {
                // Continue to vibrator fallback
            }
        }

        // Always also trigger vibrator for maximum compatibility and stronger feedback
        triggerVibratorFeedback(view.context, type)

    } catch (e: Exception) {
        // Last resort - simple vibrator
        triggerVibratorFeedback(view.context, type)
    }
}

private fun triggerVibratorFeedback(context: Context, type: HapticType) {
    try {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }

        if (vibrator?.hasVibrator() == true) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Use VibrationEffect for API 26+
                val effect = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    // Try predefined effects first (API 29+)
                    try {
                        val effectId = when (type) {
                            HapticType.LIGHT, HapticType.TICK -> VibrationEffect.EFFECT_TICK
                            HapticType.CONFIRM, HapticType.TOGGLE_ON -> VibrationEffect.EFFECT_CLICK
                            HapticType.TOGGLE_OFF -> VibrationEffect.EFFECT_TICK
                            HapticType.HEAVY -> VibrationEffect.EFFECT_HEAVY_CLICK
                            HapticType.REJECT -> VibrationEffect.EFFECT_DOUBLE_CLICK
                        }
                        VibrationEffect.createPredefined(effectId)
                    } catch (e: Exception) {
                        // Fallback to OneShot if predefined fails
                        createOneShot(type)
                    }
                } else {
                    // For API 26-28, use OneShot
                    createOneShot(type)
                }

                vibrator.vibrate(effect)
            } else {
                // Legacy vibration for API < 26
                @Suppress("DEPRECATION")
                val duration = when (type) {
                    HapticType.LIGHT, HapticType.TICK -> 25L
                    HapticType.CONFIRM, HapticType.TOGGLE_ON, HapticType.TOGGLE_OFF -> 50L
                    HapticType.HEAVY -> 100L
                    HapticType.REJECT -> 150L
                }
                vibrator.vibrate(duration)
            }
        }
    } catch (e: Exception) {
        // Silently fail if vibration is not available
    }
}

private fun createOneShot(type: HapticType): VibrationEffect {
    val duration = when (type) {
        HapticType.LIGHT, HapticType.TICK -> 25L
        HapticType.CONFIRM, HapticType.TOGGLE_ON, HapticType.TOGGLE_OFF -> 50L
        HapticType.HEAVY -> 100L
        HapticType.REJECT -> 150L
    }
    val amplitude = when (type) {
        HapticType.LIGHT, HapticType.TICK -> 80 // Subtle
        HapticType.CONFIRM, HapticType.TOGGLE_ON -> 150 // Medium
        HapticType.TOGGLE_OFF -> 100 // Slightly weaker
        HapticType.HEAVY, HapticType.REJECT -> 255 // Max strength
    }
    return VibrationEffect.createOneShot(duration, amplitude)
}

/**
 * ✨ Shimmer Effect for Loading or Highlighting
 * 🔋 Deaktiviert im Battery-Save-Modus
 */
@Composable
fun Modifier.shimmerEffect(
    isActive: Boolean = true,
    widthOfShadowBrush: Int = 500,
    angleOfAxisY: Float = 270f,
    durationMillis: Int = 1000,
): Modifier = composed {
    val performanceMode = LocalPerformanceMode.current

    // 🔋 Im Battery-Save-Modus: Kein Shimmer
    if (!isActive || performanceMode.isEnabled) return@composed this

    val shimmerColors = listOf(
        Color.White.copy(alpha = 0.0f),
        Color.White.copy(alpha = 0.3f),
        Color.White.copy(alpha = 0.0f),
    )

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnimation = transition.animateFloat(
        initialValue = 0f,
        targetValue = widthOfShadowBrush + 1000f, // Move across
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = durationMillis,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    this.background(
        brush = Brush.linearGradient(
            colors = shimmerColors,
            start = Offset(x = translateAnimation.value - widthOfShadowBrush, y = 0.0f),
            end = Offset(x = translateAnimation.value, y = angleOfAxisY),
        )
    )
}

/**
 * 🎢 Zoom Out Animation auf Scroll - "Nach hinten wegfahren" Effekt
 * Der Indicator zoomt heraus und verblasst beim Scrollen
 * MATERIAL 3 EXPRESSIVE STYLE - Stärkere, dramatischere Animation
 * 🔋 Deaktiviert im Battery-Save-Modus
 */
@Composable
fun Modifier.zoomOutOnScroll(
    lazyListState: LazyListState,
    maxScrollOffset: Float = 150f // Schnellere Reaktion
): Modifier = composed {
    val performanceMode = LocalPerformanceMode.current

    // 🔋 Im Battery-Save-Modus: Keine Animation
    if (performanceMode.isEnabled) {
        return@composed this
    }

    // Berechne den Scroll-Offset basierend auf dem ersten sichtbaren Item
    val scrollOffset by remember {
        derivedStateOf {
            if (lazyListState.firstVisibleItemIndex == 0) {
                lazyListState.firstVisibleItemScrollOffset.toFloat()
            } else {
                maxScrollOffset // Item ist bereits gescrollt
            }
        }
    }

    // Berechne den Progress mit easeOut für expressiveren Effekt
    val rawProgress = (scrollOffset / maxScrollOffset).coerceIn(0f, 1f)
    // EaseOutCubic für dramatischeren Start
    val progress = 1f - (1f - rawProgress).let { it * it * it }

    // EXPRESSIVE animierte Werte - stärkere Effekte!
    val animatedScale by animateFloatAsState(
        targetValue = 1f - (progress * 0.55f), // Von 1.0 bis 0.45 skalieren (stärker!)
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy, // Leichter Bounce für Expressivität
            stiffness = Spring.StiffnessLow // Weicher für dramatischeren Effekt
        ),
        label = "zoom_out_scale"
    )

    val animatedAlpha by animateFloatAsState(
        targetValue = 1f - (progress * 0.95f), // Von 1.0 bis 0.05 faden (fast komplett weg)
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "zoom_out_alpha"
    )

    val animatedTranslationY by animateFloatAsState(
        targetValue = -progress * 80f, // Stärker nach oben "wegfliegen"
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "zoom_out_y"
    )

    // Blur-ähnlicher Effekt durch leichte Rotation
    val animatedRotationX by animateFloatAsState(
        targetValue = progress * 25f, // Stärkere 3D Rotation
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "zoom_out_rotation"
    )

    this.graphicsLayer {
        scaleX = animatedScale
        scaleY = animatedScale
        alpha = animatedAlpha
        translationY = animatedTranslationY
        // Dramatischer 3D-Effekt
        rotationX = animatedRotationX
        cameraDistance = 8f * density // Nähere Kamera für stärkeren 3D Effekt
        // Leichte seitliche Neigung für mehr Dynamik
        rotationZ = progress * 3f
    }
}

/**
 * 🌊 Floating Animation - sanftes Auf und Ab Schweben
 * 🔋 Deaktiviert im Battery-Save-Modus
 */
@Composable
fun Modifier.floatingAnimation(
    amplitude: Float = 3f,
    duration: Int = 3000
): Modifier = composed {
    val performanceMode = LocalPerformanceMode.current

    // 🔋 Im Battery-Save-Modus: Keine Animation
    if (performanceMode.isEnabled) {
        return@composed this
    }

    val infiniteTransition = rememberInfiniteTransition(label = "floating")

    val offsetY by infiniteTransition.animateFloat(
        initialValue = -amplitude,
        targetValue = amplitude,
        animationSpec = infiniteRepeatable(
            animation = tween(duration, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floating_y"
    )

    this.graphicsLayer {
        translationY = offsetY
    }
}

/**
 * 💫 Subtle Breathing Animation - dezentes "Atmen" für lebendige UI
 * 🔋 Deaktiviert im Battery-Save-Modus
 */
@Composable
fun Modifier.breathingAnimation(
    minScale: Float = 0.98f,
    maxScale: Float = 1.02f,
    duration: Int = 4000
): Modifier = composed {
    val performanceMode = LocalPerformanceMode.current

    // 🔋 Im Battery-Save-Modus: Keine Animation
    if (performanceMode.isEnabled) {
        return@composed this
    }

    val infiniteTransition = rememberInfiniteTransition(label = "breathing")

    val scale by infiniteTransition.animateFloat(
        initialValue = minScale,
        targetValue = maxScale,
        animationSpec = infiniteRepeatable(
            animation = tween(duration, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathing_scale"
    )

    this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}

/**
 * 🎯 Attention Grab Animation - kurzer "Aufmerksamkeit" Effekt
 * 🔋 Deaktiviert im Battery-Save-Modus
 */
@Composable
fun Modifier.attentionGrab(
    isActive: Boolean = false
): Modifier = composed {
    val performanceMode = LocalPerformanceMode.current

    // 🔋 Im Battery-Save-Modus: Keine Animation
    if (performanceMode.isEnabled) {
        return@composed this
    }

    var triggered by remember { mutableStateOf(false) }

    LaunchedEffect(isActive) {
        if (isActive) {
            triggered = true
            kotlinx.coroutines.delay(600)
            triggered = false
        }
    }

    val scale by animateFloatAsState(
        targetValue = if (triggered) 1.08f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "attention_scale"
    )

    val rotation by animateFloatAsState(
        targetValue = if (triggered) 2f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "attention_rotation"
    )

    this.graphicsLayer {
        scaleX = scale
        scaleY = scale
        rotationZ = if (triggered) sin(rotation * PI.toFloat()) * 3f else 0f
    }
}

/**
 * 🌈 Subtle Color Shift Glow - dezenter Farbverlauf-Schimmer
 * 🔋 Deaktiviert im Battery-Save-Modus
 */
@Composable
fun Modifier.subtleColorShiftGlow(
    baseColor: Color = Color(0xFF2196F3),
    secondColor: Color = Color(0xFF9C27B0),
    duration: Int = 5000
): Modifier = composed {
    val performanceMode = LocalPerformanceMode.current

    // 🔋 Im Battery-Save-Modus: Keine Animation
    if (performanceMode.isEnabled) {
        return@composed this
    }

    val infiniteTransition = rememberInfiniteTransition(label = "color_shift")

    val colorProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(duration, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "color_progress"
    )

    this.drawBehind {
        val currentColor = lerp(baseColor, secondColor, colorProgress)
        drawRoundRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    currentColor.copy(alpha = 0.15f),
                    Color.Transparent
                ),
                radius = size.maxDimension * 0.8f
            ),
            cornerRadius = CornerRadius(16.dp.toPx())
        )
    }
}

/**
 * Utility function to interpolate between colors
 */
private fun lerp(start: Color, stop: Color, fraction: Float): Color {
    return Color(
        red = start.red + (stop.red - start.red) * fraction,
        green = start.green + (stop.green - start.green) * fraction,
        blue = start.blue + (stop.blue - start.blue) * fraction,
        alpha = start.alpha + (stop.alpha - start.alpha) * fraction
    )
}

/**
 * 🚀 Partikel-Explosion Effekt
 */
@Composable
fun ParticleExplosion(
    isActive: Boolean,
    modifier: Modifier = Modifier,
    colors: List<Color> = listOf(Color.Yellow, Color.Red, Color.Blue, Color.Green),
    particleCount: Int = 100 // Mehr Partikel für krasseren Effekt
) {
    val performanceMode = LocalPerformanceMode.current
    if (performanceMode.isEnabled || !isActive) return

    val particles = remember {
        List(particleCount) {
            Particle(
                angle = Random.nextFloat() * 360f,
                speed = Random.nextFloat() * 800f + 200f, // Deutlich schneller/weiter
                size = Random.nextFloat() * 12f + 4f,
                color = colors.random(),
                rotation = Random.nextFloat() * 360f,
                rotationSpeed = Random.nextFloat() * 10f - 5f
            )
        }
    }

    val animationProgress by animateFloatAsState(
        targetValue = if (isActive) 1f else 0f,
        animationSpec = tween(
            durationMillis = 1500,
            easing = EaseOutExpo
        ),
        label = "particle_explosion"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        if (animationProgress > 0f && animationProgress < 1f) {
            particles.forEach { particle ->
                val progress = animationProgress
                val distance = particle.speed * progress
                // Schwerkraft simulieren
                val gravity = 800f * progress * progress

                val x = center.x + cos(particle.angle * PI / 180) * distance
                val y = center.y + sin(particle.angle * PI / 180) * distance + gravity

                val alpha = (1f - progress).coerceIn(0f, 1f)
                val scale = (1f - progress * 0.5f) // Werden etwas kleiner

                rotate(
                    degrees = particle.rotation + particle.rotationSpeed * progress * 100f,
                    pivot = Offset(x.toFloat(), y.toFloat())
                ) {
                    drawRect(
                        color = particle.color.copy(alpha = alpha),
                        topLeft = Offset(x.toFloat() - particle.size/2 * scale, y.toFloat() - particle.size/2 * scale),
                        size = Size(particle.size * scale, particle.size * scale)
                    )
                }
            }
        }
    }
}

private data class Particle(
    val angle: Float,
    val speed: Float,
    val size: Float,
    val color: Color,
    val rotation: Float,
    val rotationSpeed: Float
)
