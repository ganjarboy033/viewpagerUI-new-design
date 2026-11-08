package com.example.viewpagertransformers

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ViewCarousel
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewpagertransformers.localization.AppStrings
import com.example.viewpagertransformers.model.AppSettings
import com.example.viewpagertransformers.model.CardStyle
import com.example.viewpagertransformers.model.PageTransformerType
import com.example.viewpagertransformers.model.PagerOrientation
import com.example.viewpagertransformers.transformer.applyPageTransformer
import com.example.viewpagertransformers.ui.components.AnimationControlPanel
import com.example.viewpagertransformers.ui.components.ControlMode
import com.example.viewpagertransformers.ui.components.DebugLogEntry
import com.example.viewpagertransformers.ui.components.DebugPanelSheet
import com.example.viewpagertransformers.ui.components.DeveloperInfoSheet
import com.example.viewpagertransformers.ui.components.PageItemContent
import com.example.viewpagertransformers.ui.components.SettingsSheet
import com.example.viewpagertransformers.ui.components.TransformerHeaderBar
import com.example.viewpagertransformers.ui.components.TransformerInfoSheet
import com.example.viewpagertransformers.ui.theme.ViewPagerTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ViewPagerTheme {
                ViewPagerTransformersApp()
            }
        }
    }
}

private const val TOTAL_PAGE_COUNT = 5

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewPagerTransformersApp() {
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { TOTAL_PAGE_COUNT })
    val coroutineScope = rememberCoroutineScope()

    var settings by remember { mutableStateOf(AppSettings()) }
    val strings = remember(settings.language) { AppStrings.get(settings.language) }

    var currentTransformer by remember { mutableStateOf(PageTransformerType.DEFAULT) }
    var controlMode by remember { mutableStateOf(ControlMode.SWIPE) }
    var scrubOffset by remember { mutableFloatStateOf(0.0f) }
    var isPlaying by remember { mutableStateOf(false) }
    var playSpeedMs by remember { mutableLongStateOf(2200L) }

    var showInfoSheet by remember { mutableStateOf(false) }
    var showDebugPanel by remember { mutableStateOf(false) }
    var showDeveloperInfo by remember { mutableStateOf(false) }
    var showSettingsSheet by remember { mutableStateOf(false) }

    val isModernTheme = settings.cardStyle == CardStyle.MODERN

    // Event logs history for debug inspector
    var eventLogs by remember {
        mutableStateOf(
            listOf(
                DebugLogEntry(message = "App siap dina Basa Sunda. Transformer: DEFAULT"),
                DebugLogEntry(message = "Total kaca: $TOTAL_PAGE_COUNT kaca.")
            )
        )
    }

    fun logEvent(message: String, level: String = "INFO") {
        eventLogs = (eventLogs + DebugLogEntry(level = level, message = message)).takeLast(50)
    }

    // Monitor pager page changes
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            logEvent("Kaca aktip ngeser ka: ${page + 1}")
        }
    }

    // Auto-play loop
    LaunchedEffect(isPlaying, playSpeedMs, controlMode) {
        if (controlMode == ControlMode.AUTOPLAY && isPlaying) {
            logEvent("Puter otomatis dimimitian interval ${playSpeedMs}ms")
            while (isActive && isPlaying) {
                delay(playSpeedMs)
                val nextPage = (pagerState.currentPage + 1) % TOTAL_PAGE_COUNT
                pagerState.animateScrollToPage(
                    page = nextPage,
                    animationSpec = tween(durationMillis = (playSpeedMs * 0.4).toInt())
                )
            }
        } else if (!isPlaying && controlMode == ControlMode.AUTOPLAY) {
            logEvent("Puter otomatis eureun/reureuh")
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("app_scaffold"),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.ViewCarousel,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = strings.appTitle,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = strings.appSubtitle,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )
                        }
                    }
                },
                actions = {
                    // Developer & Credits Info button
                    IconButton(
                        onClick = {
                            logEvent("Muka inpormasi pamekar & kredit")
                            showDeveloperInfo = true
                        },
                        modifier = Modifier
                            .testTag("btn_open_developer_info")
                            .size(36.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(32.dp)) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = strings.infoButton,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(19.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // Settings trigger button
                    IconButton(
                        onClick = {
                            logEvent("Muka panel setélan & pangaturan")
                            showSettingsSheet = true
                        },
                        modifier = Modifier
                            .testTag("btn_open_settings")
                            .size(36.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.14f)
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(32.dp)) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = strings.settingsButton,
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(19.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // Debug Panel trigger button
                    IconButton(
                        onClick = {
                            logEvent("Muka panel debug & diagnostik")
                            showDebugPanel = true
                        },
                        modifier = Modifier
                            .testTag("btn_open_debug_panel")
                            .padding(end = 4.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF10B981).copy(alpha = 0.15f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.BugReport,
                                    contentDescription = strings.debugButton,
                                    tint = Color(0xFF10B981),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = strings.debugButton,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF10B981)
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Transformer Header / Dropdown Selector
            TransformerHeaderBar(
                currentTransformer = currentTransformer,
                onSelectTransformer = {
                    currentTransformer = it
                    logEvent("Gaya pindah kaca digentos ka: ${it.title} (${it.category.label})")
                },
                onOpenInfo = {
                    logEvent("Ningali kodé implementasi transformer: ${currentTransformer.title}")
                    showInfoSheet = true
                },
                language = settings.language
            )

            // Pager Preview Arena (Supports Horizontal and Vertical)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                if (controlMode == ControlMode.SCRUBBER) {
                    // Scrubber manual frame inspect mode
                    Box(modifier = Modifier.fillMaxSize()) {
                        val pageIdx = pagerState.currentPage
                        PageItemContent(
                            pageIndex = pageIdx,
                            positionOffset = scrubOffset,
                            isModernTheme = isModernTheme,
                            modifier = Modifier
                                .fillMaxSize()
                                .applyPageTransformer(
                                    type = currentTransformer,
                                    position = scrubOffset,
                                    cameraMultiplier = settings.cameraDepth.value
                                )
                        )

                        if (scrubOffset > 0.05f) {
                            val prevIdx = if (pageIdx > 0) pageIdx - 1 else TOTAL_PAGE_COUNT - 1
                            PageItemContent(
                                pageIndex = prevIdx,
                                positionOffset = scrubOffset - 1f,
                                isModernTheme = isModernTheme,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .applyPageTransformer(
                                        type = currentTransformer,
                                        position = scrubOffset - 1f,
                                        cameraMultiplier = settings.cameraDepth.value
                                    )
                            )
                        } else if (scrubOffset < -0.05f) {
                            val nextIdx = (pageIdx + 1) % TOTAL_PAGE_COUNT
                            PageItemContent(
                                pageIndex = nextIdx,
                                positionOffset = scrubOffset + 1f,
                                isModernTheme = isModernTheme,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .applyPageTransformer(
                                        type = currentTransformer,
                                        position = scrubOffset + 1f,
                                        cameraMultiplier = settings.cameraDepth.value
                                    )
                            )
                        }
                    }
                } else {
                    // Live interactive Pager with configurable orientation & spacing
                    if (settings.orientation == PagerOrientation.HORIZONTAL) {
                        HorizontalPager(
                            state = pagerState,
                            pageSpacing = settings.pageSpacingDp.dp,
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag("viewpager_horizontal_pager")
                        ) { page ->
                            val pageOffset = (page - pagerState.currentPage) - pagerState.currentPageOffsetFraction

                            PageItemContent(
                                pageIndex = page,
                                positionOffset = pageOffset,
                                isModernTheme = isModernTheme,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .applyPageTransformer(
                                        type = currentTransformer,
                                        position = pageOffset,
                                        cameraMultiplier = settings.cameraDepth.value
                                    )
                            )
                        }
                    } else {
                        VerticalPager(
                            state = pagerState,
                            pageSpacing = settings.pageSpacingDp.dp,
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag("viewpager_vertical_pager")
                        ) { page ->
                            val pageOffset = (page - pagerState.currentPage) - pagerState.currentPageOffsetFraction

                            PageItemContent(
                                pageIndex = page,
                                positionOffset = pageOffset,
                                isModernTheme = isModernTheme,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .applyPageTransformer(
                                        type = currentTransformer,
                                        position = pageOffset,
                                        cameraMultiplier = settings.cameraDepth.value
                                    )
                            )
                        }
                    }
                }
            }

            // Bottom Control Panel
            AnimationControlPanel(
                currentPage = pagerState.currentPage,
                pageCount = TOTAL_PAGE_COUNT,
                controlMode = controlMode,
                onChangeControlMode = { newMode ->
                    controlMode = newMode
                    logEvent("Mode kontrol digentos: ${newMode.name}")
                    if (newMode == ControlMode.SCRUBBER) {
                        isPlaying = false
                        scrubOffset = 0f
                    }
                },
                scrubOffset = scrubOffset,
                onScrubOffsetChange = {
                    scrubOffset = it
                },
                isPlaying = isPlaying,
                onTogglePlay = {
                    isPlaying = !isPlaying
                },
                playSpeedMs = playSpeedMs,
                onChangePlaySpeed = {
                    playSpeedMs = it
                    logEvent("Laju puter otomatis dirobah janten: ${it}ms")
                },
                isModernTheme = isModernTheme,
                onToggleModernTheme = {
                    val newStyle = if (settings.cardStyle == CardStyle.MODERN) CardStyle.CLASSIC else CardStyle.MODERN
                    settings = settings.copy(cardStyle = newStyle)
                    logEvent("Gaya kartu dirobah: ${newStyle.name}")
                },
                onPrevPage = {
                    if (pagerState.currentPage > 0) {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        }
                    }
                },
                onNextPage = {
                    if (pagerState.currentPage < TOTAL_PAGE_COUNT - 1) {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                },
                onSelectPage = { targetPage ->
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(targetPage)
                    }
                },
                language = settings.language
            )
        }
    }

    if (showInfoSheet) {
        TransformerInfoSheet(
            transformer = currentTransformer,
            onDismiss = { showInfoSheet = false }
        )
    }

    if (showSettingsSheet) {
        SettingsSheet(
            settings = settings,
            onUpdateSettings = { newSettings ->
                settings = newSettings
                logEvent("Setélan diropéa: ${newSettings.language.displayName}, ${newSettings.cardStyle.name}, ${newSettings.orientation.name}")
            },
            currentTransformer = currentTransformer,
            onDismiss = { showSettingsSheet = false }
        )
    }

    if (showDebugPanel) {
        val currentOffsetFraction = pagerState.currentPageOffsetFraction
        val currentComputed = if (controlMode == ControlMode.SCRUBBER) {
            scrubOffset
        } else {
            -currentOffsetFraction
        }

        DebugPanelSheet(
            currentTransformer = currentTransformer,
            currentPage = pagerState.currentPage,
            pageOffsetFraction = currentOffsetFraction,
            computedOffset = currentComputed,
            controlMode = controlMode,
            isModernTheme = isModernTheme,
            language = settings.language,
            eventLogs = eventLogs,
            onClearLogs = {
                eventLogs = emptyList()
                logEvent("Log kagiatan parantos dibersihan")
            },
            onRunStressTest = {
                coroutineScope.launch {
                    logEvent(">>> Ngamimitian Uji Katahanan 18 Transformer...", "TEST")
                    PageTransformerType.entries.forEachIndexed { idx, type ->
                        currentTransformer = type
                        pagerState.animateScrollToPage(
                            page = (idx % TOTAL_PAGE_COUNT),
                            animationSpec = tween(durationMillis = 150)
                        )
                        delay(200)
                    }
                    logEvent("<<< Uji Katahanan 18 Transformer rengse teu aya masalah!", "SUCCESS")
                }
            },
            onOpenDeveloperInfo = {
                showDebugPanel = false
                showDeveloperInfo = true
            },
            onDismiss = { showDebugPanel = false }
        )
    }

    if (showDeveloperInfo) {
        DeveloperInfoSheet(
            language = settings.language,
            onDismiss = { showDeveloperInfo = false }
        )
    }
}
