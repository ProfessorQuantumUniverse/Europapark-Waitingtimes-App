package com.quantum_prof.eurpaparkwaittimes

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.net.toUri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.outlined.BatterySaver
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.quantum_prof.eurpaparkwaittimes.data.AttractionWaitTime
import com.quantum_prof.eurpaparkwaittimes.ui.theme.*
import com.quantum_prof.eurpaparkwaittimes.ui.theme.components.*
import com.quantum_prof.eurpaparkwaittimes.ui.theme.components.HapticType
import com.quantum_prof.eurpaparkwaittimes.ui.theme.components.performHapticFeedback
import com.quantum_prof.eurpaparkwaittimes.ui.theme.main.MainViewModel
import com.quantum_prof.eurpaparkwaittimes.ui.theme.main.SortDirection
import com.quantum_prof.eurpaparkwaittimes.ui.theme.main.SortType
import com.quantum_prof.eurpaparkwaittimes.ui.theme.main.WaitTimeUiState
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ ->
        // Permission result handling
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkNotificationPermission()

        setContent {
            val performanceModeState = rememberPerformanceModeState()
            EuropaparkWaitTimesTheme(performanceModeState = performanceModeState) {
                WaitTimeApp(performanceModeState = performanceModeState)
            }
        }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Permission already granted - no action needed
                }
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }
}

@DrawableRes
fun getAttractionIconResId(code: String): Int {
    return when (code) {
        // Coasters
        "383533", "383530", "323530", "323030", "323037", "353030", "373031", "333531", "343033", "333530", "393030", "313537", "343434" -> R.drawable.ic_coaster
        // Water rides
        "373030", "383030", "343030", "363530", "383531", "363030" -> R.drawable.ic_waterride
        // Family/Child rides
        "343034", "313030", "353530", "323031", "323032", "313032", "343935", "313539", "373533", "313535", "393031", "363531", "373032", "39", "323034", "363635", "383135", "343731" -> R.drawable.ic_childride
        else -> R.drawable.ic_default_ride
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaitTimeApp(
    performanceModeState: PerformanceModeState = rememberPerformanceModeState(),
    viewModel: MainViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showSortMenu by remember { mutableStateOf(false) }
    var showNotificationDialogFor by remember { mutableStateOf<AttractionWaitTime?>(null) }

    if (uiState.showWelcomeDialog) {
        WelcomeDialog(
            onDismiss = { viewModel.dismissWelcomeDialog() }
        )
    }

    // 🔋 Snackbar für Performance-Modus Feedback
    val snackbarHostState = remember { SnackbarHostState() }
    var lastPerformanceModeState by remember { mutableStateOf(performanceModeState.isEnabled) }

    // Zeige Snackbar wenn Performance-Modus geändert wird
    LaunchedEffect(performanceModeState.isEnabled) {
        if (performanceModeState.isEnabled != lastPerformanceModeState) {
            lastPerformanceModeState = performanceModeState.isEnabled
            val message = if (performanceModeState.isEnabled) {
                "🔋 Energiesparmodus aktiviert – Animationen deaktiviert"
            } else {
                "✨ Energiesparmodus deaktiviert – Animationen aktiv"
            }
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Original Hintergrundbild - gedimmt im Performance-Modus
        Image(
            painter = painterResource(id = R.drawable.background_park),
            contentDescription = "Park Hintergrund",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = if (performanceModeState.isEnabled) 0.3f else 1f
        )

        // Modern Gradient Overlay - stärker im Performance-Modus
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = if (performanceModeState.isEnabled) {
                            // Stärkerer Overlay im Energiesparmodus für OLED-Effizienz
                            listOf(
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                            )
                        } else {
                            listOf(
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.75f)
                            )
                        }
                    )
                )
        )

        // Main content
        Scaffold(
            modifier = Modifier.statusBarsPadding(),
            topBar = {
                ImprovedTopAppBar(
                    uiState = uiState,
                    showSortMenu = showSortMenu,
                    onShowSortMenuChange = { showSortMenu = it },
                    onSortDirectionToggle = { viewModel.toggleSortDirection() },
                    onSortTypeChange = { sortType ->
                        viewModel.changeSortOrder(sortType, uiState.currentSortDirection)
                    }
                )
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState) { data ->
                    Snackbar(
                        snackbarData = data,
                        containerColor = if (performanceModeState.isEnabled)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.inverseSurface,
                        contentColor = if (performanceModeState.isEnabled)
                            MaterialTheme.colorScheme.onPrimaryContainer
                        else
                            MaterialTheme.colorScheme.inverseOnSurface,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            WaitTimeContent(
                uiState = uiState,
                onRefresh = { viewModel.fetchWaitTimes(isRefresh = true) },
                onFavoriteToggle = { code -> viewModel.toggleFavorite(code) },
                onFilterOnlyOpenChanged = { enabled -> viewModel.setFilterOnlyOpen(enabled) },
                onAddAlertClicked = { attraction -> showNotificationDialogFor = attraction },
                onRemoveAlert = { code -> viewModel.removeAlert(code) },
                onSearchQueryChanged = { query -> viewModel.setSearchQuery(query) },
                modifier = Modifier.padding(paddingValues)
            )
        }

        // Alert dialog
        showNotificationDialogFor?.let { attraction ->
            val currentAlert = uiState.activeAlerts.find { it.attractionCode == attraction.code }
            WaitTimeAlertDialog(
                attraction = attraction,
                currentAlert = currentAlert,
                onDismiss = { showNotificationDialogFor = null },
                onSetAlert = { targetTime ->
                    viewModel.addAlert(attraction, targetTime)
                },
                onRemoveAlert = {
                    viewModel.removeAlert(attraction.code)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImprovedTopAppBar(
    uiState: WaitTimeUiState,
    showSortMenu: Boolean,
    onShowSortMenuChange: (Boolean) -> Unit,
    onSortDirectionToggle: () -> Unit,
    onSortTypeChange: (SortType) -> Unit,
    modifier: Modifier = Modifier
) {
    // 🔋 Get the performance mode state from CompositionLocal
    val performanceMode = LocalPerformanceMode.current

    // Modern TopAppBar with Glass Effect
    Box(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .glassBackground(
                shape = RoundedCornerShape(28.dp),
                tintColor = MaterialTheme.colorScheme.surface,
                alpha = 0.7f
            )
    ) {
        TopAppBar(
            title = {
                Column {
                    Text(
                        "Europapark",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1
                    )
                    Text(
                        "Queue Times",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                }
            },

            actions = {
                val view = LocalView.current
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    // 🔋 Battery Save Mode Toggle mit Badge
                    Box {
                        IconButton(
                            onClick = {
                                performHapticFeedback(
                                    view,
                                    if (performanceMode.isEnabled) HapticType.TOGGLE_OFF else HapticType.TOGGLE_ON
                                )
                                performanceMode.toggle()
                            },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = if (performanceMode.isEnabled)
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f)
                                else
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            )
                        ) {
                            Icon(
                                imageVector = if (performanceMode.isEnabled)
                                    Icons.Filled.BatteryFull
                                else
                                    Icons.Outlined.BatterySaver,
                                contentDescription = if (performanceMode.isEnabled)
                                    "Energiesparmodus deaktivieren"
                                else
                                    "Energiesparmodus aktivieren",
                                tint = if (performanceMode.isEnabled)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // 🔋 Badge wenn Energiesparmodus aktiv
                        if (performanceMode.isEnabled) {
                            Badge(
                                containerColor = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = (-2).dp, y = 2.dp)
                            ) {
                                Text(
                                    text = "ON",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Sort Direction Toggle Button with animated arrow
                    IconButton(
                        onClick = onSortDirectionToggle,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier.bouncyClick(onClick = onSortDirectionToggle)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_sort_arrow),
                            contentDescription = when (uiState.currentSortDirection) {
                                SortDirection.ASCENDING -> "Sort Ascending"
                                SortDirection.DESCENDING -> "Sort Descending"
                            },
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.rotate(
                                when (uiState.currentSortDirection) {
                                    SortDirection.ASCENDING -> 180f // Pfeil nach oben
                                    SortDirection.DESCENDING -> 0f  // Pfeil nach unten
                                }
                            )
                        )
                    }

                    // Sort Options Menu Button
                    IconButton(
                        onClick = { onShowSortMenuChange(!showSortMenu) },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier.bouncyClick(onClick = { onShowSortMenuChange(!showSortMenu) })
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Sort,
                            contentDescription = "Sort Options",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                DropdownMenu(
                    expanded = showSortMenu,
                    onDismissRequest = { onShowSortMenuChange(false) },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
                ) {
                    // Sort Type Options
                    SortType.entries.forEach { sortType ->
                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Zeige Checkmark für aktuell ausgewählten Sort Type
                                    if (sortType == uiState.currentSortType) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    } else {
                                        Spacer(modifier = Modifier.size(16.dp))
                                    }

                                    Text(
                                        when (sortType) {
                                            SortType.NAME -> "Nach Name"
                                            SortType.WAIT_TIME -> "Nach Wartezeit"
                                        },
                                        color = if (sortType == uiState.currentSortType) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            MaterialTheme.colorScheme.onSurface
                                        }
                                    )
                                }
                            },
                            onClick = {
                                performHapticFeedback(view, HapticType.TICK)
                                onSortTypeChange(sortType)
                                onShowSortMenuChange(false)
                            }
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                titleContentColor = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier.padding(horizontal = 8.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaitTimeContent(
    uiState: WaitTimeUiState,
    onRefresh: () -> Unit,
    onFavoriteToggle: (String) -> Unit,
    onFilterOnlyOpenChanged: (Boolean) -> Unit,
    onAddAlertClicked: (AttractionWaitTime) -> Unit,
    onRemoveAlert: (String) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val pullToRefreshState = rememberPullToRefreshState()

    Column(modifier = modifier.fillMaxSize()) {
        // Active Alerts Panel state
        var showActiveAlerts by remember { mutableStateOf(false) }
        var isSearchExpanded by remember { mutableStateOf(false) }

        // Filter controls with search
        if (uiState.waitTimes.isNotEmpty() || (uiState.error == null && !uiState.isLoading)) {
            ModernFilterControls(
                filterOnlyOpen = uiState.filterOnlyOpen,
                onFilterOnlyOpenChanged = onFilterOnlyOpenChanged,
                activeAlertsCount = uiState.activeAlerts.size,
                showActiveAlerts = showActiveAlerts,
                onToggleActiveAlerts = { showActiveAlerts = !showActiveAlerts },
                isSearchExpanded = isSearchExpanded,
                onSearchToggle = { isSearchExpanded = !isSearchExpanded },
                searchQuery = uiState.searchQuery,
                onSearchQueryChanged = onSearchQueryChanged
            )
        }

        // Active Alerts Panel - show when there are active alerts and expanded
        if (uiState.activeAlerts.isNotEmpty() && showActiveAlerts) {
            ActiveAlertsPanel(
                alerts = uiState.activeAlerts,
                waitTimes = uiState.waitTimes,
                onEditAlert = onAddAlertClicked,
                onRemoveAlert = onRemoveAlert,
                onCollapse = { showActiveAlerts = false }
            )
        }

        // Main content
        when {
            uiState.error != null && uiState.waitTimes.isEmpty() && !uiState.isLoading -> {
                ModernErrorView(
                    errorMessage = uiState.error,
                    onRetry = onRefresh,
                    modifier = Modifier.weight(1f)
                )
            }

            uiState.waitTimes.isEmpty() && !uiState.isLoading -> {
                ModernEmptyView(
                    title = if (uiState.filterOnlyOpen) "All Attractions are closed!" else "No Data Available",
                    subtitle = "Try refreshing or later again. Check your internet connection.",
                    modifier = Modifier.weight(1f)
                )
            }

            uiState.isLoading && uiState.waitTimes.isEmpty() -> {
                ModernLoadingView(
                    modifier = Modifier.weight(1f)
                )
            }

            else -> {
                val refreshView = LocalView.current
                PullToRefreshBox(
                    isRefreshing = uiState.isLoading,
                    onRefresh = {
                        performHapticFeedback(refreshView, HapticType.HEAVY)
                        onRefresh()
                    },
                    state = pullToRefreshState,
                    modifier = Modifier.weight(1f)
                ) {
                    val lazyListState = rememberLazyListState()

                    LazyColumn(
                        state = lazyListState,
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (uiState.crowdLevel != null) {
                            item(key = "crowd_level") {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .zoomOutOnScroll(lazyListState = lazyListState),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CrowdLevelIndicator(
                                        crowdLevel = uiState.crowdLevel,
                                        allAttractionsClosed = uiState.allAttractionsClosed,
                                        modifier = Modifier
                                            .slideInFromBottom(0)
                                            .floatingAnimation(amplitude = 2f, duration = 4000)
                                    )
                                }
                            }
                        }

                        if (uiState.lastUpdated > 0) {
                            item(key = "last_updated") {
                                ModernLastUpdatedHeader(
                                    timestamp = uiState.lastUpdated,
                                    isOffline = uiState.isOfflineData,
                                    modifier = Modifier.slideInFromBottom(0)
                                )
                            }
                        }

                        items(
                            items = uiState.waitTimes,
                            key = { attraction -> attraction.code }
                        ) { attraction ->
                            // Use remember to avoid recomposition on scroll
                            val isFavorite = remember(uiState.favoriteCodes, attraction.code) {
                                attraction.code in uiState.favoriteCodes
                            }
                            val currentAlert = remember(uiState.activeAlerts, attraction.code) {
                                uiState.activeAlerts.find { it.attractionCode == attraction.code }
                            }
                            val hasAlert = remember(uiState.activeAlerts, attraction.code) {
                                uiState.activeAlerts.any { it.attractionCode == attraction.code }
                            }

                            WaitTimeCard(
                                attraction = attraction,
                                isFavorite = isFavorite,
                                hasAlert = hasAlert,
                                onFavoriteToggle = { onFavoriteToggle(attraction.code) },
                                onAlertClick = { onAddAlertClicked(attraction) },
                                currentAlert = currentAlert,
                                modifier = Modifier.animateItem()
                            )
                        }

                        item(key = "footer") {
                            ModernFooter()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ModernFilterControls(
    filterOnlyOpen: Boolean,
    onFilterOnlyOpenChanged: (Boolean) -> Unit,
    activeAlertsCount: Int,
    showActiveAlerts: Boolean,
    onToggleActiveAlerts: () -> Unit,
    isSearchExpanded: Boolean,
    onSearchToggle: () -> Unit,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Filter Chip with Glass Effect and bouncy click
            Box(
                modifier = Modifier
                    .bouncyClick(onClick = { onFilterOnlyOpenChanged(!filterOnlyOpen) })
                    .glassBackground(
                        shape = RoundedCornerShape(12.dp),
                        tintColor = if (filterOnlyOpen) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                        alpha = if (filterOnlyOpen) 0.8f else 0.5f,
                        strokeColor = if (filterOnlyOpen) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.3f)
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = if (filterOnlyOpen) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Nur Geöffnete",
                        style = MaterialTheme.typography.labelLarge,
                        color = if (filterOnlyOpen) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Search Button with bouncy click
            Box(
                modifier = Modifier
                    .bouncyClick(onClick = onSearchToggle)
                    .glassBackground(
                        shape = RoundedCornerShape(12.dp),
                        tintColor = if (isSearchExpanded) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                        alpha = if (isSearchExpanded) 0.8f else 0.5f,
                        strokeColor = if (isSearchExpanded) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.3f)
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Icon(
                    imageVector = if (isSearchExpanded) Icons.Default.Close else Icons.Default.Search,
                    contentDescription = if (isSearchExpanded) "Suche schließen" else "Suchen",
                    modifier = Modifier.size(18.dp),
                    tint = if (isSearchExpanded) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Active Alerts Button with Glass Effect and bouncy click
            if (activeAlertsCount > 0) {
                Box(
                    modifier = Modifier
                        .bouncyClick(onClick = onToggleActiveAlerts)
                        .glassBackground(
                            shape = RoundedCornerShape(12.dp),
                            tintColor = if (showActiveAlerts) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                            alpha = 0.7f
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (showActiveAlerts) Icons.Default.NotificationsOff else Icons.Default.Notifications,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Alerts",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Badge(
                            containerColor = MaterialTheme.colorScheme.error,
                        ) {
                            Text(
                                text = activeAlertsCount.toString(),
                                color = MaterialTheme.colorScheme.onError,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // Search Bar (animated expansion with spring animation)
        androidx.compose.animation.AnimatedVisibility(
            visible = isSearchExpanded,
            enter = androidx.compose.animation.expandVertically(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            ) + androidx.compose.animation.fadeIn(),
            exit = androidx.compose.animation.shrinkVertically() + androidx.compose.animation.fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .glassBackground(
                        shape = RoundedCornerShape(16.dp),
                        tintColor = MaterialTheme.colorScheme.surface,
                        alpha = 0.8f
                    )
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChanged,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    placeholder = {
                        Text(
                            "Attraktion suchen...",
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchQueryChanged("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Löschen",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )
            }
        }
    }
}

@Composable
fun ModernLastUpdatedHeader(
    timestamp: Long,
    isOffline: Boolean,
    modifier: Modifier = Modifier
) {
    val performanceMode = LocalPerformanceMode.current
    val minutesAgo = TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - timestamp)
    val timeFormatted = SimpleDateFormat("HH:mm", Locale.GERMANY).format(Date(timestamp))

    val ageText = when {
        minutesAgo < 1 -> "Gerade eben"
        minutesAgo == 1L -> "Vor 1 Minute"
        minutesAgo < 60 -> "Vor $minutesAgo Minuten"
        minutesAgo < 120 -> "Vor 1 Stunde"
        else -> "Vor ${TimeUnit.MINUTES.toHours(minutesAgo)} Stunden"
    }

    // ✨ Subtle pulsing animation for the cloud icon
    // 🔋 Deaktiviert im Battery-Save-Modus
    val iconAlpha = if (performanceMode.isEnabled) {
        0.8f // Statischer Wert im Performance-Modus
    } else {
        val infiniteTransition = rememberInfiniteTransition(label = "cloud_animation")
        val animatedAlpha by infiniteTransition.animateFloat(
            initialValue = 0.6f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = EaseInOutSine),
                repeatMode = RepeatMode.Reverse
            ),
            label = "cloud_alpha"
        )
        animatedAlpha
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = if (isOffline) Icons.Default.CloudOff else Icons.Default.CloudDone,
            contentDescription = null,
            tint = if (isOffline) MaterialTheme.colorScheme.error.copy(alpha = iconAlpha)
                   else MaterialTheme.colorScheme.primary.copy(alpha = iconAlpha * 0.7f),
            modifier = Modifier.size(14.dp)
        )

        Spacer(modifier = Modifier.width(6.dp))

        Text(
            text = "${if (isOffline) "Offline • " else ""}$ageText ($timeFormatted)",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun ModernFooter(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val performanceMode = LocalPerformanceMode.current
    val apiUrl = "https://www.wartezeiten.app/"

    // ✨ Subtle breathing animation for living footer
    // 🔋 Deaktiviert im Battery-Save-Modus
    val breathingAlpha = if (performanceMode.isEnabled) {
        0.65f // Statischer Wert im Performance-Modus
    } else {
        val infiniteTransition = rememberInfiniteTransition(label = "footer_animation")
        val animatedAlpha by infiniteTransition.animateFloat(
            initialValue = 0.5f,
            targetValue = 0.8f,
            animationSpec = infiniteRepeatable(
                animation = tween(3000, easing = EaseInOutSine),
                repeatMode = RepeatMode.Reverse
            ),
            label = "footer_breathing"
        )
        animatedAlpha
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp)
            .bouncyClick(onClick = {
                val intent = Intent(Intent.ACTION_VIEW, apiUrl.toUri())
                try {
                    context.startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Daten bereitgestellt von",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = breathingAlpha * 0.7f)
        )

        Text(
            text = "wartezeiten.app",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary.copy(alpha = breathingAlpha),
            textDecoration = TextDecoration.Underline
        )
    }
}
