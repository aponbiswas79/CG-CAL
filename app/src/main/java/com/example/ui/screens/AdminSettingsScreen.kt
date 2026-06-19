package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GradeScale
import com.example.ui.components.*
import com.example.ui.viewmodel.AuthViewModel
import com.example.ui.viewmodel.AcademicViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminSettingsScreen(
    authViewModel: AuthViewModel,
    academicViewModel: AcademicViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val scrollState = rememberScrollState()

    val gradeScales by academicViewModel.gradeScales.collectAsState()
    val adminLogs by academicViewModel.adminLogs.collectAsState()
    val exportedFileUri by authViewModel.exportedSpreadsheetUri.collectAsState()

    val users by authViewModel.allUsers.collectAsState()
    var userQuery by remember { mutableStateOf("") }

    val usersSearchResult = if (userQuery.trim().isEmpty()) {
        users
    } else {
        users.filter { 
            it.fullName.contains(userQuery, ignoreCase = true) || 
            it.email.contains(userQuery, ignoreCase = true) 
        }
    }

    var showEditThresholdDialog by remember { mutableStateOf<GradeScale?>(null) }
    var editMinMark by remember { mutableStateOf("") }
    var editMaxMark by remember { mutableStateOf("") }
    var editPoint by remember { mutableStateOf("") }

    val dateFormatter = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())

    AppGradientBackground(isDark = isDark) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = if (isDark) Sky400 else Blue600
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "ADMIN CONTROLS",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Sky400 else Blue600
                    )
                    Text(
                        text = "University Settings Management",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isDark) Color.White else Slate900
                    )
                }
            }

            GlassmorphicCard(
                isDark = isDark,
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                Text(
                    text = "ACADEMIC DATABASE INTEGRATIONS",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Sky400 else Blue600,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Text(
                    text = "Sync registered users & grade distributions directly into a local backup CSV spreadsheet.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Button(
                    onClick = { authViewModel.exportDatabaseAsExcel() },
                    colors = ButtonDefaults.buttonColors(containerColor = if (isDark) Color(0xFF10B981) else Color(0xFF059669)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("GENERATE REGISTERED USERS SHEET (.CSV)", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                }

                if (exportedFileUri != null) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0x3310B981)),
                        modifier = Modifier.padding(top = 12.dp).fillMaxWidth()
                    ) {
                        Text(
                            text = "Spreadsheet exported & appended to secure storage: ${exportedFileUri}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF10B981),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }

            Text(
                text = "GRADE SCALE SYSTEM MANAGEMENT",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color.LightGray else Color.Gray,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            GlassmorphicCard(
                isDark = isDark,
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Current KU Grading Thresholds",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color.White else Slate900
                    )

                    TextButton(onClick = { academicViewModel.resetGradeScales() }) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Reset Defaults", fontSize = 11.sp, color = Color.Red)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                gradeScales.forEach { scale ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Grade: ${scale.grade}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color.White else Slate900
                            )
                            Text(
                                text = "Marks Interval: ${scale.minMark} till ${scale.maxMark} | Points: ${scale.gradePoint} GPA",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }

                        IconButton(
                            onClick = {
                                editMinMark = scale.minMark.toString()
                                editMaxMark = scale.maxMark.toString()
                                editPoint = scale.gradePoint.toString()
                                showEditThresholdDialog = scale
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Edit Grade Scale",
                                tint = if (isDark) Sky400 else Blue600,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Text(
                text = "USER ACCOUNTS INTELLIGENCE & AUDITS",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color.LightGray else Color.Gray,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            GlassmorphicCard(
                isDark = isDark,
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                Text(
                    text = "Global Security Audit Trail Logs",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color.White else Slate900,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                adminLogs.take(15).forEach { log ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = log.activityType,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (log.activityType.contains("CSV") || log.activityType.contains("SUCCESS")) Color(0xFF10B981) else (if (isDark) Sky400 else Blue600)
                            )
                            Text(
                                text = dateFormatter.format(Date(log.timestamp)),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray,
                                fontSize = 9.sp
                            )
                        }
                        Text(
                            text = log.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isDark) Color.White else Slate900,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Divider(color = if (isDark) Color(0x0CFFFFFF) else Color(0x08000000))
                    }
                }
            }

            Text(
                text = "USER REGISTER & SEARCH INDEX",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color.LightGray else Color.Gray,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            GlassmorphicCard(
                isDark = isDark,
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
            ) {
                OutlinedTextField(
                    value = userQuery,
                    onValueChange = { userQuery = it },
                    label = { Text("Search Students by Name or Email") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                )

                if (usersSearchResult.isEmpty()) {
                    Text(
                        text = "No recorded KU scholar matches your query.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.LightGray
                    )
                } else {
                    usersSearchResult.forEach { foundUser ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .background(if (isDark) Color(0x0CFFFFFF) else Color(0x06000000), RoundedCornerShape(8.dp))
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = foundUser.fullName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color.White else Slate900
                                )
                                Text(
                                    text = "Email: ${foundUser.email} / Discipline: ${foundUser.discipline}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .background(if (foundUser.isLoggedIn) Color(0x2610B981) else Color(0x26EF4444), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (foundUser.isLoggedIn) "ONLINE" else "OFFLINE",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (foundUser.isLoggedIn) Color(0xFF10B981) else Color(0xFFEF4444)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showEditThresholdDialog != null) {
        val targetScale = showEditThresholdDialog!!
        AlertDialog(
            onDismissRequest = { showEditThresholdDialog = null },
            title = { Text("Manage Grade Threshold (${targetScale.grade})", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = editMinMark,
                        onValueChange = { editMinMark = it },
                        label = { Text("Minimum Required Mark") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                    )

                    OutlinedTextField(
                        value = editMaxMark,
                        onValueChange = { editMaxMark = it },
                        label = { Text("Maximum Required Mark") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                    )

                    OutlinedTextField(
                        value = editPoint,
                        onValueChange = { editPoint = it },
                        label = { Text("Academic Grade Point (GP)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val min = editMinMark.toDoubleOrNull()
                        val max = editMaxMark.toDoubleOrNull()
                        val gp = editPoint.toDoubleOrNull()
                        if (min != null && max != null && gp != null) {
                            academicViewModel.updateGradeThreshold(targetScale, min, max, gp)
                        }
                        showEditThresholdDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = if (isDark) Sky400 else Blue600)
                ) {
                    Text("Save Changes", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditThresholdDialog = null }) {
                    Text("Close")
                }
            }
        )
    }
}
