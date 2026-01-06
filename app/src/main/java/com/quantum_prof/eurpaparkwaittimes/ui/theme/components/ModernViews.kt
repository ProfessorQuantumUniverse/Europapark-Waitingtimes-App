package com.quantum_prof.eurpaparkwaittimes.ui.theme.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.quantum_prof.eurpaparkwaittimes.ui.theme.LocalPerformanceMode

@Composable
fun ModernErrorView(
    errorMessage: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 🔋 Performance Mode check
    val performanceMode = LocalPerformanceMode.current
    val animationsEnabled = !performanceMode.isEnabled

    // Rotating animation for refresh icon
    // 🔋 Completely disabled in Battery-Save Mode
    val rotation = if (animationsEnabled) {
        val infiniteTransition = rememberInfiniteTransition(label = "errorAnimation")
        val animatedRotation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "rotation"
        )
        animatedRotation
    } else {
        0f // Statischer Wert im Performance-Modus
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Error illustration
        Box(
            modifier = Modifier
                .size(120.dp)
                .glassBackground(
                    shape = RoundedCornerShape(32.dp),
                    tintColor = MaterialTheme.colorScheme.errorContainer,
                    alpha = 0.3f
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Error",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(48.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Ups! Etwas ist schiefgelaufen",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = errorMessage,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        val view = LocalView.current
        Button(
            onClick = {
                performHapticFeedback(view, HapticType.CONFIRM)
                onRetry()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Retry",
                modifier = Modifier
                    .size(18.dp)
                    .rotate(rotation)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Erneut versuchen")
        }
    }
}

@Composable
fun ModernEmptyView(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Empty illustration
        Box(
            modifier = Modifier
                .size(120.dp)
                .glassBackground(
                    shape = RoundedCornerShape(32.dp),
                    tintColor = MaterialTheme.colorScheme.surfaceVariant,
                    alpha = 0.5f
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Empty",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(48.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ModernLoadingView(
    modifier: Modifier = Modifier
) {
    // 🔋 Performance Mode check
    val performanceMode = LocalPerformanceMode.current
    val animationsEnabled = !performanceMode.isEnabled

    // Multiple rotating animations for spectacular effect
    // 🔋 Completely disabled in Battery-Save Mode
    val rotation1 = if (animationsEnabled) {
        val infiniteTransition = rememberInfiniteTransition(label = "loadingAnimation1")
        val animatedRotation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "rotation1"
        )
        animatedRotation
    } else {
        0f // Statischer Wert im Performance-Modus
    }

    val rotation2 = if (animationsEnabled) {
        val infiniteTransition = rememberInfiniteTransition(label = "loadingAnimation2")
        val animatedRotation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = -360f,
            animationSpec = infiniteRepeatable(
                animation = tween(3000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "rotation2"
        )
        animatedRotation
    } else {
        0f // Statischer Wert im Performance-Modus
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Spectacular loading animation
        Box(
            modifier = Modifier
                .size(120.dp)
                .glassBackground(
                    shape = RoundedCornerShape(60.dp), // Circle
                    tintColor = MaterialTheme.colorScheme.surface,
                    alpha = 0.3f
                ),
            contentAlignment = Alignment.Center
        ) {
            // Outer rotating circle
            CircularProgressIndicator(
                modifier = Modifier
                    .size(100.dp)
                    .rotate(rotation1),
                strokeWidth = 6.dp,
                color = MaterialTheme.colorScheme.primary
            )

            // Inner rotating circle
            CircularProgressIndicator(
                modifier = Modifier
                    .size(60.dp)
                    .rotate(rotation2),
                strokeWidth = 4.dp,
                color = MaterialTheme.colorScheme.secondary
            )

            // Center icon
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Loading",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .size(24.dp)
                    .rotate(rotation1 * 0.5f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Lade Wartezeiten...",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Die neuesten Daten werden abgerufen",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
