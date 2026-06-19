package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Color Tokens matching exact branding: slate dark blue, blue secondary, cyan accent
val Slate900 = Color(0xFF0F172A)
val Slate800 = Color(0xFF1E293B)
val Slate700 = Color(0xFF334155)
val Slate100 = Color(0xFFF1F5F9)
val Blue600 = Color(0xFF2563EB)
val Sky400 = Color(0xFF38BDF8)
val GoldAccent = Color(0xFFEAB308)
val GlassWhite = Color(0x99FFFFFF)
val GlassDark = Color(0x660F172A)

@Composable
fun AppGradientBackground(
    isDark: Boolean,
    content: @Composable BoxScope.() -> Unit
) {
    val baseBg = if (isDark) Color(0xFF0F172A) else Color(0xFFF1F5F9)
    val gradientColors = if (isDark) {
        listOf(Color(0xFF0F172A), Color(0xFF090D1A), Color(0xFF020617))
    } else {
        listOf(Color(0xFFF1F5F9), Color(0xFFEBF2FA), Color(0xFFE2E8F0))
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(gradientColors))
    ) {
        // Draw the global mesh ambient radial glows
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Draw top-left radial glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        if (isDark) Color(0x143B82F6) else Color(0x0C3B82F6),
                        Color.Transparent
                    ),
                    center = Offset(0f, 0f),
                    radius = size.width * 0.7f
                )
            )
            // Draw bottom-right radial glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        if (isDark) Color(0x1406B6D4) else Color(0x0C06B6D4),
                        Color.Transparent
                    ),
                    center = Offset(size.width, size.height),
                    radius = size.width * 0.7f
                )
            )
        }
        content()
    }
}

@Composable
fun GlassmorphicCard(
    modifier: Modifier = Modifier,
    isDark: Boolean,
    elevation: Dp = 4.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    val bg = if (isDark) Color(0xE61E293B) else Color(0xF2FFFFFF)
    val borderCol = if (isDark) Color(0x1AFFFFFF) else Color(0xFFE2E8F0)
    
    Column(
        modifier = modifier
            .shadow(elevation, RoundedCornerShape(20.dp))
            .background(bg, RoundedCornerShape(20.dp))
            .border(1.dp, borderCol, RoundedCornerShape(20.dp))
            .padding(18.dp)
    ) {
        content()
    }
}

@Composable
fun BentoDarkCard(
    modifier: Modifier = Modifier,
    isDark: Boolean,
    content: @Composable BoxScope.() -> Unit
) {
    val borderCol = if (isDark) Color(0x1AFFFFFF) else Color(0xFF1E293B).copy(alpha = 0.15f)
    Box(
        modifier = modifier
            .shadow(6.dp, RoundedCornerShape(24.dp))
            .background(Color(0xFF0F172A), RoundedCornerShape(24.dp))
            .border(1.dp, borderCol, RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(24.dp))
    ) {
        // Top-right blue radial glow inside the slate-900 bento box
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0x333B82F6),
                        Color.Transparent
                    ),
                    center = Offset(size.width, 0f),
                    radius = size.width * 0.6f
                )
            )
        }
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            content()
        }
    }
}

@Composable
fun CustomProgressBar(
    progress: Float, // 0.0 to 1.0
    color: Color,
    modifier: Modifier = Modifier
) {
    val animateProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 500),
        label = "ProgressBarAnimation"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(Color(0x33808080))
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(animateProgress)
                .background(color, RoundedCornerShape(4.dp))
        )
    }
}

// Custom radial performance meter for beautiful visualization
@Composable
fun PerformanceMeter(
    score: Double,
    maxScore: Double,
    isDark: Boolean,
    modifier: Modifier = Modifier,
    scoreLabel: String = "Carry"
) {
    val percent = (score / maxScore).coerceIn(0.0, 1.0).toFloat()
    val animatedPercent by animateFloatAsState(
        targetValue = percent,
        animationSpec = tween(durationMillis = 800),
        label = "MeterAnimation"
    )

    val color = when {
        percent >= 0.95 -> Color(0xFF10B981) // Emerald (Outstanding)
        percent >= 0.875 -> Color(0xFF06B6D4) // Cyan (Good)
        percent >= 0.75 -> Color(0xFF3B82F6) // Blue (Fair)
        percent >= 0.625 -> Color(0xFFF59E0B) // Amber (Warning)
        else -> Color(0xFFEF4444) // Red (Danger)
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(140.dp)) {
            val strokeWidth = 14.dp.toPx()
            val canvasSize = size
            val center = Offset(canvasSize.width / 2, canvasSize.height / 2)
            val radius = (canvasSize.width - strokeWidth) / 2

            // Background Track Arch
            drawArc(
                color = if (isDark) Color(0x1FFFFFFF) else Color(0x1F000000),
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                size = Size(radius * 2, radius * 2),
                topLeft = Offset(center.x - radius, center.y - radius)
            )

            // Value Arc Progress
            drawArc(
                color = color,
                startAngle = 135f,
                sweepAngle = 270f * animatedPercent,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                size = Size(radius * 2, radius * 2),
                topLeft = Offset(center.x - radius, center.y - radius)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 10.dp)
        ) {
            Text(
                text = "%.2f".format(score),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = if (isDark) Color.White else Slate900,
                fontSize = 28.sp
            )
            Text(
                text = "/ ${"%.0f".format(maxScore)} $scoreLabel",
                style = MaterialTheme.typography.bodyMedium,
                color = if (isDark) Color.LightGray else Color.Gray,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// Gorgeous bar chart showing course grade distribution dynamically!
@Composable
fun GradeDistributionChart(
    gradeCounts: Map<String, Int>,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val orderedGrades = listOf("A+", "A", "A-", "B+", "B", "B-", "C+", "C", "D", "F")
    val values = orderedGrades.map { gradeCounts[it] ?: 0 }
    val maxVal = (values.maxOrNull() ?: 0).coerceAtLeast(1)

    Column(modifier = modifier) {
        Text(
            text = "GRADE DISTRIBUTION CHART",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = if (isDark) Sky400 else Blue600,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            orderedGrades.forEachIndexed { index, grade ->
                val count = gradeCounts[grade] ?: 0
                val heightPercent = count.toFloat() / maxVal

                val barColor = when (grade) {
                    "A+", "A", "A-" -> Color(0xFF10B981)
                    "B+", "B", "B-" -> Color(0xFF3B82F6)
                    "C+", "C", "D" -> Color(0xFFF59E0B)
                    else -> Color(0xFFEF4444)
                }

                val animatedPercent by animateFloatAsState(
                    targetValue = heightPercent,
                    animationSpec = tween(durationMillis = 600, delayMillis = index * 50),
                    label = "BarChartAnimation"
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        if (count > 0) {
                            Text(
                                text = "$count",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color.LightGray else Color.DarkGray,
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.6f)
                                .fillMaxHeight(animatedPercent.coerceIn(0.01f, 1.0f))
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                .background(barColor)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = grade,
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isDark) Color.White else Slate900,
                        maxLines = 1,
                        overflow = TextOverflow.Clip
                    )
                }
            }
        }
    }
}
