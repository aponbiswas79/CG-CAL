package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.User
import com.example.data.model.Semester
import com.example.data.model.Course
import com.example.ui.components.*
import com.example.ui.viewmodel.AuthViewModel
import com.example.ui.viewmodel.AcademicViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    authViewModel: AuthViewModel,
    academicViewModel: AcademicViewModel,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val scrollState = rememberScrollState()

    val activeUser by authViewModel.activeUser.collectAsState()
    val semesters by academicViewModel.semesters.collectAsState()
    val userLogs by academicViewModel.userLogs.collectAsState()

    var totalEarnedCredits = 0.0
    var cumulativeGpaSum = 0.0

    semesters.forEach { semester ->
        totalEarnedCredits += semester.totalCredits
        cumulativeGpaSum += (semester.gpa * semester.totalCredits)
    }

    val overallCgpa = if (totalEarnedCredits > 0) cumulativeGpaSum / totalEarnedCredits else 0.00
    
    val standing = when {
        overallCgpa >= 3.75 -> "Outstanding (Dean's List) 🏅"
        overallCgpa >= 3.50 -> "Excellent Standing 👍"
        overallCgpa >= 3.00 -> "Satisfactory 🟢"
        overallCgpa >= 2.20 -> "Passed / Good ⚠️"
        overallCgpa > 0.0 -> "Academic Warning 🚨"
        else -> "Fresh Candidate 🌱"
    }

    // Latest Semester GPA
    val currentSemesterGpa = if (semesters.isNotEmpty()) semesters.last().gpa else 3.75

    val totalGraduationCredits = 150.0
    val graduationProgress = (totalEarnedCredits / totalGraduationCredits).coerceIn(0.0, 1.0).toFloat()

    val dateFormatter = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())

    AppGradientBackground(isDark = isDark) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            
            // ==================== BENTO HEADER ====================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp, top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Side: Brand Logo and Title
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFF2563EB), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "KU",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "CG-CAL",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-0.5).sp
                        ),
                        color = if (isDark) Color.White else Slate900,
                        textDecoration = TextDecoration.Underline
                    )
                }

                // Right Side: Student Initials & Department
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${activeUser?.discipline ?: "CSE"} / BATCH ${activeUser?.batch ?: "24"}",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isDark) Sky400 else Blue600,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = activeUser?.fullName?.substringBefore(" ") ?: "Student",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color.LightGray else Slate700
                        )
                    }
                    
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .shadow(2.dp, CircleShape)
                            .background(if (isDark) Color(0xFF1E293B) else Color.White, CircleShape)
                            .border(2.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "User Avatar",
                            tint = if (isDark) Sky400 else Blue600,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // ==================== BENTO LARGE CARD: CUMULATIVE CGPA ====================
            BentoDarkCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .padding(bottom = 12.dp),
                isDark = isDark
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "CUMULATIVE CGPA",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF94A3B8),
                            letterSpacing = 1.5.sp
                        )
                        
                        // Emerald positive change pill
                        Box(
                            modifier = Modifier
                                .background(Color(0x2610B981), RoundedCornerShape(20.dp))
                                .border(1.dp, Color(0x3310B981), RoundedCornerShape(20.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "ACTIVE STANDING",
                                color = Color(0xFF34D399),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.Bottom,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = "%.2f".format(overallCgpa),
                            fontSize = 54.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            letterSpacing = (-2).sp
                        )
                        Text(
                            text = "/ 4.00",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF64748B),
                            modifier = Modifier.padding(bottom = 10.dp, start = 4.dp)
                        )
                    }

                    // Progress slider line inside Bento Card
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color(0xFF1E293B))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth((overallCgpa / 4.0).coerceIn(0.0, 1.0).toFloat())
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(Color(0xFF2563EB), Color(0xFF38BDF8))
                                        ),
                                        RoundedCornerShape(3.dp)
                                    )
                                    .shadow(elevation = 8.dp, clip = false)
                            )
                        }
                    }
                }
            }

            // ==================== BENTO ROW: STAT CARDS (SIDE BY SIDE) ====================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Item 1: Current GPA
                GlassmorphicCard(
                    isDark = isDark,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "CURRENT GPA",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "%.2f".format(currentSemesterGpa),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isDark) Color.White else Slate900,
                        letterSpacing = (-1).sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Latest Semester",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray,
                        fontSize = 11.sp
                    )
                }

                // Item 2: Earned Credits
                GlassmorphicCard(
                    isDark = isDark,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "EARNED CREDITS",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "%.1f".format(totalEarnedCredits),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isDark) Color.White else Slate900,
                            letterSpacing = (-1).sp
                        )
                        Text(
                            text = " / 150",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 4.dp, start = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Toward Graduation",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray,
                        fontSize = 11.sp
                    )
                }
            }

            // ==================== BENTO MEDIUM CARD: SMART GRADE PREDICTOR ====================
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isDark) Color(0x1A2563EB) else Color(0xFFEFF6FF)
                ),
                shape = RoundedCornerShape(24.dp),
                border = CardDefaults.outlinedCardBorder(enabled = true).copy(
                    brush = Brush.linearGradient(
                        listOf(
                            if (isDark) Color(0x2638BDF8) else Color(0xFFDBEAFE),
                            if (isDark) Color(0x0C38BDF8) else Color(0xFFEFF6FF)
                        )
                    )
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(Color(0xFF2563EB), RoundedCornerShape(6.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Grade Predictor",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color.White else Slate900
                            )
                        }
                        
                        Box(
                            modifier = Modifier
                                .background(if (isDark) Color(0x3338BDF8) else Color(0xFFDBEAFE), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "ACTIVE",
                                color = if (isDark) Sky400 else Blue600,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Targeting A+ next semester?",
                        color = if (isDark) Color.LightGray else Slate700,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(0.72f)
                                    .background(Color(0xFF10B981), RoundedCornerShape(4.dp))
                            )
                        }
                        Text(
                            text = "72% Prob.",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF10B981)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { onNavigate("GPA_CALC") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isDark) Color.White else Slate900,
                            contentColor = if (isDark) Slate900 else Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Run Simulation",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // ==================== BENTO FULL-WIDTH STATUS BAR ====================
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isDark) Color(0xFF1E293B) else Color.White
                ),
                shape = RoundedCornerShape(16.dp),
                border = CardDefaults.outlinedCardBorder(enabled = true).copy(
                    brush = androidx.compose.ui.graphics.SolidColor(if (isDark) Color(0x1AFFFFFF) else Color(0xFFE2E8F0))
                ),
                onClick = { onNavigate("PROFILE") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(Color(0xFF10B981), CircleShape)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = standing,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color.White else Slate700
                        )
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = if (isDark) Color.Gray else Color.LightGray,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // ==================== ACADEMIC CALCULATORS TITLE ====================
            Text(
                text = "ACADEMIC CALCULATORS",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Black,
                color = if (isDark) Color.LightGray else Color.Gray,
                letterSpacing = 1.5.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // ==================== BENTO GRID OF UTILITIES ====================
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickMenuCard(
                    title = "Theory",
                    subtitle = "Predict final",
                    icon = Icons.Default.Create,
                    color = if (isDark) Sky400 else Blue600,
                    onClick = { onNavigate("THEORY_CALC") },
                    isDark = isDark,
                    modifier = Modifier.weight(1f)
                )
                QuickMenuCard(
                    title = "Lab Exam",
                    subtitle = "Mark tracker",
                    icon = Icons.Default.Settings,
                    color = Color(0xFF10B981),
                    onClick = { onNavigate("LAB_CALC") },
                    isDark = isDark,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickMenuCard(
                    title = "GPA/CGPA",
                    subtitle = "Curate course",
                    icon = Icons.Default.Add,
                    color = Color(0xFFF59E0B),
                    onClick = { onNavigate("GPA_CALC") },
                    isDark = isDark,
                    modifier = Modifier.weight(1f)
                )
                QuickMenuCard(
                    title = "Profile ID",
                    subtitle = "Recent logs",
                    icon = Icons.Default.Person,
                    color = Color(0xFFD0BCFF),
                    onClick = { onNavigate("PROFILE") },
                    isDark = isDark,
                    modifier = Modifier.weight(1f)
                )
            }

            // ==================== RECENT ACTIVITIES BENTO ====================
            GlassmorphicCard(
                isDark = isDark,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "RECENT ACTIVITIES & AUDITS",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color.LightGray else Color.Gray,
                        letterSpacing = 1.sp
                    )
                    TextButton(onClick = { onNavigate("PROFILE") }) {
                        Text(
                            text = "View All", 
                            fontSize = 11.sp, 
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Sky400 else Blue600
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(6.dp))

                if (userLogs.isEmpty()) {
                    Text(
                        text = "No recent account transactions.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.LightGray,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                } else {
                    userLogs.take(3).forEach { log ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = when (log.activityType) {
                                    "LOGIN" -> Icons.Default.Lock
                                    "REGISTER" -> Icons.Default.Person
                                    "PROFILE_UPDATE" -> Icons.Default.Settings
                                    "CALC_SAVE" -> Icons.Default.Star
                                    else -> Icons.Default.Refresh
                                },
                                contentDescription = null,
                                tint = if (isDark) Sky400 else Blue600,
                                modifier = Modifier
                                    .size(16.dp)
                                    .padding(top = 2.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = log.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isDark) Color.White else Slate900,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = log.activityType,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Gray,
                                    fontSize = 9.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = dateFormatter.format(Date(log.timestamp)),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray,
                                fontSize = 9.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuickMenuCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) Color(0x0CFFFFFF) else Color(0x3BFFFFFF)
        ),
        shape = RoundedCornerShape(16.dp),
        border = CardDefaults.outlinedCardBorder(enabled = true).copy(
            brush = androidx.compose.ui.graphics.SolidColor(
                if (isDark) Color(0x12FFFFFF) else Color(0xFFE2E8F0)
            )
        ),
        modifier = modifier.height(76.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(color.copy(alpha = 0.12f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color.White else Slate900
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    fontSize = 10.sp
                )
            }
        }
    }
}
