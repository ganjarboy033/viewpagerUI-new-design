package com.example.viewpagertransformers.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.ViewCarousel
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewpagertransformers.ui.theme.PageColor1
import com.example.viewpagertransformers.ui.theme.PageColor2
import com.example.viewpagertransformers.ui.theme.PageColor3
import com.example.viewpagertransformers.ui.theme.PageColor4
import com.example.viewpagertransformers.ui.theme.PageColor5
import kotlin.math.roundToInt

val PageColors = listOf(PageColor1, PageColor2, PageColor3, PageColor4, PageColor5)

data class ShowcasePageData(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val tag: String,
    val gradientColors: List<Color>,
    val highlights: List<String>
)

val ShowcasePages = listOf(
    ShowcasePageData(
        title = "Fluid Gestures",
        subtitle = "Zero-lag touch tracking with continuous interpolation",
        icon = Icons.Default.TouchApp,
        tag = "Core UX",
        gradientColors = listOf(Color(0xFF0284C7), Color(0xFF0369A1)),
        highlights = listOf("Velocity tracking", "Physics fling", "Predictive back")
    ),
    ShowcasePageData(
        title = "3D Perspectives",
        subtitle = "True camera projection with dynamic depth and matrix tilts",
        icon = Icons.Default.ViewCarousel,
        tag = "Visual FX",
        gradientColors = listOf(Color(0xFF7C3AED), Color(0xFF6D28D9)),
        highlights = listOf("Z-axis rotation", "Cube fold 90°", "Card flips")
    ),
    ShowcasePageData(
        title = "Multi-Layer Parallax",
        subtitle = "Differential speed ratios between background and foreground",
        icon = Icons.Default.Layers,
        tag = "Immersion",
        gradientColors = listOf(Color(0xFF059669), Color(0xFF047857)),
        highlights = listOf("0.5x offset speed", "Depth planes", "Floating badges")
    ),
    ShowcasePageData(
        title = "Elastic Scaling",
        subtitle = "Harmonic scale transformations anchored to card geometry",
        icon = Icons.Default.Speed,
        tag = "Dynamics",
        gradientColors = listOf(Color(0xFFD97706), Color(0xFFB45309)),
        highlights = listOf("Accordion fold", "Depth zoom", "Margin shrinking")
    ),
    ShowcasePageData(
        title = "Production Ready",
        subtitle = "Pure Jetpack Compose graphicsLayer with hardware acceleration",
        icon = Icons.Default.Stars,
        tag = "Architecture",
        gradientColors = listOf(Color(0xFFE11D48), Color(0xFFBE123C)),
        highlights = listOf("Zero allocations", "Modifier extension", "M3 design")
    )
)

@Composable
fun PageItemContent(
    pageIndex: Int,
    positionOffset: Float,
    isModernTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val pageNum = pageIndex + 1
    val baseColor = PageColors[pageIndex % PageColors.size]

    if (!isModernTheme) {
        // Classic ViewPager UI (faithfully mirroring original ToxicBakery / daimajia fragment_main)
        ClassicPageView(
            pageNum = pageNum,
            bgColor = baseColor,
            modifier = modifier
        )
    } else {
        // Modernized Material 3 showcase card with parallax layer support
        ModernPageView(
            pageIndex = pageIndex,
            pageNum = pageNum,
            positionOffset = positionOffset,
            baseColor = baseColor,
            modifier = modifier
        )
    }
}

@Composable
private fun ClassicPageView(
    pageNum: Int,
    bgColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .shadow(16.dp, RoundedCornerShape(24.dp))
            .testTag("classic_page_$pageNum"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = pageNum.toString(),
                    fontSize = 110.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "PAGE $pageNum OF 5",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
        }
    }
}

@Composable
private fun ModernPageView(
    pageIndex: Int,
    pageNum: Int,
    positionOffset: Float,
    baseColor: Color,
    modifier: Modifier = Modifier
) {
    val pageData = ShowcasePages[pageIndex % ShowcasePages.size]

    Card(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp)
            .shadow(20.dp, RoundedCornerShape(28.dp))
            .testTag("modern_page_$pageNum"),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            pageData.gradientColors.first(),
                            pageData.gradientColors.last().copy(alpha = 0.85f),
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
                .padding(24.dp)
        ) {
            // Parallax floating decorative element
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset {
                        // Internal parallax shift
                        IntOffset(x = (-positionOffset * 120).roundToInt(), y = 0)
                    }
                    .size(90.dp)
                    .background(Color.White.copy(alpha = 0.12f), CircleShape)
                    .border(1.dp, Color.White.copy(alpha = 0.25f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = pageData.icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(44.dp)
                )
            }

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top header
                Column {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = Color.White.copy(alpha = 0.2f),
                        modifier = Modifier.border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(50))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = pageData.tag,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = pageData.title,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        lineHeight = 38.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = pageData.subtitle,
                        fontSize = 15.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        lineHeight = 22.sp
                    )
                }

                // Middle badge
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .offset {
                            // Sub-element parallax
                            IntOffset(x = (-positionOffset * 70).roundToInt(), y = 0)
                        }
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.25f))
                        .border(2.dp, Color.White.copy(alpha = 0.4f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "0$pageNum",
                            fontSize = 38.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text(
                            text = "/ 05",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White.copy(alpha = 0.75f)
                        )
                    }
                }

                // Bottom feature chips
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "CAPABILITIES",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        pageData.highlights.forEach { highlight ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                                shadowElevation = 2.dp,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = highlight,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                    maxLines = 2
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
