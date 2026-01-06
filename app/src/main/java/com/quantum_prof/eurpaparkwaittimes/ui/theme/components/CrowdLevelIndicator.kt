package com.quantum_prof.eurpaparkwaittimes.ui.theme.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.quantum_prof.eurpaparkwaittimes.data.CrowdLevel
import com.quantum_prof.eurpaparkwaittimes.ui.theme.LocalPerformanceMode

@Composable
fun CrowdLevelIndicator(
    crowdLevel: CrowdLevel,
    allAttractionsClosed: Boolean = false,
    modifier: Modifier = Modifier
) {
    val percentage = crowdLevel.crowdLevel.toFloat()
    val animatedPercentage by animateFloatAsState(
        targetValue = if (allAttractionsClosed) 0f else percentage,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "crowd_level_progress"
    )

    val color = if (allAttractionsClosed) {
        Color(0xFF757575) // Gray for closed
    } else when {
        percentage < 30 -> Color(0xFF4CAF50) // Green
        percentage < 60 -> Color(0xFFFFC107) // Amber
        percentage < 85 -> Color(0xFFFF9800) // Orange
        else -> Color(0xFFF44336) // Red
    }

    val animatedColor by animateColorAsState(targetValue = color, label = "color")

    val description = if (allAttractionsClosed) {
        "Geschlossen"
    } else when {
        percentage < 30 -> "Niedrig"
        percentage < 60 -> "Mäßig"
        percentage < 85 -> "Hoch"
        else -> "Sehr Hoch"
    }

    // 🔋 Performance Mode check
    val performanceMode = LocalPerformanceMode.current
    val animationsEnabled = !performanceMode.isEnabled

    // ✨ Micro animations for wow effect
    // 🔋 Completely disabled in Battery-Save Mode (no infiniteTransition created)

    // Pulse animation for high crowd levels
    val pulseScale = if (animationsEnabled && !allAttractionsClosed && percentage >= 85) {
        val pulseTransition = rememberInfiniteTransition(label = "pulse_transition")
        val animatedPulse by pulseTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.05f,
            animationSpec = infiniteRepeatable(
                animation = tween(800, easing = EaseInOutSine),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulse"
        )
        animatedPulse
    } else {
        1f // Statischer Wert im Performance-Modus
    }

    // Glow intensity animation
    val glowIntensity = if (animationsEnabled) {
        val glowTransition = rememberInfiniteTransition(label = "glow_transition")
        val animatedGlow by glowTransition.animateFloat(
            initialValue = 0.3f,
            targetValue = 0.6f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500, easing = EaseInOutSine),
                repeatMode = RepeatMode.Reverse
            ),
            label = "glow"
        )
        animatedGlow
    } else {
        0.45f // Statischer Wert im Performance-Modus
    }

    // Icon scale for closed state
    val iconScale = if (animationsEnabled && allAttractionsClosed) {
        val iconTransition = rememberInfiniteTransition(label = "icon_transition")
        val animatedIconScale by iconTransition.animateFloat(
            initialValue = 1f,
            targetValue = 0.95f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = EaseInOutSine),
                repeatMode = RepeatMode.Reverse
            ),
            label = "icon_scale"
        )
        animatedIconScale
    } else {
        1f // Statischer Wert im Performance-Modus
    }

    Card(
        modifier = modifier
            .scale(pulseScale),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
        ),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (!allAttractionsClosed && percentage >= 60) 8.dp else 4.dp
        )
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Circular Indicator with glow effect
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(48.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Outer glow for high crowd levels
                    if (!allAttractionsClosed && percentage >= 60) {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    animatedColor.copy(alpha = glowIntensity * 0.4f),
                                    Color.Transparent
                                ),
                                radius = size.minDimension * 0.7f
                            ),
                            radius = size.minDimension * 0.6f
                        )
                    }

                    // Background Track with subtle gradient
                    drawArc(
                        brush = Brush.sweepGradient(
                            colors = listOf(
                                animatedColor.copy(alpha = 0.15f),
                                animatedColor.copy(alpha = 0.25f),
                                animatedColor.copy(alpha = 0.15f)
                            )
                        ),
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Progress arc with gradient
                    if (animatedPercentage > 0) {
                        drawArc(
                            brush = Brush.sweepGradient(
                                colors = listOf(
                                    animatedColor,
                                    animatedColor.copy(alpha = 0.8f),
                                    animatedColor
                                ),
                                center = Offset(size.width / 2, size.height / 2)
                            ),
                            startAngle = -90f,
                            sweepAngle = (animatedPercentage / 100f) * 360f,
                            useCenter = false,
                            style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                }

                Icon(
                    imageVector = if (allAttractionsClosed) Icons.Default.NightsStay else Icons.Default.Groups,
                    contentDescription = null,
                    tint = animatedColor,
                    modifier = Modifier
                        .size(22.dp)
                        .scale(iconScale)
                )
            }

            Column {
                Text(
                    text = "Besucheraufkommen",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = if (allAttractionsClosed) "-" else "${percentage.toInt()}%",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = animatedColor,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 1.dp)
                    )
                }
            }
        }
    }
}
