package com.quantum_prof.eurpaparkwaittimes.ui.theme.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties


data class WelcomePage(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val mainColor: Color,
    val gradientColors: List<Color> = listOf(mainColor, mainColor.copy(alpha = 0.7f))
)

@Composable
private fun AnimatedBackgroundDecoration(
    colors: List<Color>,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "backgroundAnimation")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    // Additional floating orbs animation
    val orb1Offset by infiniteTransition.animateFloat(
        initialValue = -50f,
        targetValue = 50f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orb1"
    )

    val orb2Offset by infiniteTransition.animateFloat(
        initialValue = 30f,
        targetValue = -30f,
        animationSpec = infiniteRepeatable(
            animation = tween(3500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orb2"
    )

    Box(modifier = modifier.fillMaxSize()) {
        // Main gradient background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .rotate(rotation)
                .scale(scale)
                .blur(60.dp)
                .alpha(0.15f)
                .background(
                    brush = Brush.radialGradient(
                        colors = colors
                    )
                )
        )

        // Floating orb 1
        Box(
            modifier = Modifier
                .size(80.dp)
                .offset(x = 20.dp + orb1Offset.dp, y = 40.dp)
                .blur(40.dp)
                .alpha(0.2f)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(colors.first(), Color.Transparent)
                    ),
                    shape = CircleShape
                )
        )

        // Floating orb 2
        Box(
            modifier = Modifier
                .size(60.dp)
                .offset(x = 200.dp + orb2Offset.dp, y = 150.dp)
                .blur(30.dp)
                .alpha(0.15f)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(colors.getOrElse(1) { colors.first() }, Color.Transparent)
                    ),
                    shape = CircleShape
                )
        )
    }
}

@Composable
fun WelcomeDialog(
    onDismiss: () -> Unit
) {
    val view = LocalView.current

    val pages = listOf(
        WelcomePage(
            "Welcome Friend!",
            "Welcome to the unofficial Europa-Park Wait Times Monitor. Your companion for a perfect day at the park!",
            Icons.Default.Celebration,
            MaterialTheme.colorScheme.primary,
            listOf(
                MaterialTheme.colorScheme.primary,
                MaterialTheme.colorScheme.primaryContainer,
                MaterialTheme.colorScheme.tertiary
            )
        ),
        WelcomePage(
            "Unofficial & Free",
            "This app is an independent FOSS project. Not affiliated with Europa-Park.\nNo Ads. No Tracking. Just Wait Times.",
            Icons.Default.Code,
            MaterialTheme.colorScheme.tertiary,
            listOf(
                MaterialTheme.colorScheme.tertiary,
                MaterialTheme.colorScheme.tertiaryContainer,
                MaterialTheme.colorScheme.secondary
            )
        ),
        WelcomePage(
            "Quick Guide",
            "• Swipe down to refresh\n• Tap attractions for alerts\n• Use sort options to plan ahead",
            Icons.Default.TouchApp,
            MaterialTheme.colorScheme.secondary,
            listOf(
                MaterialTheme.colorScheme.secondary,
                MaterialTheme.colorScheme.secondaryContainer,
                MaterialTheme.colorScheme.primary
            )
        ),
        WelcomePage(
            "Notifications",
            "To save your battery, we check wait times approx. every 15 minutes for your alerts. Please keep this in mind.",
            Icons.Default.BatteryChargingFull,
            MaterialTheme.colorScheme.error,
            listOf(
                MaterialTheme.colorScheme.error,
                MaterialTheme.colorScheme.errorContainer,
                Color(0xFFFF6B6B)
            )
        )
    )

    var currentPageIndex by remember { mutableIntStateOf(0) }
    var showExplosion by remember { mutableStateOf(false) }
    var dialogVisible by remember { mutableStateOf(false) }

    // Entrance animation
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(100)
        dialogVisible = true
    }

    // Dialog scale animation
    val dialogScale by animateFloatAsState(
        targetValue = if (dialogVisible) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "dialogScale"
    )

    val dialogAlpha by animateFloatAsState(
        targetValue = if (dialogVisible && !showExplosion) 1f else 0f,
        animationSpec = tween(
            durationMillis = if (showExplosion) 500 else 300,
            easing = FastOutSlowInEasing
        ),
        label = "dialogAlpha"
    )

    if (showExplosion) {
        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(1500) // Wait for explosion to finish
            onDismiss()
        }
    }

    Dialog(
        onDismissRequest = {}, // Helper dialogs often shouldn't disappear on outside click initially
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight()
                .graphicsLayer {
                    scaleX = dialogScale
                    scaleY = dialogScale
                    alpha = dialogAlpha
                }
        ) {/*
            // Animated background decoration
           // AnimatedContent(
             //   targetState = currentPageIndex,
             //   transitionSpec = {
             //       fadeIn(animationSpec = tween(600)) togetherWith
             //       fadeOut(animationSpec = tween(600))
            //    },
           //     label = "BackgroundTransition"
           // ) { targetIndex ->
            //    AnimatedBackgroundDecoration(
             //       colors = pages[targetIndex].gradientColors,
            //        modifier = Modifier.matchParentSize()
            //    )
             */ }

            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .glassBackground(
                        shape = RoundedCornerShape(28.dp),
                        tintColor = MaterialTheme.colorScheme.surface,
                        alpha = 0.9f
                    ),
                shape = RoundedCornerShape(28.dp),
                color = Color.Transparent, // Let glassBackground handle the color
                tonalElevation = 0.dp,
                shadowElevation = 16.dp
            ) {
                Column(
                    modifier = Modifier.padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                // Animated Content for switching pages
                AnimatedContent(
                    targetState = currentPageIndex,
                    transitionSpec = {
                        if (targetState > initialState) {
                            (slideInHorizontally(
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessMediumLow
                                )
                            ) { width -> width } + fadeIn(tween(300))).togetherWith(
                                slideOutHorizontally(
                                    animationSpec = tween(250, easing = FastOutSlowInEasing)
                                ) { width -> -width } + fadeOut(tween(200))
                            )
                        } else {
                            (slideInHorizontally(
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessMediumLow
                                )
                            ) { width -> -width } + fadeIn(tween(300))).togetherWith(
                                slideOutHorizontally(
                                    animationSpec = tween(250, easing = FastOutSlowInEasing)
                                ) { width -> width } + fadeOut(tween(200))
                            )
                        }
                    },
                    label = "PageTransition"
                ) { targetIndex ->
                    val page = pages[targetIndex]

                    // Key for triggering entrance animation on each page
                    var pageEntered by remember { mutableStateOf(false) }
                    LaunchedEffect(targetIndex) {
                        pageEntered = false
                        kotlinx.coroutines.delay(50)
                        pageEntered = true
                    }

                    // Icon scale animation with bounce on page entry
                    val iconScale by animateFloatAsState(
                        targetValue = if (pageEntered) 1f else 0.5f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        ),
                        label = "iconScale"
                    )

                    // Pulse animation for icon
                    val infiniteTransition = rememberInfiniteTransition(label = "iconPulse")
                    val pulse by infiniteTransition.animateFloat(
                        initialValue = 1f,
                        targetValue = 1.08f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(2000, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "pulse"
                    )

                    // Rotation for decorative ring
                    val ringRotation by infiniteTransition.animateFloat(
                        initialValue = 0f,
                        targetValue = 360f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(8000, easing = LinearEasing),
                            repeatMode = RepeatMode.Restart
                        ),
                        label = "ringRotation"
                    )

                    // Icon rotation on page entry
                    val iconRotation by animateFloatAsState(
                        targetValue = if (pageEntered) 0f else -15f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        ),
                        label = "iconRotation"
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Icon Circle with gradient and glow effect
                        Box(
                            modifier = Modifier
                                .size(140.dp)
                                .scale(iconScale * pulse)
                                .floatingAnimation(),
                            contentAlignment = Alignment.Center
                        ) {
                            // Outer decorative ring
                            Canvas(
                                modifier = Modifier
                                    .size(130.dp)
                                    .rotate(ringRotation)
                            ) {
                                val strokeWidth = 2.dp.toPx()
                                // Draw dashed arc segments
                                for (i in 0 until 8) {
                                    val startAngle = i * 45f + 10f
                                    drawArc(
                                        color = page.mainColor.copy(alpha = 0.4f),
                                        startAngle = startAngle,
                                        sweepAngle = 25f,
                                        useCenter = false,
                                        style = androidx.compose.ui.graphics.drawscope.Stroke(
                                            width = strokeWidth,
                                            cap = StrokeCap.Round
                                        )
                                    )
                                }
                            }

                            // Outer glow - enhanced
                            Box(
                                modifier = Modifier
                                    .size(110.dp)
                                    .background(
                                        brush = Brush.radialGradient(
                                            colors = listOf(
                                                page.mainColor.copy(alpha = 0.4f),
                                                page.mainColor.copy(alpha = 0.1f),
                                                Color.Transparent
                                            )
                                        ),
                                        shape = CircleShape
                                    )
                                    .blur(25.dp)
                            )

                            // Main icon background with better gradient
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .rotate(iconRotation)
                                    .background(
                                        brush = Brush.linearGradient(
                                            colors = listOf(
                                                page.mainColor.copy(alpha = 0.2f),
                                                page.mainColor.copy(alpha = 0.35f)
                                            )
                                        ),
                                        shape = CircleShape
                                    )
                                    .drawBehind {
                                        // Inner highlight
                                        drawCircle(
                                            brush = Brush.verticalGradient(
                                                colors = listOf(
                                                    Color.White.copy(alpha = 0.15f),
                                                    Color.Transparent
                                                )
                                            ),
                                            radius = size.minDimension / 2.2f
                                        )
                                    }
                                    .padding(22.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = page.icon,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    tint = page.mainColor
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // Title with animated entrance and subtle glow
                        val titleAlpha by animateFloatAsState(
                            targetValue = if (pageEntered) 1f else 0f,
                            animationSpec = tween(400, delayMillis = 100),
                            label = "titleAlpha"
                        )
                        val titleOffset by animateFloatAsState(
                            targetValue = if (pageEntered) 0f else 20f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMediumLow
                            ),
                            label = "titleOffset"
                        )

                        Text(
                            text = page.title,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .graphicsLayer {
                                    alpha = titleAlpha
                                    translationY = titleOffset
                                }
                                .animateContentSize()
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Description with enhanced styling and staggered animation
                        val descAlpha by animateFloatAsState(
                            targetValue = if (pageEntered) 1f else 0f,
                            animationSpec = tween(400, delayMillis = 200),
                            label = "descAlpha"
                        )
                        val descOffset by animateFloatAsState(
                            targetValue = if (pageEntered) 0f else 30f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioNoBouncy,
                                stiffness = Spring.StiffnessMediumLow
                            ),
                            label = "descOffset"
                        )

                        Text(
                            text = page.description,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.4f,
                            modifier = Modifier
                                .heightIn(min = 100.dp)
                                .graphicsLayer {
                                    alpha = descAlpha
                                    translationY = descOffset
                                }
                                .animateContentSize()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Page Indicators with enhanced design
                Row(
                    modifier = Modifier
                        .padding(bottom = 28.dp)
                        .padding(vertical = 8.dp), // Extra Padding für Glow-Overflow
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    pages.forEachIndexed { index, page ->
                        val isSelected = index == currentPageIndex
                        val width by animateDpAsState(
                            targetValue = if (isSelected) 36.dp else 10.dp,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            ),
                            label = "indicatorWidth"
                        )

                        val height by animateDpAsState(
                            targetValue = if (isSelected) 10.dp else 10.dp,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            ),
                            label = "indicatorHeight"
                        )

                        val indicatorScale by animateFloatAsState(
                            targetValue = if (isSelected) 1.1f else 1f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            ),
                            label = "indicatorScale"
                        )

                        Box(
                            modifier = Modifier
                                .padding(horizontal = 2.dp)
                                .size(width = 60.dp, height = 30.dp), // Increased size to prevent glow clipping
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier.scale(indicatorScale),
                                contentAlignment = Alignment.Center // HIER: Zentriert den Indikator im Glow-Bereich
                            ) {
                                // Glow effect for selected indicator
                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .size(width = width + 20.dp, height = height + 20.dp)
                                            .blur(
                                                radius = 12.dp,
                                                edgeTreatment = BlurredEdgeTreatment.Unbounded
                                            )
                                            .background(
                                                color = page.mainColor.copy(alpha = 0.6f),
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .size(width = width, height = height)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            if (isSelected) {
                                                Brush.horizontalGradient(
                                                    colors = listOf(
                                                        page.mainColor,
                                                        page.mainColor.copy(alpha = 0.8f)
                                                    )
                                                )
                                            } else {
                                                Brush.horizontalGradient(
                                                    colors = listOf(
                                                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                                    )
                                                )
                                            }
                                        )
                                )
                            }
                        }
                    }
                }

                // Navigation Buttons with enhanced styling
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 4.dp), // Extra Padding für Button-Glow/Scale
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back button with fade animation
                    AnimatedVisibility(
                        visible = currentPageIndex > 0,
                        enter = fadeIn(tween(200)) + slideInHorizontally { -it },
                        exit = fadeOut(tween(150)) + slideOutHorizontally { -it }
                    ) {
                        FilledTonalButton(
                            onClick = {
                                performHapticFeedback(view, HapticType.LIGHT)
                                currentPageIndex--
                            },
                            shape = RoundedCornerShape(16.dp),
                            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Back", style = MaterialTheme.typography.labelLarge)
                        }
                    }

                    if (currentPageIndex == 0) {
                        Spacer(Modifier.width(8.dp))
                    }

                    val isLastPage = currentPageIndex == pages.size - 1

                    // "Let's Go" button pulse animation on last page
                    val buttonPulse = remember { Animatable(1f) }
                    LaunchedEffect(isLastPage) {
                        if (isLastPage) {
                            buttonPulse.animateTo(
                                targetValue = 1.05f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(800, easing = FastOutSlowInEasing),
                                    repeatMode = RepeatMode.Reverse
                                )
                            )
                        } else {
                            buttonPulse.snapTo(1f)
                        }
                    }

                    Button(
                        onClick = {
                            performHapticFeedback(view, if (isLastPage) HapticType.CONFIRM else HapticType.LIGHT)
                            if (!isLastPage) {
                                currentPageIndex++
                            } else {
                                showExplosion = true
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(horizontal = 28.dp, vertical = 14.dp),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = if (isLastPage) 8.dp else 4.dp,
                            pressedElevation = 12.dp
                        ),
                        colors = if (isLastPage) {
                            ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            ButtonDefaults.buttonColors()
                        },
                        modifier = Modifier
                            .scale(buttonPulse.value)
                            .then(
                                if (isLastPage) Modifier.shimmerEffect(durationMillis = 1500) else Modifier
                            )
                    ) {
                        Text(
                            if (!isLastPage) "Next" else "Let's Go!",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.width(8.dp))
                        Icon(
                            imageVector = if (!isLastPage)
                                Icons.AutoMirrored.Filled.ArrowForward
                            else
                                Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Celebration Explosion Overlay with Confetti
            /* This block is moved inside the Surface's content Box */
        }
    }
}


@Preview
@Composable
fun WelcomeDialogPreview() {
    MaterialTheme {
        WelcomeDialog(onDismiss = {})
    }
}
