package com.example.ui.screens

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
import com.example.data.model.User
import com.example.ui.components.*
import com.example.ui.viewmodel.AcademicViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabCalculatorScreen(
    user: User,
    academicViewModel: AcademicViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val scrollState = rememberScrollState()

    val courseName by academicViewModel.labCourseName.collectAsState()
    val attMarks by academicViewModel.labAttendance.collectAsState()
    val vivaMarks by academicViewModel.labViva.collectAsState()
    val finalMarks by academicViewModel.labFinal.collectAsState()

    val calcResult by academicViewModel.labCalcResult.collectAsState()

    val attendance = attMarks.toDoubleOrNull() ?: 0.0
    val viva = vivaMarks.toDoubleOrNull() ?: 0.0
    val finalExam = finalMarks.toDoubleOrNull() ?: 0.0
    val totalScore = attendance + viva + finalExam

    LaunchedEffect(attMarks, vivaMarks, finalMarks) {
        academicViewModel.calculateLabResult()
    }

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
                        text = "LAB / SESSIONAL",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Sky400 else Blue600
                    )
                    Text(
                        text = "Practical Course Evaluator",
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
                    text = "LAB PROFILE",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Sky400 else Blue600,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                OutlinedTextField(
                    value = courseName,
                    onValueChange = { academicViewModel.labCourseName.value = it },
                    label = { Text("Course Name or Code (e.g. CSE-3102)") },
                    leadingIcon = { Icon(Icons.Default.Settings, contentDescription = null, tint = if (isDark) Sky400 else Blue600) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            GlassmorphicCard(
                isDark = isDark,
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                Text(
                    text = "LAB / VIVA / ATTENDANCE ASSESSMENT",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Sky400 else Blue600,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Text(
                    text = "Lab Attendance (Max 10)",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color.LightGray else Slate700,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                OutlinedTextField(
                    value = attMarks,
                    onValueChange = { academicViewModel.labAttendance.value = it },
                    label = { Text("Attendance Marks (0 - 10)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp),
                    singleLine = true
                )

                Text(
                    text = "Lab Viva & Continuous Quiz (Max 30)",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color.LightGray else Slate700,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                OutlinedTextField(
                    value = vivaMarks,
                    onValueChange = { academicViewModel.labViva.value = it },
                    label = { Text("Viva & Notebook Score (0 - 30)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp),
                    singleLine = true
                )

                Text(
                    text = "Practical Final Semester Exam (Max 60)",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color.LightGray else Slate700,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                OutlinedTextField(
                    value = finalMarks,
                    onValueChange = { academicViewModel.labFinal.value = it },
                    label = { Text("Practical Final Score (0 - 60)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            GlassmorphicCard(
                isDark = isDark,
                modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)
            ) {
                Text(
                    text = "LAB PERFORMANCE OUTCOME",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color.LightGray else Color.Gray,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "TOTAL ASSESSED",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                        Text(
                            text = "${"%.1f".format(totalScore)} / 100.0",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isDark) Color.White else Slate900
                        )

                        if (calcResult != null) {
                            val (g, gp) = calcResult!!
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                                Icon(
                                    imageVector = if (g == "F") Icons.Default.Close else Icons.Default.Check,
                                    contentDescription = null,
                                    tint = if (g == "F") Color.Red else Color(0xFF10B981),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Computed Grade: $g ($gp GP)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (g == "F") Color.Red else Color(0xFF10B981)
                                )
                            }
                        }
                    }

                    Box(modifier = Modifier.size(90.dp), contentAlignment = Alignment.Center) {
                        PerformanceMeter(
                            score = totalScore,
                            maxScore = 100.0,
                            isDark = isDark,
                            scoreLabel = "Marks",
                            modifier = Modifier.size(84.dp)
                        )
                    }
                }

                CustomProgressBar(
                    progress = (totalScore / 100.0).toFloat(),
                    color = if (totalScore >= 50.0) Color(0xFF10B981) else Color(0xFFEF4444)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        academicViewModel.saveLabCalculation()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = if (isDark) Sky400 else Color(0xFF10B981)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    enabled = calcResult != null
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("SAVE EVALUATION HISTORY", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(imageVector = Icons.Default.Star, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}
