package com.example.viewpagertransformers.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.SlowMotionVideo
import androidx.compose.material.icons.filled.Swipe
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

import com.example.viewpagertransformers.localization.AppStrings
import com.example.viewpagertransformers.model.AppLanguage

enum class ControlMode(val defaultLabel: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    SWIPE("Swipe", Icons.Default.Swipe),
    SCRUBBER("Scrubber", Icons.Default.Tune),
    AUTOPLAY("Auto Play", Icons.Default.SlowMotionVideo)
}

@Composable
fun AnimationControlPanel(
    currentPage: Int,
    pageCount: Int,
    controlMode: ControlMode,
    onChangeControlMode: (ControlMode) -> Unit,
    scrubOffset: Float,
    onScrubOffsetChange: (Float) -> Unit,
    isPlaying: Boolean,
    onTogglePlay: () -> Unit,
    playSpeedMs: Long,
    onChangePlaySpeed: (Long) -> Unit,
    isModernTheme: Boolean,
    onToggleModernTheme: () -> Unit,
    onPrevPage: () -> Unit,
    onNextPage: () -> Unit,
    onSelectPage: (Int) -> Unit,
    language: AppLanguage = AppLanguage.SUNDANESE,
    modifier: Modifier = Modifier
) {
    val strings = AppStrings.get(language)
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .testTag("control_panel_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Mode Selector Tabs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    ControlMode.entries.forEach { mode ->
                        val modeLabel = when (mode) {
                            ControlMode.SWIPE -> strings.modeSwipe
                            ControlMode.SCRUBBER -> strings.modeScrubber
                            ControlMode.AUTOPLAY -> strings.modeAutoPlay
                        }
                        FilterChip(
                            selected = controlMode == mode,
                            onClick = { onChangeControlMode(mode) },
                            label = { Text(modeLabel, fontSize = 12.sp) },
                            leadingIcon = {
                                Icon(
                                    imageVector = mode.icon,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            modifier = Modifier.testTag("tab_mode_${mode.name.lowercase(Locale.ROOT)}")
                        )
                    }
                }

                // Theme switcher icon button (Classic Holo vs Modern)
                Surface(
                    onClick = onToggleModernTheme,
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.testTag("btn_toggle_card_theme")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ColorLens,
                            contentDescription = "Card Style",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isModernTheme) strings.cardStyleModern else strings.cardStyleClassic,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            when (controlMode) {
                ControlMode.SWIPE -> {
                    // Page navigation buttons and indicators
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        FilledTonalIconButton(
                            onClick = onPrevPage,
                            enabled = currentPage > 0,
                            modifier = Modifier.testTag("btn_page_prev")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Previous Page"
                            )
                        }

                        // Dots
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            for (i in 0 until pageCount) {
                                val isSelected = i == currentPage
                                Box(
                                    modifier = Modifier
                                        .size(if (isSelected) 24.dp else 10.dp, 10.dp)
                                        .clip(RoundedCornerShape(5.dp))
                                        .background(
                                            if (isSelected) {
                                                MaterialTheme.colorScheme.primary
                                            } else {
                                                MaterialTheme.colorScheme.outlineVariant
                                            }
                                        )
                                        .clickable { onSelectPage(i) }
                                        .testTag("page_dot_$i")
                                )
                            }
                        }

                        FilledTonalIconButton(
                            onClick = onNextPage,
                            enabled = currentPage < pageCount - 1,
                            modifier = Modifier.testTag("btn_page_next")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Next Page"
                            )
                        }
                    }
                }

                ControlMode.SCRUBBER -> {
                    // Interactive Offset Scrubber
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = strings.transitionPosition,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = String.format(Locale.US, "%+.2f", scrubOffset),
                                    fontSize = 13.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Slider(
                            value = scrubOffset,
                            onValueChange = onScrubOffsetChange,
                            valueRange = -1f..1f,
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.primary,
                                activeTrackColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("scrubber_slider")
                        )

                        // Quick Snap buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            listOf(-1.0f, -0.5f, 0.0f, 0.5f, 1.0f).forEach { snapVal ->
                                Surface(
                                    onClick = { onScrubOffsetChange(snapVal) },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if ((scrubOffset - snapVal).let { it >= -0.05f && it <= 0.05f }) {
                                        MaterialTheme.colorScheme.secondaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                    },
                                    modifier = Modifier.testTag("snap_btn_${snapVal}")
                                ) {
                                    Text(
                                        text = String.format(Locale.US, "%.1f", snapVal),
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }

                ControlMode.AUTOPLAY -> {
                    // Auto-play controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        FilledTonalButton(
                            onClick = onTogglePlay,
                            modifier = Modifier.testTag("btn_toggle_autoplay")
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isPlaying) strings.btnPause else strings.btnPlay)
                        }

                        // Speed toggles
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf(
                                1200L to "1.2s",
                                2200L to "2.2s",
                                3500L to "3.5s"
                            ).forEach { (speedMs, label) ->
                                Surface(
                                    onClick = { onChangePlaySpeed(speedMs) },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (playSpeedMs == speedMs) {
                                        MaterialTheme.colorScheme.primaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                    },
                                    modifier = Modifier.testTag("btn_speed_$label")
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        color = if (playSpeedMs == speedMs) {
                                            MaterialTheme.colorScheme.onPrimaryContainer
                                        } else {
                                            MaterialTheme.colorScheme.onSurfaceVariant
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
