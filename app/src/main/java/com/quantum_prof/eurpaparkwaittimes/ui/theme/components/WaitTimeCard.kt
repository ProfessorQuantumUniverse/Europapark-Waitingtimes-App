package com.quantum_prof.eurpaparkwaittimes.ui.theme.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.quantum_prof.eurpaparkwaittimes.data.AttractionWaitTime
import com.quantum_prof.eurpaparkwaittimes.getAttractionIconResId
import com.quantum_prof.eurpaparkwaittimes.ui.theme.*
import com.quantum_prof.eurpaparkwaittimes.ui.theme.LocalPerformanceMode
import java.util.*
import kotlin.math.sin

// Gold colors for favorites
private val FavoriteGold = Color(0xFFFFD700)
private val FavoriteGoldDark = Color(0xFFDAA520)
private val FavoriteGoldLight = Color(0xFFFFF8DC)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaitTimeCard(
    attraction: AttractionWaitTime,
    isFavorite: Boolean,
    hasAlert: Boolean,
    onFavoriteToggle: () -> Unit,
    onAlertClick: () -> Unit,
    modifier: Modifier = Modifier,
    currentAlert: com.quantum_prof.eurpaparkwaittimes.data.notification.WaitTimeAlert? = null
) {
    val isOpen = attraction.status.lowercase(Locale.GERMANY) == "opened"
    val waitTimeColor = getWaitTimeColor(attraction.waitTimeMinutes, isOpen)
    val waitTimeText = if (isOpen) "${attraction.waitTimeMinutes} min" else "Geschlossen"

    // 🔋 Performance Mode check (moved up to be available for sparkle animation)
    val performanceMode = LocalPerformanceMode.current
    val animationsEnabled = !performanceMode.isEnabled

    // Sparkle animation state - only trigger on change to favorite
    // 🔋 Disabled in Battery-Save Mode
    var showSparkles by remember { mutableStateOf(false) }
    var wasFavorite by remember { mutableStateOf(isFavorite) }

    LaunchedEffect(isFavorite, animationsEnabled) {
        if (animationsEnabled && isFavorite && !wasFavorite) {
            showSparkles = true
            kotlinx.coroutines.delay(800)
            showSparkles = false
        }
        wasFavorite = isFavorite
    }


    // ✨ Micro-animation: Subtle breathing effect for favorites
    // 🔋 Completely disabled in Battery-Save Mode (no infiniteTransition created)
    val breathingScale = if (animationsEnabled && isFavorite) {
        val infiniteTransition = rememberInfiniteTransition(label = "favorite_breathing")
        val animatedScale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.008f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = EaseInOutSine),
                repeatMode = RepeatMode.Reverse
            ),
            label = "breathing"
        )
        animatedScale
    } else {
        1f // Statischer Wert im Performance-Modus
    }

    // ✨ Shimmer effect offset for favorite badge
    // 🔋 Completely disabled in Battery-Save Mode
    val shimmerOffset = if (animationsEnabled) {
        val shimmerTransition = rememberInfiniteTransition(label = "shimmer_transition")
        val animatedOffset by shimmerTransition.animateFloat(
            initialValue = -200f,
            targetValue = 200f,
            animationSpec = infiniteRepeatable(
                animation = tween(3000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "shimmer"
        )
        animatedOffset
    } else {
        0f // Statischer Wert im Performance-Modus
    }

    // ✨ Subtle glow pulse for the star badge
    // 🔋 Completely disabled in Battery-Save Mode
    val starGlow = if (animationsEnabled) {
        val glowTransition = rememberInfiniteTransition(label = "star_glow_transition")
        val animatedGlow by glowTransition.animateFloat(
            initialValue = 0.6f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500, easing = EaseInOutSine),
                repeatMode = RepeatMode.Reverse
            ),
            label = "star_glow"
        )
        animatedGlow
    } else {
        0.8f // Statischer Wert im Performance-Modus
    }

    // Main card container with favorite styling
    Box(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = breathingScale
                scaleY = breathingScale
            }
            .then(
                if (isFavorite) {
                    Modifier
                        .shadow(
                            elevation = 12.dp,
                            shape = RoundedCornerShape(24.dp),
                            ambientColor = FavoriteGold.copy(alpha = 0.4f),
                            spotColor = FavoriteGold.copy(alpha = 0.5f)
                        )
                        .border(
                            width = 2.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    FavoriteGold,
                                    FavoriteGoldDark,
                                    FavoriteGold
                                )
                            ),
                            shape = RoundedCornerShape(24.dp)
                        )
                } else {
                    Modifier
                }
            )
            .glassBackground(
                shape = RoundedCornerShape(24.dp),
                tintColor = if (isFavorite) FavoriteGoldLight else MaterialTheme.colorScheme.surfaceVariant,
                alpha = if (isFavorite) 0.95f else 0.7f,
                strokeColor = if (isFavorite) Color.Transparent else Color.White.copy(alpha = 0.3f)
            )
    ) {
        // Favorite gradient overlay
        if (isFavorite) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                FavoriteGold.copy(alpha = 0.08f),
                                Color.Transparent,
                                FavoriteGold.copy(alpha = 0.05f)
                            )
                        )
                    )
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Attraction Icon with favorite badge
            Box(
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            if (isFavorite) {
                                Brush.radialGradient(
                                    colors = listOf(
                                        waitTimeColor.copy(alpha = 0.3f),
                                        FavoriteGold.copy(alpha = 0.15f)
                                    )
                                )
                            } else {
                                Brush.radialGradient(
                                    colors = listOf(
                                        waitTimeColor.copy(alpha = 0.2f),
                                        waitTimeColor.copy(alpha = 0.08f)
                                    )
                                )
                            }
                        )
                        .then(
                            if (isFavorite) {
                                Modifier.border(
                                    width = 1.5.dp,
                                    color = FavoriteGold.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(18.dp)
                                )
                            } else Modifier
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = getAttractionIconResId(attraction.code)),
                        contentDescription = attraction.name,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Fit
                    )
                }

                // Small favorite star badge with shimmer
                if (isFavorite) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 4.dp, y = (-4).dp)
                            .size(22.dp)
                            .shadow(
                                elevation = (4 + 2 * starGlow).dp,
                                shape = CircleShape,
                                ambientColor = FavoriteGold.copy(alpha = starGlow * 0.5f),
                                spotColor = FavoriteGold.copy(alpha = starGlow * 0.6f)
                            )
                            .clip(CircleShape)
                            .drawBehind {
                                // Shimmer overlay
                                drawRect(
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color.White.copy(alpha = 0.4f),
                                            Color.Transparent
                                        ),
                                        start = Offset(shimmerOffset - 50f, 0f),
                                        end = Offset(shimmerOffset + 50f, size.height)
                                    )
                                )
                            }
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        FavoriteGold,
                                        FavoriteGoldDark.copy(alpha = 0.9f)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier
                                .size(13.dp)
                                .scale(0.95f + 0.1f * starGlow) // Subtle pulse
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Attraction name with favorite styling
                Text(
                    text = attraction.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = if (isFavorite) FontWeight.Bold else FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = if (isFavorite) {
                        FavoriteGoldDark
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Wait Time Badge
                    Surface(
                        color = waitTimeColor.copy(alpha = if (isFavorite) 0.2f else 0.12f),
                        shape = RoundedCornerShape(10.dp),
                        tonalElevation = if (isFavorite) 2.dp else 0.dp
                    ) {
                        Text(
                            text = waitTimeText,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = waitTimeColor
                        )
                    }

                    // Alert badge
                    if (hasAlert && currentAlert != null) {
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NotificationsActive,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "< ${currentAlert.targetTime} min",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            // Action buttons
            val view = LocalView.current

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Favorite button with improved styling and pulse + haptic feedback
                IconButton(
                    onClick = {
                        // Satisfying Haptic feedback - stärker für Favorite Action
                        performHapticFeedback(
                            view,
                            if (isFavorite) HapticType.TOGGLE_OFF else HapticType.TOGGLE_ON
                        )
                        onFavoriteToggle()
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .then(
                            if (isFavorite) {
                                Modifier
                                    .scale(0.98f + 0.04f * starGlow)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.radialGradient(
                                            colors = listOf(
                                                FavoriteGold.copy(alpha = 0.2f * starGlow),
                                                FavoriteGold.copy(alpha = 0.08f)
                                            )
                                        )
                                    )
                                    .drawBehind {
                                        // Outer glow ring
                                        drawCircle(
                                            color = FavoriteActive.copy(alpha = 0.15f * starGlow),
                                            radius = size.minDimension * 0.7f
                                        )
                                    }
                            } else Modifier
                        )
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = if (isFavorite) "Aus Favoriten entfernen" else "Zu Favoriten hinzufügen",
                        tint = if (isFavorite) FavoriteActive else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Alert button with subtle animation + haptic feedback
                IconButton(
                    onClick = {
                        performHapticFeedback(view, HapticType.LIGHT)
                        onAlertClick()
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .then(
                            if (hasAlert) {
                                Modifier
                                    .clip(CircleShape)
                                    .background(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                    )
                            } else Modifier
                        )
                ) {
                    Icon(
                        imageVector = if (hasAlert) Icons.Default.EditNotifications else Icons.Default.AddAlert,
                        contentDescription = if (hasAlert) "Alert bearbeiten" else "Alert hinzufügen",
                        tint = if (hasAlert) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        // Sparkle overlay for new favorites
        if (showSparkles) {
            StarburstAnimation(
                isActive = showSparkles,
                color = FavoriteGold,
                modifier = Modifier.matchParentSize()
            )
        }
    }
}

@Composable
private fun getWaitTimeColor(waitTime: Int, isOpen: Boolean): Color {
    return if (!isOpen) {
        WaitTimeClosed
    } else {
        when {
            waitTime <= 15 -> WaitTimeShort
            waitTime <= 30 -> WaitTimeMedium
            waitTime <= 60 -> WaitTimeLong
            else -> WaitTimeVeryLong
        }
    }
}
