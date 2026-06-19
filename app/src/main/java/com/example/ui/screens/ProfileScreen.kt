package com.example.ui.screens

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.User
import com.example.data.model.SavedCalculation
import com.example.data.model.ActivityLog
import com.example.ui.components.*
import com.example.ui.viewmodel.AuthViewModel
import com.example.ui.viewmodel.AcademicViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    academicViewModel: AcademicViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val scrollState = rememberScrollState()

    val activeUser by authViewModel.activeUser.collectAsState()
    val savedCalcs by academicViewModel.savedCalculations.collectAsState()
    val userLogs by academicViewModel.userLogs.collectAsState()

    var showEditProfile by remember { mutableStateOf(false) }
    var editFullName by remember { mutableStateOf(activeUser?.fullName ?: "") }
    var editPhone by remember { mutableStateOf(activeUser?.phone ?: "") }

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
                        text = "STUDENT DOSSIER",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Sky400 else Blue600
                    )
                    Text(
                        text = "My Personal Profile",
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            shape = RoundedCornerShape(32.dp),
                            colors = CardDefaults.cardColors(containerColor = if (isDark) Sky400 else Blue600),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(
                                    text = (activeUser?.fullName?.take(2) ?: "KU").uppercase(),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 20.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = activeUser?.fullName ?: "Unknown Student",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isDark) Color.White else Slate900
                        )
                        Text(
                            text = "Batch ${activeUser?.batch ?: "Unknown"} | ${activeUser?.discipline ?: "Computer Science"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }

                    IconButton(onClick = {
                        editFullName = activeUser?.fullName ?: ""
                        editPhone = activeUser?.phone ?: ""
                        showEditProfile = true
                    }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Edit Profile",
                            tint = if (isDark) Sky400 else Blue600
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Divider(color = if (isDark) Color(0x19FFFFFF) else Color(0x0F000000))

                Spacer(modifier = Modifier.height(12.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Email Address:", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                        Text(text = activeUser?.email ?: "ku@domain.edu", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = if (isDark) Color.White else Slate900)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Phone Number:", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                        Text(text = activeUser?.phone ?: "01XXX-XXXXXX", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = if (isDark) Color.White else Slate900)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "School Location:", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                        Text(text = activeUser?.school?.replace("School of ", "") ?: "Science", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = if (isDark) Color.White else Slate900)
                    }
                }
            }

            Text(
                text = "SAVED PREDICTIONS / GRADE CALCULATIONS",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.ExtraBold,
                color = if (isDark) Color.LightGray else Color.Gray,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 12.dp, top = 8.dp)
            )

            if (savedCalcs.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0x08FFFFFF) else Color(0x14FFFFFF))
                ) {
                    Text(
                        text = "No saved carry exam calculations yet.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.LightGray,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                savedCalcs.forEach { calc ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF1E293B) else Color.White),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .padding(4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Card(
                                    shape = RoundedCornerShape(18.dp),
                                    colors = CardDefaults.cardColors(containerColor = if (calc.courseType == "LAB") Color(0xFF10B981) else Sky400)
                                ) {
                                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = if (calc.courseType == "LAB") Icons.Default.Settings else Icons.Default.Create,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = calc.courseName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color.White else Slate900
                                )
                                Text(
                                    text = "Type: ${calc.courseType} | Saved Grade: ${calc.calculatedGrade ?: calc.targetGrade ?: "N/A"}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }

                            IconButton(onClick = { academicViewModel.deleteSavedCalc(calc.id) }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete saved calculation",
                                    tint = Color.Red,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            Text(
                text = "MY DETAILED SECURITY AUDIT LOGS",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.ExtraBold,
                color = if (isDark) Color.LightGray else Color.Gray,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 12.dp, top = 8.dp)
            )

            GlassmorphicCard(
                isDark = isDark,
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
            ) {
                if (userLogs.isEmpty()) {
                    Text(
                        text = "No recorded activity audits.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.LightGray
                    )
                } else {
                    userLogs.forEach { log ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = log.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isDark) Color.White else Slate900
                                )
                                Text(
                                    text = log.activityType,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Gray,
                                    fontSize = 9.sp
                                )
                            }
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

    if (showEditProfile) {
        AlertDialog(
            onDismissRequest = { showEditProfile = false },
            title = { Text("Update Public Profile", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = editFullName,
                        onValueChange = { editFullName = it },
                        label = { Text("Full Name") },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                    )

                    OutlinedTextField(
                        value = editPhone,
                        onValueChange = { editPhone = it },
                        label = { Text("Phone Number") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val user = activeUser
                        if (user != null) {
                            authViewModel.updateProfile(
                                fullName = editFullName,
                                email = user.email,
                                phone = editPhone,
                                school = user.school,
                                discipline = user.discipline,
                                batch = user.batch
                            )
                        }
                        showEditProfile = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = if (isDark) Sky400 else Blue600)
                ) {
                    Text("Save Changes", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfile = false }) {
                    Text("Close")
                }
            }
        )
    }
}
