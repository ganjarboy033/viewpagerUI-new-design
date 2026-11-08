package com.example.viewpagertransformers.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewpagertransformers.localization.AppStrings
import com.example.viewpagertransformers.model.AppLanguage
import com.example.viewpagertransformers.model.PageTransformerType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.absoluteValue

data class DebugLogEntry(
    val timestamp: String = SimpleDateFormat("HH:mm:ss.SSS", Locale.US).format(Date()),
    val level: String = "INFO",
    val message: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebugPanelSheet(
    currentTransformer: PageTransformerType,
    currentPage: Int,
    pageOffsetFraction: Float,
    computedOffset: Float,
    controlMode: ControlMode,
    isModernTheme: Boolean,
    language: AppLanguage = AppLanguage.SUNDANESE,
    eventLogs: List<DebugLogEntry>,
    onClearLogs: () -> Unit,
    onRunStressTest: () -> Unit,
    onOpenDeveloperInfo: (() -> Unit)? = null,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val strings = AppStrings.get(language)
    val config = LocalConfiguration.current
    val density = LocalDensity.current

    // Memory info
    val runtime = Runtime.getRuntime()
    val totalMemoryMb = (runtime.totalMemory() / (1024 * 1024)).toInt()
    val freeMemoryMb = (runtime.freeMemory() / (1024 * 1024)).toInt()
    val usedMemoryMb = totalMemoryMb - freeMemoryMb
    val maxMemoryMb = (runtime.maxMemory() / (1024 * 1024)).toInt()
    val memoryUsageFraction = if (maxMemoryMb > 0) usedMemoryMb.toFloat() / maxMemoryMb else 0f

    // Estimated matrix transforms for current frame
    val estimatedScale = when (currentTransformer) {
        PageTransformerType.ACCORDION -> (if (computedOffset < 0f) 1f + computedOffset else 1f - computedOffset).coerceIn(0f, 1f)
        PageTransformerType.BACKGROUND_TO_FOREGROUND -> (if (computedOffset < 0f) 1f else 1f - computedOffset).coerceIn(0.5f, 1f)
        PageTransformerType.FOREGROUND_TO_BACKGROUND -> (if (computedOffset > 0f) 1f else 1f + computedOffset).coerceIn(0.5f, 1f)
        PageTransformerType.ZOOM_IN -> if (computedOffset < 0f) (computedOffset + 1f).coerceIn(0f, 1f) else (1f - computedOffset).absoluteValue.coerceIn(0f, 1f)
        PageTransformerType.ZOOM_OUT -> (1f - computedOffset.absoluteValue * 0.35f).coerceIn(0.65f, 1f)
        PageTransformerType.ZOOM_OUT_SLIDE -> maxOf(0.85f, 1f - computedOffset.absoluteValue)
        else -> 1.00f
    }
    val estimatedRotationY = when (currentTransformer) {
        PageTransformerType.CUBE_IN -> -90f * computedOffset
        PageTransformerType.CUBE_OUT -> 90f * computedOffset
        PageTransformerType.FLIP_HORIZONTAL -> 180f * computedOffset
        PageTransformerType.TABLET -> (if (computedOffset < 0f) 30f else -30f) * computedOffset.absoluteValue
        else -> 0.0f
    }
    val estimatedRotationX = when (currentTransformer) {
        PageTransformerType.FLIP_VERTICAL -> -180f * computedOffset
        else -> 0.0f
    }
    val estimatedRotationZ = when (currentTransformer) {
        PageTransformerType.ROTATE_DOWN -> 18.75f * computedOffset
        PageTransformerType.ROTATE_UP -> -15f * computedOffset
        else -> 0.0f
    }
    val estimatedAlpha = when (currentTransformer) {
        PageTransformerType.FLIP_HORIZONTAL -> if (estimatedRotationY.absoluteValue > 90f) 0f else 1f
        PageTransformerType.FLIP_VERTICAL -> if (estimatedRotationX.absoluteValue > 90f) 0f else 1f
        PageTransformerType.DEPTH_PAGE -> if (computedOffset > 0f) (1f - computedOffset).coerceIn(0f, 1f) else 1f
        PageTransformerType.ZOOM_IN -> (2f - estimatedScale).coerceIn(0f, 1f)
        PageTransformerType.ZOOM_OUT_SLIDE -> (0.5f + (estimatedScale - 0.85f) / (1f - 0.85f) * 0.5f).coerceIn(0f, 1f)
        else -> if (computedOffset.absoluteValue > 1.01f) 0f else 1f
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        modifier = Modifier.testTag("debug_panel_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF10B981).copy(alpha = 0.2f),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.BugReport,
                                contentDescription = "Debug Icon",
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Panel Debug & Diagnostik",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Real-time Telemetry & Error Inspector",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Copy diagnostics button
                Button(
                    onClick = {
                        val report = buildString {
                            appendLine("=== VIEW PAGER TRANSFORMERS DEBUG REPORT ===")
                            appendLine("Timestamp: ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())}")
                            appendLine()
                            appendLine("[Device & Runtime]")
                            appendLine("Device: ${Build.MANUFACTURER} ${Build.MODEL} (${Build.DEVICE})")
                            appendLine("Android OS: ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})")
                            appendLine("Screen: ${config.screenWidthDp}x${config.screenHeightDp} dp, density: ${density.density}")
                            appendLine("Memory: $usedMemoryMb MB used / $maxMemoryMb MB max (${(memoryUsageFraction * 100).toInt()}%)")
                            appendLine()
                            appendLine("[Pager Telemetry]")
                            appendLine("Transformer: ${currentTransformer.name} (${currentTransformer.title})")
                            appendLine("Category: ${currentTransformer.category.label}")
                            appendLine("Mode: ${controlMode.name}")
                            appendLine("Current Page: $currentPage / 5")
                            appendLine("Page Offset Fraction: ${String.format(Locale.US, "%+.4f", pageOffsetFraction)}")
                            appendLine("Computed Offset: ${String.format(Locale.US, "%+.4f", computedOffset)}")
                            appendLine()
                            appendLine("[Estimated Transform Matrix]")
                            appendLine("Scale: ${String.format(Locale.US, "%.3f", estimatedScale)}")
                            appendLine("Rotation: X=${String.format(Locale.US, "%.1f°", estimatedRotationX)}, Y=${String.format(Locale.US, "%.1f°", estimatedRotationY)}, Z=${String.format(Locale.US, "%.1f°", estimatedRotationZ)}")
                            appendLine("Alpha: ${String.format(Locale.US, "%.2f", estimatedAlpha)}")
                            appendLine("Camera Distance: 14 * density")
                            appendLine()
                            appendLine("[Event Logs (${eventLogs.size} entries)]")
                            eventLogs.takeLast(20).forEach { log ->
                                appendLine("[${log.timestamp}] [${log.level}] ${log.message}")
                            }
                            appendLine("=============================================")
                        }

                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Debug Diagnostics", report)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Laporan diagnostik berhasil disalin ke clipboard!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("btn_copy_diagnostics")
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Salin Log", fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Real-time Telemetry Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "LIVE TELEMETRY",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TelemetryChip(label = "Page", value = "$currentPage / 5")
                        TelemetryChip(label = "Offset Frac", value = String.format(Locale.US, "%+.2f", pageOffsetFraction))
                        TelemetryChip(label = "Computed", value = String.format(Locale.US, "%+.2f", computedOffset))
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TelemetryChip(label = "Scale", value = String.format(Locale.US, "%.2fx", estimatedScale))
                        TelemetryChip(label = "Rot Y", value = String.format(Locale.US, "%.1f°", estimatedRotationY))
                        TelemetryChip(label = "Alpha", value = String.format(Locale.US, "%.2f", estimatedAlpha))
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Memory & System Health Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Memory,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "JVM Memory Heap",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = "$usedMemoryMb MB / $maxMemoryMb MB",
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { memoryUsageFraction },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(CircleShape),
                        color = if (memoryUsageFraction > 0.8f) Color(0xFFEF4444) else MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${Build.MANUFACTURER} ${Build.MODEL} • Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action & Stress Test Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onRunStressTest,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_run_stress_test")
                ) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = "Test",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Stress Test (18x)", fontSize = 11.sp)
                }

                OutlinedButton(
                    onClick = onClearLogs,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_clear_logs")
                ) {
                    Icon(
                        imageVector = Icons.Default.CleaningServices,
                        contentDescription = "Clear",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Bersihkan Log", fontSize = 11.sp)
                }
            }

            if (onOpenDeveloperInfo != null) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = onOpenDeveloperInfo,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_debug_open_developer_info")
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Developer Info",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Identitas Developer & Kredit (Suyono Ion)", fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Event Logs Section
            Text(
                text = "LOG AKTIVITAS & EVENT (${eventLogs.size})",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(6.dp))

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFF0F172A),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(14.dp))
            ) {
                if (eventLogs.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Belum ada event tercatat",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                    ) {
                        items(eventLogs.reversed()) { log ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(
                                    text = log.timestamp,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color(0xFF94A3B8),
                                    modifier = Modifier.width(82.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "[${log.level}]",
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = if (log.level == "ERROR") Color(0xFFF87171) else Color(0xFF38BDF8),
                                    modifier = Modifier.width(52.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = log.message,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color(0xFFF1F5F9),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun TelemetryChip(label: String, value: String) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
        modifier = Modifier.width(96.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
